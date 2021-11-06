package com.instriker.wcre

import com.instriker.wcre.framework.RedbgcActivity
import com.instriker.wcre.framework.RedbgcViewModel
import com.instriker.wcre.presentation.ServiceLocator
import com.instriker.wcre.presentation.ToolsViewModel

class ToolsActivity : RedbgcActivity() {

    protected override val initialLayout: Int
        get() = com.instriker.wcre.R.layout.activity_tools

    override fun createViewModel(): RedbgcViewModel {
        return ToolsViewModel(ServiceLocator(this))
    }
}
