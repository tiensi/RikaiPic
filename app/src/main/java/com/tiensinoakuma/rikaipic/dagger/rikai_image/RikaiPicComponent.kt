package com.tiensinoakuma.rikaipic.dagger.rikai_image

import com.tiensinoakuma.rikaipic.ui.RikaiPicActivity
import dagger.Subcomponent

@Subcomponent(modules = [(RikaiPicModule::class)])
interface RikaiPicComponent {
    fun inject(rikaiImageActivity: RikaiPicActivity)
}
