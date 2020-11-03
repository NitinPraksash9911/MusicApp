package com.example.musicapp.notification

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media.session.MediaButtonReceiver
import com.example.musicapp.AppClass.Companion.CHANNEL_ID
import com.example.musicapp.R
import com.example.musicapp.data.AudioModel

object AppNotification {


    fun createNotification(context: Context, audioModel: AudioModel): NotificationCompat.Builder? {


        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(audioModel.path)
        val art = retriever.embeddedPicture
        val bitmapArt = if (art != null) {
            BitmapFactory.decodeByteArray(art, 0, art.size)
        } else {
            BitmapFactory.decodeResource(context.resources, R.drawable.music_icon)
        }

//        notificationManager = NotificationManagerCompat.from(context)


        val pauseAction = NotificationCompat.Action.Builder(
            R.drawable.ic_small_pause, "Pause", null
        ).build()

        val skipNext = NotificationCompat.Action.Builder(
            R.drawable.ic_skip_next, "next", null
        ).build()
        val skipPrev = NotificationCompat.Action.Builder(
            R.drawable.ic_skip_previous, "prev", null
        ).build()


        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_play)
            .setLargeIcon(bitmapArt)
            .setContentTitle(audioModel.aName)
            .setContentText(audioModel.aArtist)
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            context,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    )
            ).setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        notification.addAction(skipPrev)
        notification.addAction(pauseAction)
        notification.addAction(skipNext)

//        notificationManager.notify(1, notification.build())
        return notification
    }
}