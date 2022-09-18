package ru.myitschool.lab23

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import ru.myitschool.lab23.di.module
import ru.myitschool.lab23.di.viewModelModule

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                listOf(
                    module,
                    viewModelModule
                )
            )
        }
    }

    // start Koin context
    /*startKoin(this, weatherApp + localAndroidDatasourceModule)

    Iconify.with(WeathericonsModule())*/

}