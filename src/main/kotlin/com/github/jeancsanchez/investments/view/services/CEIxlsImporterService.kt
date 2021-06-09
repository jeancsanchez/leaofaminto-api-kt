package com.github.jeancsanchez.investments.view.services

import com.github.jeancsanchez.investments.domain.model.Operacao
import org.springframework.web.multipart.MultipartFile

/**
 * @author @jeancsanchez
 * @created 09/06/2021
 * Jesus loves you.
 */
interface CEIXLSImporterService : ApplicationService<MultipartFile, List<Operacao>> {
}