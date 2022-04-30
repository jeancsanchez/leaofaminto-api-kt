package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.domain.model.AtivoInfo
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

/**
 * @author @jeancsanchez
 * @created 30/04/2022
 * Jesus loves you.
 */

@Service
class PegarAtivoInfoService : IDomainService<String, AtivoInfo?> {

    override fun execute(param: String): AtivoInfo? {
        RestTemplate().getForEntity(
            "https://leaofaminto-ativos-api.herokuapp.com/api/ativo/$param",
            AtivoInfo::class.java
        ).also {
            if (it.statusCode.is2xxSuccessful) {
                return it.body
            }
        }

        return null
    }
}