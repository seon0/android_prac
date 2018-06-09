package com.example.seonyoung.my_application

import android.content.DialogInterface
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import android.widget.Toast
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.support.v7.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener





class MainActivity : AppCompatActivity() {

    var linearLayout: LinearLayout? = null
    var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("hi","hihi")

        linearLayout = findViewById(R.id.mainactivity_linearLayout)
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        mFirebaseRemoteConfig?.setConfigSettings(configSettings)
        mFirebaseRemoteConfig?.setDefaults(R.xml.default_config);

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
        var main_background = mFirebaseRemoteConfig?.getString("main_background")
        var caps = mFirebaseRemoteConfig?.getBoolean("main_message_caps")
        var main_message = mFirebaseRemoteConfig?.getString("main_message")

        linearLayout?.setBackgroundColor(Color.parseColor(main_background))

        if(caps!!){
            var builder : AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage(main_message).setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i -> finish()  })

            builder.create().show()
        }
    }

}
