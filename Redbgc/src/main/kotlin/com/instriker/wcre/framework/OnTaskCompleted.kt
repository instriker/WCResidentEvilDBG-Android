package com.instriker.wcre.framework

interface OnTaskCompleted<TResult> {
    fun onTaskCompleted(result: TResult)

    fun onTaskFailed()
} 