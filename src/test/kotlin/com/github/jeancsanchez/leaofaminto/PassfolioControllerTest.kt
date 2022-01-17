package com.github.jeancsanchez.leaofaminto

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.jeancsanchez.leaofaminto.data.ComprasRepository
import com.github.jeancsanchez.leaofaminto.data.OperacaoRepository
import com.github.jeancsanchez.leaofaminto.data.VendasRepository
import com.github.jeancsanchez.leaofaminto.domain.model.TipoDeAtivo
import com.github.jeancsanchez.leaofaminto.view.formatToStringBR
import com.github.jeancsanchez.leaofaminto.view.round
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
 * @created 15/01/2022
 * Jesus loves you.
 */

@SpringBootTest
@WebAppConfiguration
@RunWith(SpringRunner::class)
@TestExecutionListeners(DependencyInjectionTestExecutionListener::class)
class PassfolioControllerTest {

    @Autowired
    protected lateinit var context: WebApplicationContext

    private lateinit var mvc: MockMvc

    protected lateinit var request: MockHttpServletRequestBuilder

    @Autowired
    private lateinit var operacaoRepository: OperacaoRepository

    @Autowired
    private lateinit var comprasRepository: ComprasRepository

    @Autowired
    private lateinit var vendasRepository: VendasRepository

    protected val mapper = ObjectMapper()

    @Value("classpath:passfolio_2021.csv")
    var passfolio2021: Resource? = null

    @BeforeEach
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build()
        operacaoRepository.deleteAll()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun sincronizarComprasAcoesPassfolioCSV() {
        fazerUploadDeArquivo(passfolio2021)
        comprasRepository.findAll().run {
            assertEquals(150.0, sumByDouble { it.valorTotal }.round(), 0.1)

            first().also { firstLine ->
                assertTrue(firstLine.corretora.nome.contains("Passfolio", true))
                assertEquals("12/10/2021", firstLine.data.formatToStringBR())
                assertEquals("SLYV", firstLine.ativo.codigo)
                assertEquals(TipoDeAtivo.STOCK, firstLine.ativo.tipoDeAtivo)
                assertEquals(0.45152091, firstLine.quantidade)
                assertEquals(84.16, firstLine.preco)
                assertEquals(38.0, firstLine.valorTotal.round())
            }

            last().also { lastLine ->
                assertTrue(lastLine.corretora.nome.contains("PASSFOLIO", true))
                assertEquals("06/01/2021", lastLine.data.formatToStringBR())
                assertEquals("AMZN", lastLine.ativo.codigo)
                assertEquals(TipoDeAtivo.STOCK, lastLine.ativo.tipoDeAtivo)
                assertEquals(0.02357082, lastLine.quantidade)
                assertEquals(3181.9, lastLine.preco)
                assertEquals(75.0, lastLine.valorTotal.round())
            }
        }
    }

    /**
     * Nesse caso, quando já existe operações importadas, ao sincronizar novo arquivo,
     * deve-se manter os já importados e só adicionar os novos.
     */
    @Test
    fun sincronizarOperacoesPassfolioSemDuplicidade() {
        fazerUploadDeArquivo(passfolio2021, version = 2)
        assertEquals(3, comprasRepository.count())
        assertEquals(0, vendasRepository.count())

        fazerUploadDeArquivo(passfolio2021, version = 2)
        assertEquals(3, comprasRepository.count())
        assertEquals(0, vendasRepository.count())
    }

    private fun fazerUploadDeArquivo(resource: Resource?, version: Int? = 1) {
        val file = MockMultipartFile(
            "arquivo",
            resource!!.inputStream
        )

        request = multipart("/api/passfolio/sync").file(file)

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().`is`(200))
    }
}