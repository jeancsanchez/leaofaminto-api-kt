package com.github.jeancsanchez.leaofaminto.view.services

import com.github.jeancsanchez.leaofaminto.data.*
import com.github.jeancsanchez.leaofaminto.domain.model.*
import com.github.jeancsanchez.leaofaminto.view.formatStringBRFromTimeStamp
import com.github.jeancsanchez.leaofaminto.view.services.exceptions.ImporterException
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * @author @jeancsanchez
 * @created 15/01/2022
 * Jesus loves you.
 */

@Service
@Qualifier("passfolioImporter")
class PassfolioImporterServiceImpl(
    @Autowired val operacaoRepository: OperacaoRepository,
    @Autowired val comprasRepository: ComprasRepository,
    @Autowired val vendasRepository: VendasRepository,
    @Autowired val corretoraRepository: CorretoraRepository,
    @Autowired val ativoRepository: AtivoRepository
) : FileImporterService {

    companion object {
        const val BUY = "BUY"
        const val SELL = "SELL"
    }


    override fun execute(param: MultipartFile): List<Operacao> {
        val resultList = mutableListOf<Operacao>()

        val linhaCabecalhos = 0
        val colunaDataOperacao = 1
        val colunaStatus = 3
        val colunaTipoOperacao = 4
        val colunaCodigoAtivo = 6
        val colunaQuantidade = 7
        val colunaPrecoOperacao = 10

        val fileReader = BufferedReader(InputStreamReader(param.inputStream, "UTF-8"))
        val csvParser = CSVParser(fileReader, CSVFormat.DEFAULT)

        csvParser.records.forEachIndexed rowsLooping@{ rowIndex, row ->
            var currentColumnIndex = 0
            try {
                if (rowIndex > linhaCabecalhos && row.get(colunaStatus).toString().equals("filled", true)) {
                    var ativo: Ativo? = null
                    val corretora = corretoraRepository.findTop1ByNomeIgnoreCase("Passfolio")

                    currentColumnIndex = colunaDataOperacao
                    val dataOperacao = row.get(colunaDataOperacao).formatStringBRFromTimeStamp()

                    currentColumnIndex = colunaCodigoAtivo
                    row.get(colunaCodigoAtivo).let { codigoAtivo ->
                        ativo = ativoRepository
                            .findTop1ByNomeIgnoreCase(codigoAtivo)
                            ?: ativoRepository.save(
                                Ativo(
                                    codigo = codigoAtivo,
                                    tipoDeAtivo = TipoDeAtivo.STOCK
                                )
                            )
                    }

                    currentColumnIndex = colunaQuantidade
                    val quantidade = row.get(colunaQuantidade).toDouble()

                    currentColumnIndex = colunaPrecoOperacao
                    val preco = row.get(colunaPrecoOperacao).toDouble()

                    if (row.get(colunaTipoOperacao) == BUY) {
                        Compra(
                            data = dataOperacao,
                            ativo = ativo!!,
                            corretora = corretora!!,
                            preco = preco,
                            quantidade = quantidade
                        ).also { resultList.add(it) }

                    } else {
                        Venda(
                            data = dataOperacao,
                            ativo = ativo!!,
                            corretora = corretora!!,
                            preco = preco,
                            quantidade = quantidade
                        ).also { resultList.add(it) }
                    }
                }
            } catch (t: Throwable) {
                throw ImporterException(
                    cause = t,
                    line = rowIndex,
                    columnIndex = currentColumnIndex
                )
            }
        }

        return resultList.updateRepository()
    }

    /**
     * Atualiza o [OperacaoRepository]. Os registros não são substituídos, pois, pode haver
     * operações iguais no mesmo dia, ficando impossível saber se é o mesmo registro.
     */
    private fun MutableList<Operacao>.updateRepository(): List<Operacao> {
        if (isNotEmpty()) {
            operacaoRepository.deleteAllInBatchByCorretoraNomeIgnoreCase("Passfolio")
            operacaoRepository.saveAll(this)
        }

        return this
    }
}
