package com.instriker.wcre

import com.instriker.wcre.framework.RedbgcActivity
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.presentation.*

class BuildResourceAreaActivity : RedbgcActivity() {

    protected override val initialLayout: Int
        get() = com.instriker.wcre.R.layout.activity_buildresourcearea

    override fun createViewModel(): RedbgcViewModel {
        return BuildResourceAreaViewModel(ServiceLocator(this))
    }

}
