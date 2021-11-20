package com.instriker.wcre

import com.instriker.wcre.framework.RedbgcActivity
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.presentation.NewGameTrackerSetupViewModel
import com.instriker.wcre.presentation.ServiceLocator

class NewGameTrackerSetupActivity : RedbgcActivity() {

    protected override val initialLayout: Int
        get() = com.instriker.wcre.R.layout.activity_newgamesetup

    override fun createViewModel(): RedbgcViewModel {
        return NewGameTrackerSetupViewModel(ServiceLocator(this))
    }
}
