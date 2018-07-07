package com.example.seonyoung.my_application.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.seonyoung.my_application.R
import com.example.seonyoung.my_application.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

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
            return CustomvViewHolder(view)
        }

        inner class CustomvViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        }

        override fun getItemCount(): Int {
            return chatModels.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        }

    }

}