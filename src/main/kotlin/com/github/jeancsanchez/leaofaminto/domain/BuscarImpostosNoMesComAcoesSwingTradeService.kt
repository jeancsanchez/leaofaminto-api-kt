package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.ImpostoRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.model.Imposto
import com.github.jeancsanchez.leaofaminto.domain.model.Operacao
import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
import com.github.jeancsanchez.leaofaminto.domain.model.dto.ImpostosDTO
import com.github.jeancsanchez.leaofaminto.view.round
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.YearMonth

/**
 * @author @jeancsanchez
 * @created 15/06/2021
 * Jesus loves you.
 */

@Service
class BuscarImpostosNoMesComAcoesSwingTradeService(
    @Autowired private val comprasRepository: ComprasRepository,
    @Autowired private val vendasRepository: VendasRepository,
    @Autowired private val impostoRepository: ImpostoRepository,
) : IDomainService<LocalDate, ImpostosDTO> {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun execute(date: LocalDate): ImpostosDTO {
        val firstDay = YearMonth.from(date).atDay(1)
        val lastDay = YearMonth.from(date).atEndOfMonth()
        var lucros = 0.0
        var prejuizos = 0.0
        val operacoesDeVenda = mutableListOf<Operacao>()

        vendasRepository.findAll()
            .filter { it.data >= firstDay && it.data <= lastDay }
            .filter { it.ativo.tipoDeAtivo == TipoDeAtivo.ACAO }
            .groupBy { it.ativo.codigo }
            .flatMap { it.value }
            .map { venda ->
                comprasRepository.findAllByAtivoCodigo(venda.ativo.codigo)
                    .forEach { compra ->
                        if (compra.data.isBefore(venda.data)) {
                            if (venda.valorTotal > 0) {
                                operacoesDeVenda.add(venda)
                                val resultado = venda.valorTotal - compra.valorTotal
                                if (resultado > 0) {
                                    lucros += resultado
                                } else if (resultado < 0) {
                                    prejuizos += resultado
                                }
                            }
                        }
                    }
            }


        if (operacoesDeVenda.sumByDouble { it.valorTotal } > 20000) {
            if (prejuizos > 0) {
//            Se vamos deduzir este prejuízo da base de cálculo do IR,
//            ele passa a ser OBRIGATÓRIO a informar na declaração,
//            na ficha Renda variável/Operações comuns.
                lucros -= prejuizos
            }

            // TODO: Precisa remover os custos antes
            val valorImpostoDoMes = ((lucros * 0.15) - 0.00005).round()

            // Verifica se há impostos remanescentes não pagos
            val impostosRemanescentes = impostoRepository.findAllByEstaPago(estaPago = false)

            // Verifica se o valor de imposto do mês já foi registrado, se não registra
            val impostoDoMes: Imposto = impostoRepository.findTop1ByDataReferenciaAndValor(
                dataReferencia = date,
                valor = valorImpostoDoMes
            ) ?: let {
                val novoImposto = Imposto(
                    dataReferencia = operacoesDeVenda.first().data,
                    operacoes = operacoesDeVenda,
                    valor = valorImpostoDoMes,
                    estaPago = false
                )
                impostoRepository.save(novoImposto)
            }

            // Se a soma dos impostos do mês com os remanescentes for maior que R$ 10,00, retorna esse imposto
            val totalImpostos = impostosRemanescentes.sumByDouble { it.valor } + impostoDoMes.valor
            if (totalImpostos >= 10) {
                val impostosList = mutableListOf<Imposto>()
                impostosList.addAll(impostosRemanescentes)

                if (impostoDoMes.estaPago == false) {
                    impostosList.add(impostoDoMes)
                }

                return ImpostosDTO(
                    impostos = impostosList,
                    total = impostosList.sumByDouble { it.valor }
                )
            }

            return ImpostosDTO()
        }

        return ImpostosDTO()
    }
}