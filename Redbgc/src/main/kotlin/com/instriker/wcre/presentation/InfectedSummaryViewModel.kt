package com.instriker.wcre.presentation

import androidx.databinding.ObservableInt

import com.instriker.wcre.framework.BindingsFactory

class InfectedSummaryViewModel {
    var DecorationLevel = BindingsFactory.bindInteger()
    var Count = BindingsFactory.bindInteger()
}