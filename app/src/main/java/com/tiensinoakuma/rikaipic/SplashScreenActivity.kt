package com.tiensinoakuma.rikaipic

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tiensinoakuma.rikaipic.ui.RikaiPicActivity

class SplashScreenActivity : AppCompatActivity() {

    //todo set a theme with the app logo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        RikaiPicActivity.startActivity(this)
        finish()
    }

}
