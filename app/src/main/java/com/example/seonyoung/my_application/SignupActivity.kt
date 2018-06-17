package com.example.seonyoung.my_application

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.seonyoung.my_application.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class SignupActivity : AppCompatActivity() {

    val email by lazy { findViewById<EditText>(R.id.signupActivity_edittext_email) }
    val name by lazy { findViewById<EditText>(R.id.signupActivity_edittext_name) }
    val password by lazy { findViewById<EditText>(R.id.signupActivity_edittext_password) }
    val signup by lazy { findViewById<Button>(R.id.signupActivity_button_signup) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        var splash_background = FirebaseRemoteConfig.getInstance().getString(getString(R.string.rc_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor(splash_background))
        }
        signup.setOnClickListener {
            if( email.text.toString()==null || name.text.toString()==null || password.text.toString()==null ){
                return@setOnClickListener
            }
            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this@SignupActivity, OnCompleteListener {
                        var userModel :UserModel = UserModel()
                        userModel.userName = name.text.toString()
                        var uid = it.getResult().user.uid
                        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel)
                    })
        }
    }

}
