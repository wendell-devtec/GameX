/*
 * Copyright (c) de Tw4rs 2021.
 */
package com.tw4rs.gamex.app


import androidx.multidex.MultiDexApplication
import com.tw4rs.gamex.R
import eu.giovannidefrancesco.easysharedprefslib.IStorage
import eu.giovannidefrancesco.easysharedprefslib.SharedPreferenceStorage

class App : MultiDexApplication() {
    var appStorage: IStorage? = null
    override fun onCreate() {
        super.onCreate()
        instance = this
        appStorage = SharedPreferenceStorage(instance, getString(R.string.app_name))
    }

    operator fun <T> get(name: String?, value: T): T {
        val result: T
        result = appStorage!![name, value]
        return result
    }

    fun store(name: String?, value: Any?) {
        appStorage!!.store(name, value)
    }

    companion object {
        @get:Synchronized
        var instance: App? = null
            private set
    }
}