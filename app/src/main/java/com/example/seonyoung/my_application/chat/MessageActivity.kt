package com.example.seonyoung.my_application.chat

import android.util.Log
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideContext
import com.bumptech.glide.request.RequestOptions
import com.example.seonyoung.my_application.R
import com.example.seonyoung.my_application.model.ChatModel
import com.example.seonyoung.my_application.model.ChatModel.Companion
import com.example.seonyoung.my_application.model.NotificationModel
import com.example.seonyoung.my_application.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_message.*
import okhttp3.*
import org.w3c.dom.Text
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MessageActivity : AppCompatActivity() {

    val destinationUid: String by lazy { intent.getStringExtra("destinationUid") }
    val button: Button by lazy { findViewById<Button>(R.id.messageActivity_button) }
    val editText: EditText by lazy { findViewById<EditText>(R.id.messageActivity_editText) }

    val uid: String = FirebaseAuth.getInstance().currentUser!!.uid
    var chatRoomUid: String? = null

    val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.messageActivity_recyclerview) }

    var simpleDateFormat : SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    //val tag = "MessageActivity"
    lateinit var destinationUserModel: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        checkChatRoom()
        button.setOnClickListener {
            var users: HashMap<String, Boolean> = HashMap()
            var comments: HashMap<String, Companion.Comment> = HashMap()
            users.put(uid, true)
            users.put(destinationUid, true)
            var chatModel: ChatModel = ChatModel(users, comments)
            //checkChatRoom()
            if (chatRoomUid == null) {
                button.isEnabled = false
                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnSuccessListener {
                    checkChatRoom()
                }
            } else {
                val comment: Companion.Comment = Companion.Comment(uid, editText.text.toString(), ServerValue.TIMESTAMP)
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid!!).child("comments").push().setValue(comment).addOnCompleteListener {
                    sendGcm()
                    editText.setText("")
                }
            }

        }
    }

    fun sendGcm(){
        var gson : Gson = Gson()

        var sendNotification : NotificationModel.Companion.Notification = NotificationModel.Companion.Notification("보낸이 아이디", editText.text.toString())
        var notificationModel : NotificationModel = NotificationModel(destinationUserModel.pushToken, sendNotification)

        var requestBody : RequestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel))

        var request : Request = Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyC2vr58VfsUByFLQ1wQTtgEeYN9inN_ypo")
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build()
        var okHTTPClient : OkHttpClient = OkHttpClient()
        okHTTPClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                Log.d("OKHTTPPPPPPPP","faillllll")
            }

            override fun onResponse(call: Call?, response: Response?) {
                Log.d("OKHTTPPPPPPPP","okokokok")
            }
        })
    }
    fun checkChatRoom() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (item in p0.children) {
                    var chatModel: ChatModel? = item.getValue(ChatModel::class.java)
                    if (chatModel!!.users?.containsKey(destinationUid)!!) {
                        chatRoomUid = item.key
                        button.isEnabled = true
                        recyclerView.layoutManager = LinearLayoutManager(this@MessageActivity)
                        recyclerView.adapter = RecyclerViewAdapter()
                    }
                }
            }

        })
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var comments : ArrayList<Companion.Comment>

        init {

            comments = ArrayList<Companion.Comment>()
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    destinationUserModel = p0.getValue(UserModel::class.java)!!
                    getMessageList()
                }

            })
        }

        fun getMessageList() {
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid!!).child("comments").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    comments.clear()
                    p0.children.forEach {
                        comments.add(it.getValue(ChatModel.Companion.Comment::class.java)!!)
                    }
                    //메세지가 갱신
                    notifyDataSetChanged()

                    recyclerView.scrollToPosition(comments.size-1)
                }

            })
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
            return MessageViewHolder(view)
        }


        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var messageViewHolder: MessageViewHolder = (holder as MessageViewHolder)

            //내가 보냔메세지
            if (comments.get(position).uid.equals(uid)) {
                messageViewHolder.textView_message.text = comments.get(position).message
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble)
                messageViewHolder.linearLayout_destination.visibility = View.INVISIBLE
                messageViewHolder.textView_message.setTextSize(25f)
                messageViewHolder.linearLayout_main.gravity=Gravity.RIGHT
            }
            //상대방이 보낸 매세지
            else {
                Glide.with(holder.itemView.context).load(destinationUserModel!!.profileImageUrl).apply(RequestOptions().circleCrop()).into(messageViewHolder.iamgeView_profile)
                messageViewHolder.textview_name.setText(destinationUserModel!!.userName)
                messageViewHolder.linearLayout_destination.visibility = View.VISIBLE
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble)
                messageViewHolder.textView_message.setText(comments.get(position).message)
                messageViewHolder.textView_message.setTextSize(25f)
                messageViewHolder.linearLayout_main.gravity = Gravity.LEFT
            }
            var unixTime = comments.get(position).timestamp.toString().toLong()
            var date : Date = Date(unixTime)
            simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            var time :String = simpleDateFormat.format(date)
            messageViewHolder.testView_timestamp.setText(time)
        }

        inner class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var textView_message: TextView = view.findViewById(R.id.messageItem_textView_message)
            var textview_name: TextView = view.findViewById(R.id.messageItem_textview_name)
            var iamgeView_profile: ImageView = view.findViewById(R.id.messageItem_imageview_profile)
            var linearLayout_destination: LinearLayout = view.findViewById(R.id.messageItem_linearlayout_destination)
            var linearLayout_main : LinearLayout = view.findViewById(R.id.messageItem_linearlayout_main)
            var testView_timestamp : TextView = view.findViewById(R.id.messageItem_textView_timestamp)

        }

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.fromleft, R.anim.toright)
    }
}
