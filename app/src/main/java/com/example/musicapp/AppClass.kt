package com.example.musicapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.os.Build
import com.example.musicapp.di.ApplicationComponent
import com.example.musicapp.di.ApplicationModule
import com.example.musicapp.di.DaggerApplicationComponent
import com.example.musicapp.di.DatabaseModule


class AppClass : Application() {

    lateinit var applicationComponent: ApplicationComponent

    companion object {


        const val CHANNEL_ID = BuildConfig.APPLICATION_ID

        private var instance: AppClass? = null

        fun getComponent(): ApplicationComponent? {
            return instance!!.applicationComponent
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (instance == null) {
            instance = this
        }
        applicationComponent = DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()

        createNotificationChannel()
    }


    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, "channel_one", IMPORTANCE_DEFAULT)
            notificationChannel.description = "channel_1"

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


}