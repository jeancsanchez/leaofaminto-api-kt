@file:Suppress("SpellCheckingInspection")

package com.github.jeancsanchez.leaofaminto.view

import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.GerarDeclaracaoIRPFService
import com.github.jeancsanchez.leaofaminto.domain.GerarOperacoesConsolidadasService
import com.github.jeancsanchez.leaofaminto.domain.model.Operacao
import com.github.jeancsanchez.leaofaminto.view.dto.ConsolidadoDTO
import com.github.jeancsanchez.leaofaminto.view.dto.DeclaracaoIRPFDTO
import com.github.jeancsanchez.leaofaminto.view.dto.IRPFRequestDTO
import com.github.jeancsanchez.leaofaminto.view.dto.OperacoesDTO
import com.github.jeancsanchez.leaofaminto.view.services.FileImporterService
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
@CrossOrigin(origins = ["http://localhost:3000"])
@RequestMapping("/api")
class MainController(
    @Autowired val vendasRepository: VendasRepository,
    @Autowired val comprasRepository: ComprasRepository,
    @Autowired val gerarOperacoesConsolidadasService: GerarOperacoesConsolidadasService,
    @Autowired val gerarDeclaracaoIRPFService: GerarDeclaracaoIRPFService,

    @Autowired
    @Qualifier("CEIImporter") val ceiXLSImporterService: FileImporterService,

    @Autowired
    @Qualifier("CEIImporterV2") val ceiXLSImporterServiceV2: FileImporterService,

    @Autowired
    @Qualifier("passfolioImporter") val passfolioImporterService: FileImporterService
) {
    /**
     * Lista todas as operações.
     */
    @GetMapping("/operacoes")
    fun listarOperacoes(): ResponseEntity<OperacoesDTO> {
        return OperacoesDTO(
            compras = comprasRepository.findAll(),
            vendas = vendasRepository.findAll(),
        ).run {
            ResponseEntity.ok(this)
        }
    }

    /**
     * Lista todas as operações de forma consolidada
     */
    @GetMapping("/operacoes/consolidadas")
    fun listarOperacoesConsolidadas(): ResponseEntity<ConsolidadoDTO> {
        return ResponseEntity.ok(gerarOperacoesConsolidadasService.execute(Unit))
    }

    /**
     * Sincroniza as operações conforme o arquivo informado.
     */
    @Deprecated(
        message = "Arquivo antigo do CEI (meados de 2020).",
        replaceWith = ReplaceWith("syncOperacoesV2")
    )
    @PostMapping("/sync")
    fun syncOperacoes(@RequestParam("arquivo") arquivo: MultipartFile): ResponseEntity<List<Operacao>> {
        val result = ceiXLSImporterService.execute(arquivo)
        return ResponseEntity.ok(result)
    }

    /**
     * Sincroniza as operações conforme o arquivo informado.
     */
    @PostMapping("/v2/sync")
    fun syncOperacoesV2(@RequestParam("arquivo") arquivo: MultipartFile): ResponseEntity<OperacoesDTO> {
        ceiXLSImporterServiceV2.execute(arquivo)
        val response = listarOperacoes().body
        return ResponseEntity.ok(response)
    }

    /**
     * Sincroniza as operações conforme o arquivo da Passfolio.
     */
    @PostMapping("/passfolio/sync")
    fun syncOperacoesPassfolio(@RequestParam("arquivo") arquivo: MultipartFile): ResponseEntity<OperacoesDTO> {
        passfolioImporterService.execute(arquivo)
        val response = listarOperacoes().body
        return ResponseEntity.ok(response)
    }


    /**
     * Gera declaração de imposto de renda do ano informado.
     */
    @PostMapping("/irpf")
    fun generateIRPF(@RequestBody request: IRPFRequestDTO): ResponseEntity<DeclaracaoIRPFDTO> {
        val result = gerarDeclaracaoIRPFService.execute(request.year)
        return ResponseEntity.ok(result)
    }
}