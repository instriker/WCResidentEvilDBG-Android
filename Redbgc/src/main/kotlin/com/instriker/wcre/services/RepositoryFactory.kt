package com.instriker.wcre.services

import android.content.Context

import com.instriker.wcre.framework.DbRepositoryFactory
import com.instriker.wcre.repositories.CardRepository
import com.instriker.wcre.repositories.ExtensionRepository
import com.instriker.wcre.repositories.GameContentOpenHelper
import com.instriker.wcre.repositories.MansionRepository
import com.instriker.wcre.repositories.ScenarioRepository
import com.instriker.wcre.repositories.StandardMutualyExclusiveCardRepository
import com.instriker.wcre.repositories.StandardPilesRepository

class RepositoryFactory(private val _context: Context) {

    fun createCardRepository(): CardRepository {
        return CardRepository(DbRepositoryFactory(GameContentOpenHelper(_context)))
    }

    fun createExtensionRepository(): ExtensionRepository {
        return ExtensionRepository(DbRepositoryFactory(GameContentOpenHelper(_context)))
    }

    fun createScenarioRepository(): ScenarioRepository {
        return ScenarioRepository(DbRepositoryFactory(GameContentOpenHelper(_context)))
    }

    fun createMansionRepository(): MansionRepository {
        return MansionRepository(DbRepositoryFactory(GameContentOpenHelper(_context)))
    }

    fun createStandardPilesRepository(): StandardPilesRepository {
        return StandardPilesRepository(DbRepositoryFactory(GameContentOpenHelper(_context)))
    }

    fun createStandardMutualyExclusiveCardRepository(): StandardMutualyExclusiveCardRepository {
        return StandardMutualyExclusiveCardRepository(DbRepositoryFactory(GameContentOpenHelper(_context)))
    }
}
