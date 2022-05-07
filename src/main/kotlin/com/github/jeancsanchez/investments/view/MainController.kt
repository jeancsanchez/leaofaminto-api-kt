package com.github.jeancsanchez.investments.view

import com.github.jeancsanchez.investments.data.CorretoraRepository
import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.data.PapelRepository
import com.github.jeancsanchez.investments.domain.RelatorioService
import com.github.jeancsanchez.investments.domain.model.*
import com.github.jeancsanchez.investments.domain.model.dto.ConsolidadoDTO
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.time.LocalDate

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@RestController
@Configuration
@RequestMapping("/api")
class MainController(
    @Autowired val corretoraRepository: CorretoraRepository,
    @Autowired val operacaoRepository: OperacaoRepository,
    @Autowired val papelRepository: PapelRepository,
    @Autowired val relatorioService: RelatorioService
) {
    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/operacoes")
    fun listarOperacoes(): ResponseEntity<List<Operacao>> {
        return ResponseEntity.ok(operacaoRepository.findAll())
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping("/operacoes/consolidadas")
    fun listarOperacoesConsolidadas(): ResponseEntity<ConsolidadoDTO> {
        return ResponseEntity.ok(relatorioService.pegarOperacoesConsolidadas())
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/sync")
    fun syncOperacoes(@RequestParam("arquivo") arquivo: MultipartFile): ResponseEntity<List<Operacao>> {
        val resultList = mutableListOf<Operacao>()

        val workbook = HSSFWorkbook(arquivo.inputStream)
        val currentSheet = workbook.getSheetAt(0)
        val linhaCabecalhos = 10
        val colunaDataOperacao = 1
        val colunaTipoOperacao = 3
        val colunaCodenamePapel = 6
        val colunaTipoDoPapel = 7
        val colunaQuantidade = 8
        val colunaPrecoOperacao = 9

        val nomeDaCorretora = currentSheet.getRow(9).getCell(1).stringCellValue
        var dataOperacao: LocalDate? = null
        var tipoOperacao: TipoOperacao? = null
        var tipoDeLote: String = TipoDeLote.FRACIONARIO
        var papelCodename = ""
        var tipoPapel: TipoAcao? = null
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
                        colunaTipoOperacao -> {
                            tipoOperacao = column.stringCellValue.extractTipoOperacao()
                        }
                        colunaCodenamePapel -> {
                            papelCodename = column.stringCellValue.extractPapelName()
                            tipoDeLote = column.stringCellValue.extractTipoDeLote()
                        }
                        colunaTipoDoPapel -> {
                            tipoPapel = column.stringCellValue.extractTipoAcao()
                        }
                        colunaQuantidade -> {
                            quantidade = column.numericCellValue.toInt()
                        }
                        colunaPrecoOperacao -> {
                            preco = column.numericCellValue
                        }
                    }
                }

                val papel =
                    papelRepository.findTop1ByNomeIgnoreCase(papelCodename)
                        ?: papelRepository.save(Papel(codigo = papelCodename))
                val corretora = corretoraRepository.findTop1ByNomeIgnoreCase(nomeDaCorretora)
                    ?: corretoraRepository.save(Corretora(nome = nomeDaCorretora))

                val newOperacao = Operacao(
                    data = dataOperacao!!,
                    papel = papel,
                    corretora = corretora,
                    tipoDaOperacao = tipoOperacao!!,
                    tipoDaAcao = tipoPapel!!,
                    tipoDeLote = tipoDeLote,
                    preco = preco,
                    quantidade = quantidade
                )

                resultList.add(newOperacao)
            }
        }

        if (resultList.isNotEmpty()) {
            if (operacaoRepository.count() == 0L) {
                operacaoRepository.saveAll(resultList)
                return ResponseEntity.ok(resultList)
            }

            if (resultList.last().hashId !== operacaoRepository.findTopByOrderByIdDesc()?.hashId) {
                val lastIndexFromDB = operacaoRepository.count() - 1
                val differenceList = resultList.subList(lastIndexFromDB.toInt(), resultList.size - 1)
                operacaoRepository.saveAll(differenceList)
                return ResponseEntity.ok(resultList)
            }
        }

        return ResponseEntity.ok(emptyList())
    }
}