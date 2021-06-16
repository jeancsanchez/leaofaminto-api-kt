package com.github.jeancsanchez.investments.domain.service

/**
 * Essa classe representa um Domain Service de acordo com a definição do DDD.
 *
 * @author @jeancsanchez
 * @created 15/06/2021
 * Jesus loves you.
 */

interface DomainService<P, R> {

    /**
     * Executa a operação do serviço.
     *
     * @param param necessário para a execução.
     * @return retorno da operação.
     */
    fun execute(param: P): R
}