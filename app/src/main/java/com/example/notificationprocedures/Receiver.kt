package com.example.notificationprocedures

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val messagetext = intent?.getStringExtra("toast")

        Log.d("toast",messagetext!!)
        Toast.makeText(context,messagetext,Toast.LENGTH_LONG).show()
    }
}