package com.example.seonyoung.my_application.chat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.seonyoung.my_application.R
import com.example.seonyoung.my_application.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {

    val destinationUid: String by lazy { intent.getStringExtra("destinationUid") }
    val button: Button by lazy { findViewById<Button>(R.id.messageActivity_button) }
    val editText: EditText by lazy { findViewById<EditText>(R.id.messageActivity_editText) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        button.setOnClickListener {
            var chatModel: ChatModel = ChatModel()
            chatModel.uid = FirebaseAuth.getInstance().currentUser!!.uid
            chatModel.destinationUid = destinationUid

            FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel)

        }
    }
}
