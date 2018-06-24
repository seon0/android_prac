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
import com.example.seonyoung.my_application.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PeopleFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {

        var view :View = inflater!!.inflate(R.layout.fragment_people, container, false)
        var recycleView : RecyclerView = view.findViewById(R.id.peoplefragment_recyclerview)
        recycleView.layoutManager=LinearLayoutManager(inflater.context)
        recycleView.adapter = PeopleFragmentRecycleViewAdapter()
        return view
    }

    class PeopleFragmentRecycleViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view : View = LayoutInflater.from(parent!!.context).inflate(R.layout.item_friend, parent,false)

            return CustomViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            var customView = holder as CustomViewHolder
            Glide.with(holder?.itemView?.context!!).load(userModels.get(position).profileImageUrl).apply(RequestOptions().circleCrop()).into( (customView.imageView))
            customView.textView.setText(userModels.get(position).userName)
        }


        var userModels : ArrayList<UserModel>
        init{
            userModels = ArrayList<UserModel>()
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    userModels.clear()
                    p0.children.forEach {
                        userModels.add(it.getValue(UserModel::class.java)!!)
                    }
                    notifyDataSetChanged()

                }

            })
        }
//        constructor()

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var imageView :ImageView
            var textView :TextView
           init{
               imageView = view.findViewById<ImageView>(R.id.friend_imageview)
               textView = view.findViewById(R.id.frienditem_textview)
           }
        }

        override fun getItemCount(): Int {
            return userModels.size
        }


    }
}