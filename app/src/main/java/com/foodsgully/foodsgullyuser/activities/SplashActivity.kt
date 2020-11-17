package com.foodsgully.foodsgullyuser.activities

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import com.foodsgully.foodsgullyuser.R
import com.foodsgully.foodsgullyuser.database.SharedPreferenceManager
import com.foodsgully.foodsgullyuser.firebase.FCMNotificationHelper
import com.foodsgully.foodsgullyuser.utils.FoodsGullyConstants
import com.foodsgully.foodsgullyuser.utils.FoodsGullyUtils
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashLogo = findViewById<ImageView>(R.id.ivSplashLogo)

        prepareNotificationChannel()
        ObjectAnimator.ofFloat(splashLogo, "translationX", FoodsGullyUtils.pxFromDp(this, 0f)).apply {
                duration = 3800
                startDelay = 100
                start()
        }

        Handler().postDelayed({
            val currentUser = FoodsGullyUtils.getCurrentUser(this)
            if(currentUser != null) launchHomeActivity() else launchLoginActivity()
        },4000)

    }

    private fun prepareNotificationChannel() {
        val sharedPreferenceManager = SharedPreferenceManager(this, FoodsGullyConstants.LOGIN_SP)
        if (!sharedPreferenceManager.getBooleanPreference(FoodsGullyConstants.IS_CHANNEL_PREPARED, false) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) FCMNotificationHelper(this)

    }

    private fun launchLoginActivity() {
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }

    private fun launchHomeActivity() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}