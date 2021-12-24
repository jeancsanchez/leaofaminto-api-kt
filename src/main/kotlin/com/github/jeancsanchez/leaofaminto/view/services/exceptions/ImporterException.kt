package com.github.jeancsanchez.leaofaminto.view.services.exceptions

/**
 * @author @jeancsanchez
 * @created 24/12/2021
 * Jesus loves you.
 */

class ImporterException(cause: Throwable?, line: Int, columnIndex: Int) : Throwable(
    message = "\nLinha: $line\nColuna: $columnIndex",
    cause = cause
)