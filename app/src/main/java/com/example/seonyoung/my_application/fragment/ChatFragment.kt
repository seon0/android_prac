package com.example.seonyoung.my_application.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.seonyoung.my_application.R
import com.example.seonyoung.my_application.model.ChatModel
import com.example.seonyoung.my_application.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ChatFragment :Fragment(){
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view : View = inflater!!.inflate(R.layout.fragment_chat, container, false)
        var recyclerView : RecyclerView = view.findViewById(R.id.chatfragment_recyclerview)
        recyclerView.adapter = ChatRecyclerViewAdapter()
        recyclerView.layoutManager = LinearLayoutManager(inflater.context)

        return view
    }

    inner class ChatRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var chatModels : ArrayList<ChatModel> = ArrayList()
        lateinit var uid :String

        init {
            uid = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseDatabase.getInstance().getReference("chatrooms").orderByChild("users/"+uid).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    chatModels.clear()
                    p0.children.forEach {
                        chatModels.add(it.getValue(ChatModel::class.java)!!)
                    }
                    notifyDataSetChanged()
                }

            })

        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view :View = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            lateinit var imageView : ImageView
            lateinit var textView_title : TextView
            lateinit var textView_last_message : TextView

            init {
                imageView = view.findViewById(R.id.chatitem_imageview)
                textView_title= view.findViewById(R.id.chatitem_textview_title)
                textView_last_message = view.findViewById(R.id.chatitem_textview_lastMessage)
            }


        }

        override fun getItemCount(): Int {
            return chatModels.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var customViewHolder : CustomViewHolder = holder as CustomViewHolder
            var destinationUid :String? = null

            // 일일 챗방에 있는 유저를 체크
            chatModels.get(position).users!!.keys.forEach {
                if( it!=uid ){
                    destinationUid = it
                }
            }
            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid!!).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var userModel :UserModel? = p0.getValue(UserModel::class.java)
                    Glide.with(customViewHolder.itemView.context).load(userModel!!.profileImageUrl).apply(RequestOptions().circleCrop()).into(customViewHolder.imageView)
                    customViewHolder.textView_title.setText(userModel.userName)
                }
            })
            //메세지를 내림 차순으로 정렬 후 마지막 메세지의 키값을 가져옴
            var commentMap : TreeMap<String, ChatModel.Companion.Comment> = TreeMap(Collections.reverseOrder())
            commentMap.putAll(chatModels.get(position).comments!!)
            var lastMessageKey:String = commentMap.keys.toTypedArray()[0]
            customViewHolder.textView_last_message.setText(chatModels.get(position).comments!!.get(lastMessageKey)!!.message)
        }

    }

}