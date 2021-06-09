package com.github.jeancsanchez.investments.view.services

import org.springframework.stereotype.Service

/**
 * Essa classe representa um Application Service de acordo com a definição do DDD.
 *
 * @author @jeancsanchez
 * @created 09/06/2021
 * Jesus loves you.
 */
interface ApplicationService<P, R> {

    /**
     * Executa a operação do serviço.
     *
     * @param param necessário para a execução.
     * @return retorno da operação.
     */
    fun execute(param: P): R
}