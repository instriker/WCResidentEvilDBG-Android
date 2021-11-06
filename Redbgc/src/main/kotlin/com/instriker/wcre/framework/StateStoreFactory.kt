package com.instriker.wcre.framework

import android.os.Bundle

object StateStoreFactory {

    fun create(savedInstanceState: Bundle): IStateStore {
        return BundleStateStore(savedInstanceState)
    }
}
