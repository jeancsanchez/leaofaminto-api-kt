package com.github.jeancsanchez.leaofaminto

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author @jeancsanchez
 * @created 15/05/2021
 * Jesus loves you.
 */

@SpringBootApplication
class Application {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<Application>(*args)
        }
    }
}