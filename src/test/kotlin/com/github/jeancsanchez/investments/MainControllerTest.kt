package com.github.jeancsanchez.investments

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jeancsanchez.investments.data.OperacaoRepository
import com.github.jeancsanchez.investments.domain.model.TipoAcao
import com.github.jeancsanchez.investments.domain.model.TipoDeLote
import com.github.jeancsanchez.investments.domain.model.TipoOperacao
import com.github.jeancsanchez.investments.view.formatToStringBR
import com.github.jeancsanchez.investments.view.round
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*

/**
 * @author @jeancsanchez
 * @created 17/05/2021
 * Jesus loves you.
 */

@SpringBootTest
@WebAppConfiguration
@RunWith(SpringRunner::class)
@TestExecutionListeners(DependencyInjectionTestExecutionListener::class)
class MainControllerTest {

    @Autowired
    protected lateinit var context: WebApplicationContext

    private lateinit var mvc: MockMvc

    protected lateinit var request: MockHttpServletRequestBuilder

    @Autowired
    private lateinit var operacaoRepository: OperacaoRepository

    protected val mapper = ObjectMapper()

    @Value("classpath:cei_2020.xls")
    var arquivo2020: Resource? = null

    @Value("classpath:cei_2021.xls")
    var arquivo2021: Resource? = null


    @BeforeEach
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build()
        operacaoRepository.deleteAll()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun sincronizarOperacoesCEIExcel() {
        fazerUploadDeArquivo(arquivo2021)
        operacaoRepository.findAll().run {
            assertEquals(19364.81, sumByDouble { it.valorTotal }.round())

            first().also { firstLine ->
                assertTrue(firstLine.corretora.nome.contains("Clear Corretora", true))
                assertEquals("04/03/20", firstLine.data.formatToStringBR())
                assertEquals(TipoOperacao.COMPRA, firstLine.tipoDaOperacao)
                assertEquals("ITSA4", firstLine.papel.codigo)
                assertEquals(TipoDeLote.LOTE_DE_100, firstLine.tipoDeLote)
                assertEquals(TipoAcao.PREFERENCIAL, firstLine.tipoDaAcao)
                assertEquals(100, firstLine.quantidade)
                assertEquals(12.20, firstLine.preco)
                assertEquals(1220.0, firstLine.valorTotal)
            }

            last().also { lastLine ->
                assertTrue(lastLine.corretora.nome.contains("Clear Corretora", true))
                assertEquals("20/05/21", lastLine.data.formatToStringBR())
                assertEquals(TipoOperacao.COMPRA, lastLine.tipoDaOperacao)
                assertEquals("XPML11", lastLine.papel.codigo)
                assertEquals(TipoDeLote.LOTE_DE_100, lastLine.tipoDeLote)
                assertEquals(TipoAcao.FUNDO_IMOBILIARIO, lastLine.tipoDaAcao)
                assertEquals(1, lastLine.quantidade)
                assertEquals(104.55, lastLine.preco)
                assertEquals(104.55, lastLine.valorTotal)
            }
        }
    }

    /**
     * Nesse caso, quando já existe operações importadas, ao sincronizar novo arquivo,
     * deve-se manter os já importados e só adicionar os novos.
     */
    @Test
    fun sincronizarOperacoesComNovoArquivoCEIExcel() {
        fazerUploadDeArquivo(arquivo2020)
        assertEquals(22, operacaoRepository.count())

        /**
         * Arquivo 2020 -> 22 operações
         * Arquivo 2021 -> 35 operações
         */
        fazerUploadDeArquivo(arquivo2021)
        assertEquals(35, operacaoRepository.count())
    }

    private fun fazerUploadDeArquivo(resource: Resource?) {
        val file = MockMultipartFile(
            "arquivo",
            resource!!.inputStream
        )

        request = multipart("/api/sync").file(file)

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().`is`(200))
    }
}