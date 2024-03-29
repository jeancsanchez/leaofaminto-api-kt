@file:Suppress("SpellCheckingInspection")

package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.data.AtivoRepository
import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.model.Ativo
import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
import com.github.jeancsanchez.leaofaminto.view.dto.*
import com.github.jeancsanchez.leaofaminto.view.stripToDouble
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
     * @param param Report year
     */
    override fun execute(param: Int): DeclaracaoIRPFDTO {
        val bensEDireitos = generateBensEDireitos(reportYear = param)
        val rendimentosIsentos = generateRendimentosIsentos(reportYear = param)
        val rendimentosTributaveis = generateRendimentosTributaveis(reportYear = param)

        return DeclaracaoIRPFDTO(
            rendimentosInsentos = rendimentosIsentos,
            rendimentosTributaveis = rendimentosTributaveis,
            bensEDireitos = bensEDireitos
        )
    }

    private fun generateBensEDireitos(reportYear: Int): DeclaracaoBensEDireitosDTO {
        val positionsList = getLastAndCurrentPosition(reportYear = reportYear)
        val bensEDireitosList = arrayListOf<BensEDireitosItemDTO>()

        gerarOperacoesConsolidadasService
            .execute(Unit)
            .items
            .map { operacaoConsolidada ->
                val ativo = operacaoConsolidada.ativo
                val positions = positionsList.find { it.ativo == ativo }
                val lastPosition = positions?.lastPosition?.stripToDouble() ?: 0.0
                val currentPosition = positions?.currentPosition?.stripToDouble() ?: 0.0
                if (lastPosition == 0.0 && currentPosition == 0.0) return@map

                val corretora = comprasRepository.findTopByAtivoCodigoOrderByDataDesc(ativo.codigo).corretora
                val tipoDeAcao: String = when (ativo.tipoDeAtivo) {
                    TipoDeAtivo.ACAO -> {
                        if (operacaoConsolidada.quantidadeTotal > 1) "ações" else "ação"
                    }

                    TipoDeAtivo.FII -> {
                        if (operacaoConsolidada.quantidadeTotal > 1) "cotas" else "cota"
                    }

                    TipoDeAtivo.STOCK -> {
                        if (operacaoConsolidada.quantidadeTotal > 1) "ações" else "ação"
                    }
                }

                val localizacao =
                    if (ativo.tipoDeAtivo == TipoDeAtivo.ACAO || ativo.tipoDeAtivo == TipoDeAtivo.FII) {
                        "Brasil"
                    } else {
                        "Estados Unidos"
                    }

                BensEDireitosItemDTO(
                    localizacao = localizacao,
                    nome = ativo.nome,
                    cnpj = ativo.cnpj,
                    situacaoAnterior = lastPosition.toBrazilMoney(),
                    situacaoAtual = currentPosition.toBrazilMoney(),
                    discriminacao = (operacaoConsolidada.quantidadeTotal.toQuantidadeString() +
                            " $tipoDeAcao DE ${ativo.nome} (${ativo.codigo}) AO CUSTO" +
                            " MÉDIO DE ${operacaoConsolidada.precoMedio.toBrazilMoney()}" +
                            " CUSTODIADA NA CORRETORA ${corretora.nome}," +
                            " CNPJ: ${corretora.cnpj}").toUpperCase()
                ).run {
                    bensEDireitosList.add(this)
                }
            }

        return DeclaracaoBensEDireitosDTO(
            titulo = "Bens e Direitos",
            codigo = "31",
            dados = bensEDireitosList
        )
    }

    private data class AtivoPosition(
        val ativo: Ativo,
        val lastPosition: String?,
        val currentPosition: String?,
    )

    private fun getLastAndCurrentPosition(reportYear: Int): List<AtivoPosition> {
        val olderYear = LocalDate.of(2010, 1, 1)
        val firstDayOfLastYear = LocalDate.of(reportYear - 1, 1, 1)
        val lastDayOfLastYear = firstDayOfLastYear.with(TemporalAdjusters.lastDayOfYear())

        val lastPositions: List<Pair<Ativo, Double>> = getPositionWithinDates(
            startDate = olderYear,
            endDate = firstDayOfLastYear.minusDays(1)
        )

        val currentPositions: List<Pair<Ativo, Double>> = getPositionWithinDates(
            startDate = firstDayOfLastYear,
            endDate = lastDayOfLastYear
        )

        val oldAtivos = lastPositions.distinctBy { it.first }.map { it.first }
        val currentAtivos = currentPositions.distinctBy { it.first }.map { it.first }
        val newAtivos = currentAtivos
            .mapNotNull { currentAtivo ->
                if (oldAtivos.contains(currentAtivo).not()) {
                    return@mapNotNull currentAtivo
                }

                return@mapNotNull null
            }

        val allPositions = lastPositions + currentPositions
        return allPositions
            .distinctBy { it.first }
            .map { pair ->
                val ativo = pair.first

                if (ativo in newAtivos) {
                    return@map AtivoPosition(
                        ativo = ativo,
                        lastPosition = 0.0.toBrazilMoney(),
                        currentPosition = pair.second.toBrazilMoney()
                    )
                }

                val lastPositionValue = lastPositions.find { it.first == ativo }
                    ?.second
                    ?: 0.0

                val currentPosition = (currentPositions.find { it.first == ativo }
                    ?.second
                    ?: 0.0)
                    .run {
                        if (this < 0.0) return@run 0.0 // Posição liquidada
                        if (this == 0.0) return@run lastPositionValue // Posição se manteve
                        return@run this + lastPositionValue
                    }

                AtivoPosition(
                    ativo = ativo,
                    lastPosition = lastPositionValue.toBrazilMoney(),
                    currentPosition = currentPosition.toBrazilMoney()
                )
            }
    }

    private fun getPositionWithinDates(startDate: LocalDate, endDate: LocalDate): List<Pair<Ativo, Double>> {
        val comprasList = comprasRepository.findAll()
            .filter { it.data in startDate..endDate }
            .toMutableList()

        val vendasList = vendasRepository.findAll()
            .filter { it.data in startDate..endDate }
            .toMutableList()

        return if (comprasList.isEmpty()) {
            vendasList
                .groupBy { it.ativo }
                .map { keyMap ->
                    val vendas = keyMap.value
                    val quantidade = vendas.sumByDouble { it.quantidade }
                    val valor = keyMap.value.sumByDouble { it.valorTotal }
                    val valorFinal = (quantidade * valor) * -1
                    Pair(keyMap.key, valorFinal)
                }
        } else {
            comprasList
                .groupBy { it.ativo }
                .map { keyMap ->
                    val compras = keyMap.value
                    val vendas = vendasList.filter { it.ativo == keyMap.key }

                    val valorCompras = compras.sumByDouble { it.valorTotal }
                    val valorVendas = vendas.sumByDouble { it.valorTotal }
                    val valorTotal = valorCompras - valorVendas

                    val quantidadeCompras = compras.sumByDouble { it.quantidade }
                    val quantidadeVendas = vendas.sumByDouble { it.quantidade }
                    val quantidadeTotal = quantidadeCompras - quantidadeVendas

                    val precoMedio = if (quantidadeTotal > 0) {
                        valorTotal / quantidadeTotal
                    } else {
                        0.0
                    }

                    val valorFinal = quantidadeTotal * precoMedio

                    Pair(keyMap.key, valorFinal)
                }
        }
    }

    private fun generateRendimentosIsentos(reportYear: Int): DeclaracaoRendimentosIsentosDTO {
        val firstDayOfLastYear = LocalDate.of(reportYear - 1, 1, 1)
        val lastDayOfLastYear = firstDayOfLastYear.with(TemporalAdjusters.lastDayOfYear())

        val comprasList = comprasRepository.findAll()
            .filter { it.data in firstDayOfLastYear..lastDayOfLastYear }
            .toMutableList()

        val vendasList = vendasRepository.findAll()
            .filter { it.data in firstDayOfLastYear..lastDayOfLastYear }
            .toMutableList()


        return DeclaracaoRendimentosIsentosDTO(
            titulo = "Rendimentos Insentos ou Não tributaveis",
            codigo = "9", // Lucros e dividendos recebidos. 20 - Ganhos liquidos em operações...
            dados = emptyList()
        )
    }

    private fun generateRendimentosTributaveis(reportYear: Int): DeclaracaoRendimentosTributaveisDTO {
        return DeclaracaoRendimentosTributaveisDTO(
            titulo = "Rendimentos Tributaveis",
            codigo = "10", // Juros sobre capital proprio
            dados = emptyList()
        )
    }
}