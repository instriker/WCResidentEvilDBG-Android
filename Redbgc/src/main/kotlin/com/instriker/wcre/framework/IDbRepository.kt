package com.instriker.wcre.framework

import java.io.IOException

interface IDbRepository {
    @Throws(IOException::class)
    fun Read(queryString: String): IDbResultSet

    @Throws(IOException::class)
    fun Read(queryString: String, params: Array<String>): IDbResultSet

    fun close()
}
