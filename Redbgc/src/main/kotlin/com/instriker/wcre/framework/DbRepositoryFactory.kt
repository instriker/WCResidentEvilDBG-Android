package com.instriker.wcre.framework

import com.instriker.wcre.repositories.GameContentOpenHelper

class DbRepositoryFactory(private val _gameContentOpenHelper: GameContentOpenHelper) : IDbRepositoryFactory {

    override fun open(): IDbRepository {
        return DbRepository(_gameContentOpenHelper)
    }

}
