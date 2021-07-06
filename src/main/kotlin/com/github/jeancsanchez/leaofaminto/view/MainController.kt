package com.github.jeancsanchez.leaofaminto.view

import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.GerarOperacoesConsolidadasService
import com.github.jeancsanchez.leaofaminto.domain.model.Operacao
import com.github.jeancsanchez.leaofaminto.domain.model.dto.ConsolidadoDTO
import com.github.jeancsanchez.leaofaminto.view.services.CEIXLSImporterService
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
    @Autowired val vendasRepository: VendasRepository,
    @Autowired val gerarOperacoesConsolidadasService: GerarOperacoesConsolidadasService,
    @Autowired val ceiXLSImporterService: CEIXLSImporterService,
) {
    /**
     * Lista todas as operações.
     */
    @GetMapping("/operacoes")
    fun listarOperacoes(): ResponseEntity<List<Operacao>> {
        return ResponseEntity.ok(vendasRepository.findAll())
    }

    /**
     * Lista todas as operações de forma consolidada
     */
    @GetMapping("/operacoes/consolidadas")
    fun listarOperacoesConsolidadas(): ResponseEntity<ConsolidadoDTO> {
        return ResponseEntity.ok(gerarOperacoesConsolidadasService.execute(Unit))
    }

    /**
     * Sincroniza as operações de acordo com o arquivo informado.
     */
    @PostMapping("/sync")
    fun syncOperacoes(@RequestParam("arquivo") arquivo: MultipartFile): ResponseEntity<List<Operacao>> {
        val result = ceiXLSImporterService.execute(arquivo)
        return ResponseEntity.ok(result)
    }
}