package com.example.seonyoung.my_application.chat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.seonyoung.my_application.R
import com.example.seonyoung.my_application.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {

    val destinationUid: String by lazy { intent.getStringExtra("destinationUid") }
    val button: Button by lazy { findViewById<Button>(R.id.messageActivity_button) }
    val editText: EditText by lazy { findViewById<EditText>(R.id.messageActivity_editText) }

    val uid : String = FirebaseAuth.getInstance().currentUser!!.uid
    var chatRoomUid : String? = null

    val tag = "MessageActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        button.setOnClickListener {
            var users :HashMap<String, Boolean> = HashMap()
            var comments :HashMap<String, ChatModel.Companion.Comment> = HashMap()
            users.put(uid,true)
            users.put(destinationUid,true)
            var chatModel: ChatModel = ChatModel(users,comments)
            checkChatRoom()
            if( chatRoomUid == null ){
                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel)
            }
            else {
                val comment : ChatModel.Companion.Comment = ChatModel.Companion.Comment(uid, editText.text.toString())
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid!!).child("comments").push().setValue(comment)
            }

        }
    }

    fun checkChatRoom(){
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for( item in p0.children) {
                    var chatModel :ChatModel? = item.getValue(ChatModel::class.java)
                    if(chatModel!!.users?.containsKey(destinationUid)!!){
                        chatRoomUid = item.key
                    }
                }
            }

        })
    }
}
