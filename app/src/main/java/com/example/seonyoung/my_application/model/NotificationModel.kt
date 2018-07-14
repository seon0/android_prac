package com.example.seonyoung.my_application.model

data class NotificationModel(var to :String, var notification :Notification){
//    lateinit var notification :Notification

    companion object {
        data class Notification(var title:String, var text:String)
    }
}