package ru.mperika.simplenotes

import android.os.AsyncTask

class DoInBackground<T0, T1, T2>(private val handler: () -> Unit) : AsyncTask<T0, T1, T2>() {
    override fun doInBackground(vararg params: T0): T2? {
        handler()
        return null
    }
}
