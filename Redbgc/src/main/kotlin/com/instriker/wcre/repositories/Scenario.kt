package com.instriker.wcre.repositories

class Scenario(
        val id: Int,
        val name: String,
        val description: String?,
        val extensionId: Int,
        val useBasicResources: Boolean,
        val gameMode: GameMode?,
        val skillSystem: Boolean)
