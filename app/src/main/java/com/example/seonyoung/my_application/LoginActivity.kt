package com.example.seonyoung.my_application

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class LoginActivity : AppCompatActivity() {

    val login by lazy { findViewById<Button>(R.id.loginActivity_button_login) }
    val signup by lazy { findViewById<Button>(R.id.loginActivity_button_signup) }
    var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        var splash_background = mFirebaseRemoteConfig?.getString(getString(R.string.rc_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor(splash_background))
        }

        login.setBackgroundColor(Color.parseColor(splash_background))
        signup.setBackgroundColor(Color.parseColor(splash_background))

        signup.setOnClickListener{
            startActivity((Intent(this@LoginActivity, SignupActivity::class.java)))
        }

    }
}