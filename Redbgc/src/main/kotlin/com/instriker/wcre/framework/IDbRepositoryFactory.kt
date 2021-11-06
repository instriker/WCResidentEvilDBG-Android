package com.instriker.wcre.framework

interface IDbRepositoryFactory {
    fun open(): IDbRepository
}
