package com.github.jeancsanchez.leaofaminto.view.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * @author @jeancsanchez
 * @created 07/02/2022
 * Jesus loves you.
 */

data class IRPFRequestDTO(
    @JsonProperty("ano") val year: Int
)