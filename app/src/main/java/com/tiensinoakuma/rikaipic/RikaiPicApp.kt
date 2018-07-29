package com.tiensinoakuma.rikaipic

import android.app.Application
import android.content.Context
import com.tiensinoakuma.rikaipic.dagger.AppComponent
import com.tiensinoakuma.rikaipic.dagger.AppModule
import com.tiensinoakuma.rikaipic.dagger.DaggerAppComponent
import timber.log.Timber

class RikaiPicApp : Application() {
    private lateinit var component: AppComponent

    companion object {
        fun getComponent(context: Context): AppComponent {
            return (context.applicationContext as RikaiPicApp).component
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        component.inject(this)
    }
}