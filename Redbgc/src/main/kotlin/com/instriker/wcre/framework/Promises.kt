package com.instriker.wcre.framework

object Promises {
    fun <TResult> run(promise: (() -> TResult), completed: (TResult) -> Unit, error: (Exception) -> Unit) {
        // TODO : Potential GC.Collect running during BG Tasks?
        BackgroundTaskPromise(promise, completed, error).execute()
    }
}