package com.example.seonyoung.my_application

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class LoginActivity : AppCompatActivity() {

    val id by lazy { findViewById<EditText>(R.id.loginActivity_edittext_id) }
    val password by lazy { findViewById<EditText>(R.id.loginActivity_edittext_password) }

    val login by lazy { findViewById<Button>(R.id.loginActivity_button_login) }
    val signup by lazy { findViewById<Button>(R.id.loginActivity_button_signup) }
    var firebaseRemoteConfig: FirebaseRemoteConfig? = null
    var firebaseAuth :FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var authStateListener :FirebaseAuth.AuthStateListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()

        var splash_background = firebaseRemoteConfig?.getString(getString(R.string.rc_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor(splash_background))
        }

        login.setBackgroundColor(Color.parseColor(splash_background))
        signup.setBackgroundColor(Color.parseColor(splash_background))

        login.setOnClickListener {
            loginEvent()
        }
        signup.setOnClickListener{
            startActivity((Intent(this@LoginActivity, SignupActivity::class.java)))
        }

        // 로그인 인터페이스 리스터
        authStateListener = FirebaseAuth.AuthStateListener {
            var user :FirebaseUser? = it.currentUser
            if ( user != null ){
                // 로그인
                var intent : Intent = Intent(applicationContext,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                // 로그아웃
            }
        }
    }

    // 로그인이 잘 되었는지 확인만 하는 아이   ( 로그인 후 다른 화면으로 넘어가는 아이는 따로 있음 :: 위에 authStateListener)
    fun loginEvent(){
        firebaseAuth.signInWithEmailAndPassword(id.text.toString(), password.text.toString()).addOnCompleteListener {
            if( !it.isSuccessful){
                // 로그인 실패한 부분
                Toast.makeText(this@LoginActivity, "로그인 실패래요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}
