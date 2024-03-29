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

    @Autowired
    private lateinit var comprasRepository: ComprasRepository

    @Autowired
    private lateinit var vendasRepository: VendasRepository

    protected val mapper = ObjectMapper()

    @Value("classpath:cei_2020.xls")
    var arquivo2020: Resource? = null

    @Value("classpath:cei_2021.xls")
    var arquivo2021: Resource? = null

    @Value("classpath:cei_2021_v2.xlsx")
    var arquivo2021V2: Resource? = null

    @BeforeEach
    fun setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build()
        operacaoRepository.deleteAll()
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun sincronizarComprasAcoesCEIExcel() {
        fazerUploadDeArquivo(arquivo2020)
        comprasRepository.findAll().run {
            assertEquals(12931.49, sumByDouble { it.valorTotal }.round())

            first().also { firstLine ->
                assertTrue(firstLine.corretora.nome.contains("Clear", true))
                assertEquals("04/03/20", firstLine.data.formatToStringBR())
                assertEquals("ITSA4", firstLine.ativo.codigo)
                assertEquals(TipoDeAtivo.ACAO, firstLine.ativo.tipoDeAtivo)
                assertEquals(100.0, firstLine.quantidade)
                assertEquals(12.20, firstLine.preco)
                assertEquals(1220.0, firstLine.valorTotal)
            }

            last().also { lastLine ->
                assertTrue(lastLine.corretora.nome.contains("Clear", true))
                assertEquals("23/10/20", lastLine.data.formatToStringBR())
                assertEquals("WEGE3", lastLine.ativo.codigo)
                assertEquals(TipoDeAtivo.ACAO, lastLine.ativo.tipoDeAtivo)
                assertEquals(12.0, lastLine.quantidade)
                assertEquals(81.08, lastLine.preco)
                assertEquals(972.96, lastLine.valorTotal)
            }
        }
    }

    @Test
    fun sincronizarComprasAcoesCEIExcelV2() {
        fazerUploadDeArquivo(arquivo2021V2, version = 2)

        comprasRepository.findAll().run {
            assertEquals(34, count())
            assertEquals(13861.38, sumByDouble { it.valorTotal }.round())

            first().also { firstLine ->
                assertTrue(firstLine.corretora.nome.contains("Clear", true))
                assertEquals("14/12/2021", firstLine.data.formatToStringBR())
                assertEquals("BRCR11", firstLine.ativo.codigo)
                assertEquals(TipoDeAtivo.FII, firstLine.ativo.tipoDeAtivo)
                assertEquals(4.0, firstLine.quantidade)
                assertEquals(70.07, firstLine.preco)
                assertEquals(280.28, firstLine.valorTotal)
            }

            last().also { lastLine ->
                assertTrue(lastLine.corretora.nome.contains("Clear", true))
                assertEquals("06/01/2021", lastLine.data.formatToStringBR())
                assertEquals("MDIA3", lastLine.ativo.codigo)
                assertEquals(TipoDeAtivo.ACAO, lastLine.ativo.tipoDeAtivo)
                assertEquals(11.0, lastLine.quantidade)
                assertEquals(32.87, lastLine.preco)
                assertEquals(361.57, lastLine.valorTotal)
            }
        }

        vendasRepository.findAll().run {
            assertEquals(2, count())
            assertEquals(1245.14, sumByDouble { it.valorTotal }.round())

            first().also { firstLine ->
                assertTrue(firstLine.corretora.nome.contains("Clear", true))
                assertEquals("14/12/2021", firstLine.data.formatToStringBR())
                assertEquals("CEAB3", firstLine.ativo.codigo)
                assertEquals(TipoDeAtivo.ACAO, firstLine.ativo.tipoDeAtivo)
                assertEquals(50.0, firstLine.quantidade)
                assertEquals(6.23, firstLine.preco)
                assertEquals(311.50, firstLine.valorTotal)
            }

            last().also { lastLine ->
                assertTrue(lastLine.corretora.nome.contains("Clear", true))
                assertEquals("16/04/2021", lastLine.data.formatToStringBR())
                assertEquals("CEAB3", lastLine.ativo.codigo)
                assertEquals(TipoDeAtivo.ACAO, lastLine.ativo.tipoDeAtivo)
                assertEquals(68.0, lastLine.quantidade)
                assertEquals(13.73, lastLine.preco)
                assertEquals(933.64, lastLine.valorTotal)
            }
        }
    }

    @Test
    fun sincronizarVendasAcoesCEIExcel() {
        fazerUploadDeArquivo(arquivo2020)
        vendasRepository.findAll().run {
            assertEquals(878.70, sumByDouble { it.valorTotal }.round())

            first().also { firstLine ->
                assertTrue(firstLine.corretora.nome.contains("Clear", true))
                assertEquals("23/10/20", firstLine.data.formatToStringBR())
                assertEquals("SIMH3", firstLine.ativo.codigo)
                assertEquals(TipoDeAtivo.ACAO, firstLine.ativo.tipoDeAtivo)
                assertEquals(13.0, firstLine.quantidade)
                assertEquals(29.29, firstLine.preco)
                assertEquals(380.77, firstLine.valorTotal)
            }

            last().also { lastLine ->
                assertTrue(lastLine.corretora.nome.contains("Clear", true))
                assertEquals("23/10/20", lastLine.data.formatToStringBR())
                assertEquals("SIMH3", lastLine.ativo.codigo)
                assertEquals(TipoDeAtivo.ACAO, lastLine.ativo.tipoDeAtivo)
                assertEquals(17.0, lastLine.quantidade)
                assertEquals(29.29, lastLine.preco)
                assertEquals(497.93, lastLine.valorTotal)
            }
        }
    }

    /**
     * Nesse caso, quando já existe operações importadas, ao sincronizar novo arquivo,
     * deve-se manter os já importados e só adicionar os novos.
     */
    @Test
    fun sincronizarOperacoesArquivoCEIExcelSemDuplicidade() {
        fazerUploadDeArquivo(arquivo2020)
        assertEquals(20, comprasRepository.count())
        assertEquals(2, vendasRepository.count())

        fazerUploadDeArquivo(arquivo2021)
        assertEquals(31, comprasRepository.count())
        assertEquals(4, vendasRepository.count())
    }

    /**
     * Nesse caso, quando já existe operações importadas, ao sincronizar novo arquivo,
     * deve-se manter os já importados e só adicionar os novos.
     */
    @Test
    fun sincronizarOperacoesArquivoCEIExcelSemDuplicidadeV2() {
        fazerUploadDeArquivo(arquivo2021V2, version = 2)
        assertEquals(34, comprasRepository.count())
        assertEquals(2, vendasRepository.count())

        fazerUploadDeArquivo(arquivo2021V2, version = 2)
        assertEquals(34, comprasRepository.count())
        assertEquals(2, vendasRepository.count())
    }

    private fun fazerUploadDeArquivo(resource: Resource?, version: Int? = 1) {
        val file = MockMultipartFile(
            "arquivo",
            resource!!.inputStream
        )

        request = if (version == 2) {
            multipart("/api/v2/sync").file(file)
        } else {
            multipart("/api/sync").file(file)
        }

        mvc.perform(request)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().`is`(200))
    }
}