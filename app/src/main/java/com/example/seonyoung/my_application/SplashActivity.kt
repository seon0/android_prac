package com.example.seonyoung.my_application

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.widget.LinearLayout
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.android.gms.tasks.OnCompleteListener

class SplashActivity : AppCompatActivity() {

    var linearLayout: LinearLayout? = null
    var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        Log.d("hi", "hihi")

        linearLayout = findViewById(R.id.splashactivity_linearLayout)
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        mFirebaseRemoteConfig?.setConfigSettings(configSettings)
        mFirebaseRemoteConfig?.setDefaults(R.xml.default_config)

        mFirebaseRemoteConfig?.fetch(0)//시간(초)
                ?.addOnCompleteListener(this, OnCompleteListener<Void> { task ->

                    if (task.isSuccessful) {

                        // After config data is successfully fetched, it must be activated before newly fetched
                        // values are returned.
                        mFirebaseRemoteConfig?.activateFetched()
                    } else {

                    }
                    displayMessage()
                })
    }

    fun displayMessage() {
        var splash_background = mFirebaseRemoteConfig?.getString("splash_background")
        var caps = mFirebaseRemoteConfig?.getBoolean("splash_message_caps")
        var splash_message = mFirebaseRemoteConfig?.getString("splash_message")

        Log.d("splash","${splash_background} , ${caps},  ${splash_message}")
        linearLayout?.setBackgroundColor(Color.parseColor(splash_background))

        if(caps!!){
            var builder : AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage(splash_message).setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i -> finish()  })

            builder.create().show()
        }
        else {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }
    }

}