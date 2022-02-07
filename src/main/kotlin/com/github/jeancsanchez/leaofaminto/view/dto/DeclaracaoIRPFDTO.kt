@file:Suppress("SpellCheckingInspection")

package com.github.jeancsanchez.leaofaminto.view.dto

/**
 * @author @jeancsanchez
 * @created 22/01/2022
 * Jesus loves you.
 */

data class DeclaracaoIRPFDTO(
    val rendimentosInsentos: List<DeclaracaoRendimentosIsentosDTO>,
    val rendimentosTributaveis: List<DeclaracaoRendimentosTributaveisDTO>,
    val bensEDireitos: List<DeclaracaoBensEDireitosDTO>,
)

data class DeclaracaoBensEDireitosDTO(
    val titulo: String,
    val codigo: String,
    val localizacao: String,
    val cnpj: String?,
    val discriminacao: String,
    val situacaoAnterior: String,
    val situacaoAtual: String,
)

data class DeclaracaoRendimentosIsentosDTO(
    val titulo: String,
    val codigo: String,
    val cnpjDaFonte: String?,
    val nomeDaFonte: String?,
    val valor: String,
)

data class DeclaracaoRendimentosTributaveisDTO(
    val titulo: String,
    val codigo: String,
    val cnpjDaFonte: String?,
    val nomeDaFonte: String?,
    val valor: String,
)
