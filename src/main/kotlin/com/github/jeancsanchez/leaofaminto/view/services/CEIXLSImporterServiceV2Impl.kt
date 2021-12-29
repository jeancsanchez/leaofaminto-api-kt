package com.github.jeancsanchez.leaofaminto.view.services

import com.github.jeancsanchez.leaofaminto.data.*
import com.github.jeancsanchez.leaofaminto.domain.model.*
import com.github.jeancsanchez.leaofaminto.view.*
import com.github.jeancsanchez.leaofaminto.view.services.exceptions.ImporterException
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 09/06/2021
 * Jesus loves you.
 */

@Service
@Qualifier("CEIImporterV2")
class CEIXLSImporterServiceV2Impl(
    @Autowired val operacaoRepository: OperacaoRepository,
    @Autowired val comprasRepository: ComprasRepository,
    @Autowired val vendasRepository: VendasRepository,
    @Autowired val corretoraRepository: CorretoraRepository,
    @Autowired val ativoRepository: AtivoRepository
) : CEIXLSImporterService {

    companion object {
        // Credito | Transferência - Liquidação ---> significa que é uma compra
        // Debito | Transferência - Liquidação ---> significa que é uma venda
        // Any | Transferência ---> significa nada

        const val CREDITO = 1
        const val DEBITO = 2
        const val TRANSFERENCIA_LIQUIDACAO = 3
        const val RENDIMENTO = 4
        const val DIVIDENDO = 5
    }


    override fun execute(param: MultipartFile): List<Operacao> {
        val resultList = mutableListOf<Operacao>()

        val workbook = XSSFWorkbook(param.inputStream)
        val currentSheet = workbook.getSheetAt(0)

        val linhaCabecalhos = 0
        val colunaEntradaSaida = 0
        val colunaDataOperacao = 1
        val colunaMovimentacao = 2
        val colunaCodigoAtivo = 3
        val colunaCorretora = 4
        val colunaQuantidade = 5
        val colunaPrecoOperacao = 6

        var dataOperacao: LocalDate = LocalDate.now()
        var ativo: Ativo? = null
        var tipoDeAtivo: TipoDeAtivo
        var entradaSaida = -1
        var movimentacao = -1
        var quantidade = 0
        var preco = 0.0

        currentSheet.rowIterator().withIndex().forEach rowsLooping@{ row ->
            var corretora: Corretora? = null
            var currColumnIndex = 0

            try {
                if (row.index > linhaCabecalhos) {
                    row.value.cellIterator().run {
                        while (hasNext()) {
                            forEach columnsLooping@{ column ->
                                currColumnIndex = column.columnIndex

                                when (currColumnIndex) {
                                    colunaMovimentacao -> {
                                        val value = column.stringCellValue.trim()

                                        movimentacao =
                                            if (value.equals("Transferência - Liquidação", ignoreCase = true)) {
                                                TRANSFERENCIA_LIQUIDACAO
                                            } else {
                                                return@rowsLooping
                                            }
                                    }

                                    colunaDataOperacao -> {
                                        dataOperacao = column.stringCellValue.formatStringBRToDate() ?: let {
                                            return@rowsLooping
                                        }
                                    }

                                    colunaCorretora -> {
                                        corretora = column.stringCellValue.run {
                                            if (isNullOrEmpty()) {
                                                return@rowsLooping
                                            }

                                            corretoraRepository.findTop1ByNomeIgnoreCase(extractNomeCorretora())
                                        }
                                    }

                                    colunaCodigoAtivo -> {
                                        tipoDeAtivo = column.stringCellValue.extractTipoDeAtivo()

                                        column
                                            .stringCellValue
                                            .extractCodigoAtivoV2()
                                            .also { codigoAtivo ->
                                                ativo = ativoRepository
                                                    .findTop1ByNomeIgnoreCase(codigoAtivo)
                                                    ?: ativoRepository.save(
                                                        Ativo(
                                                            codigo = codigoAtivo,
                                                            tipoDeAtivo = tipoDeAtivo
                                                        )
                                                    )
                                            }
                                    }

                                    colunaQuantidade -> {
                                        quantidade = try {
                                            column.stringCellValue.toInt()
                                        } catch (e: IllegalStateException) {
                                            column.numericCellValue.toInt()
                                        }
                                    }

                                    colunaPrecoOperacao -> {
                                        preco = column.numericCellValue
                                    }

                                    colunaEntradaSaida -> {
                                        val value = column.stringCellValue.trim()
                                        entradaSaida = if (value.equals("credito", ignoreCase = true)) {
                                            CREDITO
                                        } else {
                                            DEBITO
                                        }
                                    }
                                }
                            }

                            if (corretora !== null && movimentacao == TRANSFERENCIA_LIQUIDACAO) {
                                if (entradaSaida == CREDITO) {
                                    Compra(
                                        data = dataOperacao,
                                        ativo = ativo!!,
                                        corretora = corretora!!,
                                        preco = preco,
                                        quantidade = quantidade
                                    ).also { resultList.add(it) }

                                } else if (entradaSaida == DEBITO) {
                                    Venda(
                                        data = dataOperacao,
                                        ativo = ativo!!,
                                        corretora = corretora!!,
                                        preco = preco,
                                        quantidade = quantidade
                                    ).also { resultList.add(it) }
                                }
                            } else if (corretora == null) {
                                throw IllegalStateException("Corretora não cadastrada")
                            }
                        }
                    }
                }
            } catch (t: Throwable) {
                throw ImporterException(
                    cause = t,
                    line = row.value.rowNum,
                    columnIndex = currColumnIndex
                )
            }
        }

        return updateRepository(resultList)
    }

    /**
     * Atualiza o [OperacaoRepository] considerando os objetos já existentes para evitar duplicidades.
     *
     * @param resultList Lista com novos registros
     * @return Lista de operações atualizada.
     */
    private fun updateRepository(resultList: MutableList<Operacao>): List<Operacao> {
        if (resultList.isNotEmpty()) {
            if (operacaoRepository.count() == 0L) {
                operacaoRepository.saveAll(resultList)
            } else if (operacaoRepository.findTopByOrderByIdDesc()?.hashId !== resultList.last().hashId) {
                if (resultList.last().hashId !== operacaoRepository.findTopByOrderByIdDesc()?.hashId) {
                    val differenceList = resultList.subList(operacaoRepository.count().toInt(), resultList.size)
                    operacaoRepository.saveAll(differenceList)
                    return resultList
                }
            }
        }

        return resultList
    }
}
