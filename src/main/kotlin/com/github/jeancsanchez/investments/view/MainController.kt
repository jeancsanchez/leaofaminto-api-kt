package com.github.jeancsanchez.investments.view

import com.github.jeancsanchez.investments.data.CorretoraRepository
import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.data.PapelRepository
import com.github.jeancsanchez.investments.domain.RelatorioService
import com.github.jeancsanchez.investments.domain.model.Operacao
import com.github.jeancsanchez.investments.domain.model.dto.ConsolidadoDTO
import com.github.jeancsanchez.investments.view.services.ApplicationService
import com.github.jeancsanchez.investments.view.services.CEIxlsImporterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
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
@RequestMapping("/api")
class MainController(
    @Autowired val operacaoRepository: OperacaoRepository,
    @Autowired val relatorioService: RelatorioService,
    @Autowired val ceiXLSImporterService: CEIxlsImporterService,
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
        val result = ceiXLSImporterService.run(arquivo)
        return ResponseEntity.ok(result)
    }
}