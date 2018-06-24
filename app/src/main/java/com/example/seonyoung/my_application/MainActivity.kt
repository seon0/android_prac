package com.example.seonyoung.my_application

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.seonyoung.my_application.fragment.PeopleFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fragmentManager.beginTransaction().replace(R.id.mainactivity_framelayout, PeopleFragment()).commit()
    }
}
