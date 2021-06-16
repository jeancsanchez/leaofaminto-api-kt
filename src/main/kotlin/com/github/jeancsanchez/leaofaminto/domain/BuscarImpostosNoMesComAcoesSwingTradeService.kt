package com.github.jeancsanchez.leaofaminto.domain

import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.ImpostoRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.model.Imposto
import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
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
) : IDomainService<LocalDate, Imposto?> {

    override fun execute(date: LocalDate): Imposto? {
        val firstDay = YearMonth.from(date).atDay(1)
        val lastDay = YearMonth.from(date).atEndOfMonth()
        var lucros = 0.0
        var prejuizos = 0.0
        var totalDeVendas = 0.0

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
                                totalDeVendas += venda.valorTotal

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

        if (totalDeVendas > 20000) {
            if (prejuizos > 0) {
//            Se vamos deduzir este prejuízo da base de cálculo do IR,
//            ele passa a ser OBRIGATÓRIO a informar na declaração,
//            na ficha Renda variável/Operações comuns.
                lucros -= prejuizos
            }

            // TODO: Precisa remover os custos antes
            val totalImposto = ((lucros * 0.15) - 0.00005).round()
            val impostoDoMes = impostoRepository.findTop1ByDataReferenciaAndValor(
                dataReferencia = date,
                valor = totalImposto,
            )

            if (impostoDoMes !== null && impostoDoMes.valor >= 10) {
                return impostoDoMes
            }

            val novoImposto = Imposto(
                dataReferencia = date,
                valor = totalImposto,
                estaPago = false
            )

            impostoRepository.save(novoImposto)
            if (novoImposto.valor >= 10) {
                return novoImposto
            }

            return null
        }

        return null
    }
}