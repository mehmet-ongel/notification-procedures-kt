package com.example.notificationprocedures

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.notificationprocedures.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "1"
    private val CHANNEL_NAME = "Notification Procedures"
    private val NOTIFICATION_ID = 1

    lateinit var builder : NotificationCompat.Builder

    lateinit var mainBinding: ActivityMainBinding
    var counter = 0

    var bitmap : Bitmap? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        mainBinding.sendNotification.setOnClickListener {
            counter++

            mainBinding.sendNotification.text = counter.toString()

            if (counter == 5) {

                startNotification()

            }

        }

    }

    fun startNotification() {

        val intent = Intent(applicationContext, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        //actionButton
        val actionIntent = Intent(applicationContext,Receiver::class.java)
        actionIntent.putExtra("toast","This is a notification message")

        val actionPending = PendingIntent.getBroadcast(applicationContext,1, actionIntent, PendingIntent.FLAG_IMMUTABLE)

        //dismissButton
        val dismissIntent = Intent(applicationContext,ReceiverDismiss::class.java)

        val dismissPending = PendingIntent.getBroadcast(applicationContext,2,dismissIntent,PendingIntent.FLAG_IMMUTABLE)

        val myBitmap : Bitmap = BitmapFactory.decodeResource(resources,R.drawable.android)
        val text : String = resources.getString(R.string.big_text)


        builder = NotificationCompat.Builder(this@MainActivity,CHANNEL_ID)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val channel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
            val manager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        builder.setSmallIcon(R.drawable.small_icon)
            .setContentTitle("Title")
            .setContentText("Notification Text")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(R.drawable.small_icon,"Toast Message",actionPending)
            .addAction(R.drawable.small_icon,"Dismiss",dismissPending)
            .setLargeIcon(myBitmap)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(myBitmap)
                .bigLargeIcon(bitmap))
        //.setStyle(NotificationCompat.BigTextStyle().bigText(text))


        NotificationManagerCompat.from(this@MainActivity).apply {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

                if (ContextCompat.checkSelfPermission(this@MainActivity
                        , Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){

                    if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)){

                        Snackbar.make(
                            mainBinding.constraintLayout,
                            "Allow permission to take notification",
                            Snackbar.LENGTH_LONG).setAction("Allow"){

                            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS),10)

                        }.show()

                    }else{
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS),10)
                    }

                }else{
                    notify(NOTIFICATION_ID,builder.build())
                }

            }else{
                notify(NOTIFICATION_ID,builder.build())
            }

        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 10 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            NotificationManagerCompat.from(this@MainActivity).apply {

                notify(NOTIFICATION_ID, builder.build())

            }

        } else {
            Snackbar.make(
                mainBinding.constraintLayout,
                "Allow permission to take notification",
                Snackbar.LENGTH_LONG
            ).setAction("Allow") {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    10
                )
            }.show()
        }
    }
}