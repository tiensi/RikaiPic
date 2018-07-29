package com.tiensinoakuma.rikaipic.dagger

import com.tiensinoakuma.rikaipic.RikaiPicApp
import com.tiensinoakuma.rikaipic.dagger.rikai_image.RikaiPicComponent
import com.tiensinoakuma.rikaipic.dagger.rikai_image.RikaiPicModule
import com.tiensinoakuma.rikaipic.dagger.scopes.AppScope
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@AppScope
@Component(modules = [AppModule::class, AndroidInjectionModule::class, NetModule::class])
interface AppComponent {
    fun inject(rikaiPicApp: RikaiPicApp)
    fun rikaiPicActivity(rikaiImageModule: RikaiPicModule): RikaiPicComponent
}
