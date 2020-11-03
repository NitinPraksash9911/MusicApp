package com.example.musicapp.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.musicapp.MusicService
import com.example.musicapp.helper.NEXT
import com.example.musicapp.helper.PLAYPAUSE
import com.example.musicapp.helper.PREVIOUS

class ControlActionsListener : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        when (action) {
            PREVIOUS, PLAYPAUSE, NEXT -> context.sendIntent(action)
        }
    }

    fun Context.sendIntent(action: String) {
        Intent(this, MusicService::class.java).apply {
            this.action = action
            ContextCompat.startForegroundService(this@sendIntent, this)
        }
    }
}
