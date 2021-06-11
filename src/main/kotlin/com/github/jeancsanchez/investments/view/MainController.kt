package com.github.jeancsanchez.investments.view

import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.domain.RelatorioService
import com.github.jeancsanchez.investments.domain.model.TOperacao
import com.github.jeancsanchez.investments.domain.model.dto.ConsolidadoDTO
import com.github.jeancsanchez.investments.view.services.CEIXLSImporterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@RestController
@Configuration
@CrossOrigin(origins = ["http://localhost:3000"])
@RequestMapping("/api")
class MainController(
    @Autowired val operacaoRepository: OperacaoRepository,
    @Autowired val relatorioService: RelatorioService,
    @Autowired val ceiXLSImporterImporterService: CEIXLSImporterService,
) {
    @GetMapping("/operacoes")
    fun listarOperacoes(): ResponseEntity<List<TOperacao>> {
        return ResponseEntity.ok(operacaoRepository.findAll())
    }

    @GetMapping("/operacoes/consolidadas")
    fun listarOperacoesConsolidadas(): ResponseEntity<ConsolidadoDTO> {
        return ResponseEntity.ok(relatorioService.pegarOperacoesConsolidadas())
    }

    @PostMapping("/sync")
    fun syncOperacoes(@RequestParam("arquivo") arquivo: MultipartFile): ResponseEntity<List<TOperacao>> {
        val result = ceiXLSImporterImporterService.execute(arquivo)
        return ResponseEntity.ok(result)
    }
}