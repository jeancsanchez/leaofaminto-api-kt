package com.github.jeancsanchez.leaofaminto.view.services

import com.github.jeancsanchez.leaofaminto.domain.model.Operacao
import org.springframework.web.multipart.MultipartFile

/**
 * @author @jeancsanchez
 * @created 09/06/2021
 * Jesus loves you.
 */
interface FileImporterService : ApplicationService<MultipartFile, List<Operacao>>