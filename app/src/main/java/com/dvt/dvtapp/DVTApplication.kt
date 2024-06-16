package com.dvt.dvtapp

import android.app.Application
import com.dvt.dvtapp.dependencyInjection.repoModule
import com.dvt.dvtapp.dependencyInjection.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext

class DVTApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        GlobalContext.startKoin {
            androidLogger()
            androidContext(this@DVTApplication)
            modules(listOf(repoModule, viewModelModule))
        }
    }
}