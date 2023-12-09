package com.instriker.wcre.framework

import android.os.AsyncTask

class BackgroundTaskPromise<TResult>(
        private val _promise: () -> TResult,
        private val _completedCallback: ((TResult) -> Unit)?,
        private val _errorCallback: ((Exception) -> Unit)?)
    : AsyncTask<Void, Void, TResult>() {
    private var _exception: Exception? = null
    private var _sucessful: Boolean = false

    @Deprecated("To be updated to coroutine")
    override fun doInBackground(vararg args: Void): TResult? {
        _sucessful = false
        _exception = null

        try {
            val result = _promise()
            _sucessful = true
            return result
        } catch (ex: Exception) {
            _exception = ex
            _sucessful = false
            return null
        }

    }

    @Deprecated("To be updated to coroutine")
    override fun onPostExecute(result: TResult) {
        if (_sucessful) {
            _completedCallback?.invoke(result)
        } else {
            _errorCallback?.invoke(_exception!!)
        }
    }
}