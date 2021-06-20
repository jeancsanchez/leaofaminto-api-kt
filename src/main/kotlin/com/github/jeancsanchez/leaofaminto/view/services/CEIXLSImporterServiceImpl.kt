package com.github.jeancsanchez.leaofaminto.view.services

import com.github.jeancsanchez.leaofaminto.data.*
import com.github.jeancsanchez.leaofaminto.domain.model.*
import com.github.jeancsanchez.leaofaminto.view.extractCodigoAtivo
import com.github.jeancsanchez.leaofaminto.view.extractTipoDeAtivo
import com.github.jeancsanchez.leaofaminto.view.formatStringBRToDate
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 09/06/2021
 * Jesus loves you.
 */

@Service
class CEIXLSImporterServiceImpl(
    @Autowired val operacaoRepository: OperacaoRepository,
    @Autowired val comprasRepository: ComprasRepository,
    @Autowired val vendasRepository: VendasRepository,
    @Autowired val corretoraRepository: CorretoraRepository,
    @Autowired val ativoRepository: AtivoRepository
) : CEIXLSImporterService {

    override fun execute(param: MultipartFile): List<Operacao> {
        val resultList = mutableListOf<Operacao>()

        val workbook = HSSFWorkbook(param.inputStream)
        val currentSheet = workbook.getSheetAt(0)
        val linhaCabecalhos = 10
        val colunaDataOperacao = 1
        val colunaTipoOperacao = 3
        val colunaCodigoAtivo = 6
        val colunaTipoDoPapel = 7
        val colunaQuantidade = 8
        val colunaPrecoOperacao = 9

        val corretora = currentSheet.getRow(9).getCell(1).stringCellValue.run {
            corretoraRepository.findTop1ByNomeIgnoreCase(this) ?: corretoraRepository.save(Corretora(nome = this))
        }

        var dataOperacao: LocalDate = LocalDate.now()
        var ativo: Ativo? = null
        var tipoDeAtivo: TipoDeAtivo
        var tipoOperacao = ""
        var quantidade = 0
        var preco = 0.0

        currentSheet.rowIterator().withIndex().forEach rowsLooping@{ row ->
            if (row.index > linhaCabecalhos) {
                row.value.cellIterator().forEach columnsLooping@{ column ->
                    when (column.columnIndex) {
                        colunaDataOperacao -> {
                            dataOperacao = column.stringCellValue.formatStringBRToDate() ?: let {
                                return@rowsLooping
                            }
                        }

                        colunaCodigoAtivo -> {
                            val codigoAtivo = column.stringCellValue.extractCodigoAtivo()
                            ativo = ativoRepository.findTop1ByNomeIgnoreCase(codigoAtivo)
                                ?: ativoRepository.save(Ativo(codigo = codigoAtivo))
                        }

                        colunaTipoDoPapel -> {
                            tipoDeAtivo = column.stringCellValue.extractTipoDeAtivo()
                        }

                        colunaQuantidade -> {
                            quantidade = column.numericCellValue.toInt()
                        }

                        colunaPrecoOperacao -> {
                            preco = column.numericCellValue
                        }

                        colunaTipoOperacao -> {
                            tipoOperacao = column.stringCellValue.trim()
                        }
                    }
                }

                val operacao = if (tipoOperacao.equals("V", true)) {
                    Venda(
                        data = dataOperacao,
                        ativo = ativo!!,
                        corretora = corretora,
                        preco = preco,
                        quantidade = quantidade
                    )
                } else {
                    Compra(
                        data = dataOperacao,
                        ativo = ativo!!,
                        corretora = corretora,
                        preco = preco,
                        quantidade = quantidade
                    )
                }

                resultList.add(operacao)
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
