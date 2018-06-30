package com.example.seonyoung.my_application

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.seonyoung.my_application.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage

class SignupActivity : AppCompatActivity() {

    val PICK_FROM_ALBUM: Int = 10
    val email by lazy { findViewById<EditText>(R.id.signupActivity_edittext_email) }
    val name by lazy { findViewById<EditText>(R.id.signupActivity_edittext_name) }
    val password by lazy { findViewById<EditText>(R.id.signupActivity_edittext_password) }
    val signup by lazy { findViewById<Button>(R.id.signupActivity_button_signup) }
    val profile by lazy { findViewById<ImageView>(R.id.singupActivity_imageview_profile) }
    var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        var splash_background = FirebaseRemoteConfig.getInstance().getString(getString(R.string.rc_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor(splash_background))
        }

        profile.setOnClickListener {
            var intent: Intent = Intent(Intent.ACTION_PICK)
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
            startActivityForResult(intent, PICK_FROM_ALBUM)
        }

        signup.setOnClickListener {
            if (email.text.toString() == null || name.text.toString() == null || password.text.toString() == null || imageUri==null) {
                return@setOnClickListener
            }

            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this@SignupActivity, OnCompleteListener {
                        var uid :String = it.getResult().user.uid
                        FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(imageUri!!).addOnCompleteListener {

                            val imageUrl: String = it.getResult().downloadUrl.toString()
                            var userModel: UserModel = UserModel()
                            userModel.userName = name.text.toString()
                            userModel.profileImageUrl = imageUrl
                            userModel.uid = FirebaseAuth.getInstance().currentUser!!.uid

                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener {
                                this@SignupActivity.finish()
                            }
                        }
                    })


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) { //data?였는데 nullable안해도 나중에 에러가 발생 안할까?
        if (requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            profile.setImageURI(data.data) // 가운데 뷰를 바꿈
            imageUri = data.data //이미지 경로 원본

        }
    }
}
