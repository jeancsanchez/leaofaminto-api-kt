@file:Suppress("SpellCheckingInspection")

package com.github.jeancsanchez.leaofaminto.view

import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.GerarOperacoesConsolidadasService
import com.github.jeancsanchez.leaofaminto.domain.model.Operacao
import com.github.jeancsanchez.leaofaminto.view.services.FileImporterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/**
 * @author @jeancsanchez
 * @created 15/01/2022
 * Jesus loves you.
 */

@RestController
@Configuration
@CrossOrigin(origins = ["http://localhost:3000"])
@RequestMapping("/api/passfolio")
class PassfolioController(
    @Autowired val vendasRepository: VendasRepository,
    @Autowired val comprasRepository: ComprasRepository,
    @Autowired val gerarOperacoesConsolidadasService: GerarOperacoesConsolidadasService,

    @Autowired
    @Qualifier("passfolioImporter") val passfolioImporterService: FileImporterService,
) {

    /**
     * Sincroniza as operações conforme o arquivo informado.
     */
    @PostMapping("/sync")
    fun syncOperacoesV2(@RequestParam("arquivo") arquivo: MultipartFile): ResponseEntity<List<Operacao>> {
        val result = passfolioImporterService.execute(arquivo)
        return ResponseEntity.ok(result)
    }
}