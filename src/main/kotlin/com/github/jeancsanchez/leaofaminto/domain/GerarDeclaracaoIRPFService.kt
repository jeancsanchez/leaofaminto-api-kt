package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.data.AtivoRepository
import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.model.Ativo
import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
import com.github.jeancsanchez.leaofaminto.view.dto.DeclaracaoBensEDireitosDTO
import com.github.jeancsanchez.leaofaminto.view.dto.DeclaracaoIRPFDTO
import com.github.jeancsanchez.leaofaminto.view.toBrazilMoney
import com.github.jeancsanchez.leaofaminto.view.toQuantidadeString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * @author @jeancsanchez
 * @created 22/01/2022
 * Jesus loves you.
 */

@Service
class GerarDeclaracaoIRPFService(
    @Autowired private val comprasRepository: ComprasRepository,
    @Autowired private val vendasRepository: VendasRepository,
    @Autowired private val ativoRepository: AtivoRepository,
    @Autowired private val gerarOperacoesConsolidadasService: GerarOperacoesConsolidadasService
) : IDomainService<Int, DeclaracaoIRPFDTO> {

    /**
     * @param param Current year
     */
    override fun execute(param: Int): DeclaracaoIRPFDTO {
        val consolidados = gerarOperacoesConsolidadasService.execute(Unit)

        val tituloBensEDireitos = "Bens e Direitos"
        val codigo = "31"

//        AMZN - 0.02357082 ac ̧ o ̃ es de Amazon . com Inc..
//                Custo total de US $ 75, 00 com dólar médio de R$ 5, 3683.
//
//        400 ACOES ORDINARIAS DE ITAUSA INVESTIMENTOS (ITSA4)
//        AO CUSTO MEDIO DE R$10.56-CUSTODIADA NA CORRETORA XP INVESTIMENTOS CCTVM S/A,
//        CNPJ:  02.332.886/0001-04

        val positionsList = getLastAndCurrentPosition(currentYear = param)
        val bensEDireitosList = arrayListOf<DeclaracaoBensEDireitosDTO>()

        consolidados
            .items
            .map {
                val ativo = it.ativo
                val corretora = comprasRepository.findTopByAtivoCodigoOrderByDataDesc(ativo.codigo).corretora

                val tipoDeAcao: String = when (ativo.tipoDeAtivo) {
                    TipoDeAtivo.ACAO -> {
                        if (it.quantidadeTotal > 1) "ações" else "ação"
                    }

                    TipoDeAtivo.FII -> {
                        if (it.quantidadeTotal > 1) "cotas" else "cota"
                    }

                    TipoDeAtivo.STOCK -> {
                        if (it.quantidadeTotal > 1) "ações" else "ação"
                    }
                }

                val localizacao =
                    if (ativo.tipoDeAtivo == TipoDeAtivo.ACAO || ativo.tipoDeAtivo == TipoDeAtivo.FII) {
                        "Brasil"
                    } else {
                        "Estados Unidos"
                    }


                val positions = positionsList.find { it.ativo.codigo == ativo.codigo }
                DeclaracaoBensEDireitosDTO(
                    titulo = tituloBensEDireitos,
                    codigo = codigo,
                    localizacao = localizacao,
                    cnpj = ativo.cnpj,
                    situacaoAnterior = positions?.lastPosition ?: "R$ 00,00",
                    situacaoAtual = positions?.currentPosition ?: "R$ 00,00",
                    discriminacao = ("${it.quantidadeTotal.toQuantidadeString()} $tipoDeAcao DE ${ativo.nome} (${ativo.codigo}) AO CUSTO" +
                            " MÉDIO DE ${it.precoMedio.toBrazilMoney()} CUSTODIADA NA CORRETORA ${corretora.nome}," +
                            " CNPJ: ${corretora.cnpj}").toUpperCase()
                ).apply {
                    bensEDireitosList.add(this)
                }
            }

        return DeclaracaoIRPFDTO(
            rendimentosInsentos = emptyList(),
            rendimentosTributaveis = emptyList(),
            bensEDireitos = bensEDireitosList
        )
    }


    data class AtivoPosition(
        val ativo: Ativo,
        val lastPosition: String?,
        val currentPosition: String?,
    )

    fun getLastAndCurrentPosition(currentYear: Int): List<AtivoPosition> {
        val lastYear = currentYear - 1
        val firstDayOfLastYear = LocalDate.of(lastYear, 1, 1)
        val lastDayOfLastYear = firstDayOfLastYear.with(TemporalAdjusters.lastDayOfYear())

        val firstDayOfYear = LocalDate.of(currentYear, 1, 1)
        val lastDayOfYear = firstDayOfYear.with(TemporalAdjusters.lastDayOfYear())

        val lastPositions = getPosicaoWithinDates(firstDayOfLastYear, lastDayOfLastYear)
            .map {
                AtivoPosition(
                    ativo = it.first,
                    lastPosition = it.second.toString(),
                    currentPosition = null
                )
            }

        val currentPositions = getPosicaoWithinDates(firstDayOfYear, lastDayOfYear)
            .map {
                AtivoPosition(
                    ativo = it.first,
                    lastPosition = null,
                    currentPosition = it.second.toString()
                )
            }

        val result = lastPositions
            .zip(currentPositions)
            .map {
                val currentPosition =
                    (it.first.lastPosition?.toDouble() ?: 0.0) + (it.second.currentPosition?.toDouble() ?: 0.0)

                AtivoPosition(
                    ativo = it.first.ativo,
                    lastPosition = it.first.lastPosition?.toDouble()?.toBrazilMoney(),
                    currentPosition = currentPosition.toBrazilMoney()
                )
            }

        return result
    }

    private fun getPosicaoWithinDates(startDate: LocalDate, endDate: LocalDate): List<Pair<Ativo, Double>> {
        return comprasRepository.findAll()
            .filter { it.data in startDate..endDate }
            .toMutableList()
            .groupBy { it.ativo }
            .map { map ->
                val valorCompras = map.value
                    .sumByDouble { it.valorTotal }

                val quantidadeCompras = map.value
                    .sumByDouble { it.quantidade }

                val quantidadeVendas = vendasRepository
                    .findAllByAtivoCodigo(map.key.codigo)
                    .filter { it.data in startDate..endDate }
                    .sumByDouble { it.quantidade }

                val quantidade = quantidadeCompras - quantidadeVendas
                val precoMedio = if (quantidade > 0) {
                    valorCompras / quantidadeCompras
                } else {
                    0.0
                }

                val valorTotal = quantidade * precoMedio

                Pair(map.key, valorTotal)
            }
    }
}