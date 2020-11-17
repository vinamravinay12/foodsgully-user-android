package com.foodsgully.foodsgullyuser.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.foodsgully.foodsgullyuser.R


class LoginActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findNavController(R.id.login_host_fragment).navigate(R.id.nav_login)

    }
}