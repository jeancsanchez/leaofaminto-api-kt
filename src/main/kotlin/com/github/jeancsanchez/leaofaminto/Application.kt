package com.github.jeancsanchez.leaofaminto

import com.github.jeancsanchez.leaofaminto.domain.CriarCenarioBrasilService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@SpringBootApplication
class Application : CommandLineRunner {

    @Autowired
    private lateinit var criarCenarioBrasilService: CriarCenarioBrasilService

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<Application>(*args)
        }
    }

    override fun run(vararg args: String?) {
        criarCenarioBrasilService.execute(Unit)
    }
}