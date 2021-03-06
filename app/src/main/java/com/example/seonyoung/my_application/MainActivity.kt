package com.example.seonyoung.my_application

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import com.example.seonyoung.my_application.fragment.ChatFragment
import com.example.seonyoung.my_application.fragment.PeopleFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var bottomNavigationView : BottomNavigationView = findViewById(R.id.mainactivity_bottomnavigationview) as BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener(object :BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId){
                    R.id.action_people ->{
                        fragmentManager.beginTransaction().replace(R.id.mainactivity_framelayout, PeopleFragment()).commit()
                        return true
                    }
                    R.id.action_chat -> {
                        fragmentManager.beginTransaction().replace(R.id.mainactivity_framelayout, ChatFragment()).commit()
                        return false
                    }
                }
                return false
            }
        })
        passPushTokenToServer()
    }

    fun passPushTokenToServer(){
        var uid : String = FirebaseAuth.getInstance().currentUser!!.uid
        var token : String = FirebaseInstanceId.getInstance().getToken()!!
        var map : HashMap<String, Any> = HashMap()
        map.put("pushToken", token)

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map)
    }
}
