package com.instriker.wcre

import com.instriker.wcre.framework.RedbgcActivity
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.presentation.GameTrackerViewModel
import com.instriker.wcre.presentation.ServiceLocator

class GameTrackerActivity : RedbgcActivity() {

    protected override val initialLayout: Int
        get() = com.instriker.wcre.R.layout.activity_gametracker

    override fun createViewModel(): RedbgcViewModel {
        return GameTrackerViewModel(ServiceLocator(this))
    }
}