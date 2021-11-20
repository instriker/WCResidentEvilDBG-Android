package com.instriker.wcre.repositories

import java.util.ArrayList


object Scenarios {

    fun filterScenariosByExtensions(allScenarios: Collection<Scenario>, fromExtensions: Collection<Int>): List<Scenario> {

        val results = ArrayList<Scenario>(allScenarios.size)
        for (current in allScenarios) {
            if (fromExtensions.contains(current.extensionId)) {
                results.add(current)
            }
        }

        // TODO : DEPENDENT EXTENSIONS...
        return results
    }
}