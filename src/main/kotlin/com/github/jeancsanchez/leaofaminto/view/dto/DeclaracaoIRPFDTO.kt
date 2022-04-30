@file:Suppress("SpellCheckingInspection")

package com.github.jeancsanchez.leaofaminto.view.dto

/**
 * @author @jeancsanchez
 * @created 22/01/2022
 * Jesus loves you.
 */

data class DeclaracaoIRPFDTO(
    val rendimentosInsentos: DeclaracaoRendimentosIsentosDTO,
    val rendimentosTributaveis: DeclaracaoRendimentosTributaveisDTO,
    val bensEDireitos: DeclaracaoBensEDireitosDTO,
)

//region Bens e Direitos
data class BensEDireitosItemDTO(
    val localizacao: String,
    val cnpj: String?,
    val discriminacao: String,
    val situacaoAnterior: String,
    val situacaoAtual: String,
)

data class DeclaracaoBensEDireitosDTO(
    val titulo: String,
    val codigo: String,
    val dados: List<BensEDireitosItemDTO>
)

//endregion

//region Redimentos insentos ou não tributáveis
data class RedimentosIsentosItemDTO(
    val cnpjDaFonte: String?,
    val nomeDaFonte: String?,
    val valor: String
)

data class DeclaracaoRendimentosIsentosDTO(
    val titulo: String,
    val codigo: String,
    val dados: List<RedimentosIsentosItemDTO>
)
//endregion

//region Redimentos Tributáveis
data class RedimentosTributaveisDTO(
    val cnpjDaFonte: String?,
    val nomeDaFonte: String?,
    val valor: String,
)

data class DeclaracaoRendimentosTributaveisDTO(
    val titulo: String,
    val codigo: String,
    val dados: List<RedimentosTributaveisDTO>
)
//endregion
