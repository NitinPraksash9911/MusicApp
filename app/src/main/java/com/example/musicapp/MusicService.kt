package com.example.musicapp

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.media.session.MediaButtonReceiver
import androidx.navigation.navGraphViewModels
import com.example.musicapp.data.AudioModel
import com.example.musicapp.helper.*
import com.example.musicapp.reciever.ControlActionsListener
import com.example.musicapp.ui.HomeFragment.Companion.tempAudioList
import com.example.musicapp.viewmodel.MusicDataViewModel
import com.example.musicapp.viewmodel.ViewModelProviderFactory
import javax.inject.Inject

class MusicService : LifecycleService(), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener,
    AudioManager.OnAudioFocusChangeListener {

    private var mClicksCnt = 0
    private var mPlayOnPrepare = true

    private var mMediaSession: MediaSessionCompat? = null

    private var currentPosition = 0

    private var mediaPlayer: MediaPlayer? = null

    private var mAudioManager: AudioManager? = null
    lateinit var currentTrack: AudioModel


    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory



    private fun getIsPlaying() = mediaPlayer?.isPlaying!!


    override fun onCreate() {
        super.onCreate()

        mMediaSession = MediaSessionCompat(this, "MusicService")
        mMediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
        mMediaSession!!.setCallback(object : MediaSessionCompat.Callback() {
            override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
//                handleMediaButton(mediaButtonEvent)
                return super.onMediaButtonEvent(mediaButtonEvent)
            }
        })



        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)


        when (intent?.action) {

            INIT -> handleInit(intent)
            PREVIOUS -> handlePrevious()
            PAUSE -> pauseTrack()
            PLAYPAUSE -> handlePlayPause()
            NEXT -> handleNext()
        }

        return START_NOT_STICKY
    }


    private fun handleInit(intent: Intent? = null) {

        currentPosition = intent?.getIntExtra("pos", 0)!!
        currentTrack = tempAudioList[currentPosition]

        initMediaPlayerIfNeeded()
        MediaButtonReceiver.handleIntent(mMediaSession!!, intent)

        setTrack()

    }

    private fun initMediaPlayerIfNeeded() {

        if (mediaPlayer != null) {
            return
        }
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)
            setOnPreparedListener(this@MusicService)
            setOnCompletionListener(this@MusicService)
            setOnErrorListener(this@MusicService)
        }
    }

    private fun handleNext() {

        currentPosition++
        currentTrack = tempAudioList[currentPosition]
        initMediaPlayerIfNeeded()
        mediaPlayer!!.reset()
        setTrack()

    }

    private fun handlePlayPause() {

        if (getIsPlaying()) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }
        createNotification()
    }

    private fun pauseTrack() {
    }

    private fun handlePrevious() {
        currentPosition--
        currentTrack = tempAudioList[currentPosition]
        initMediaPlayerIfNeeded()
        mediaPlayer!!.reset()
        setTrack()
    }

    private fun createNotification() {

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(currentTrack.path)
        val art = retriever.embeddedPicture
        val bitmapArt = if (art != null) {
            BitmapFactory.decodeByteArray(art, 0, art.size)
        } else {
            BitmapFactory.decodeResource(this.resources, R.drawable.music_icon)
        }

        val playPauseIcon =
            if (getIsPlaying()) R.drawable.ic_small_pause else R.drawable.ic_small_play


        val pauseAction = NotificationCompat.Action.Builder(
            playPauseIcon, "Pause", getIntent(PLAYPAUSE)
        ).build()

        val skipNext = NotificationCompat.Action.Builder(
            R.drawable.ic_skip_next, "next", getIntent(NEXT)
        ).build()
        val skipPrev = NotificationCompat.Action.Builder(
            R.drawable.ic_skip_previous, "prev", getIntent(PREVIOUS)
        ).build()

//        val notificationDismissedIntent = Intent(this, NotificationDismissedReceiver::class.java).apply {
//            action = NOTIFICATION_DISMISSED
//        }
//        val notificationDismissedPendingIntent = PendingIntent.getBroadcast(this, 0, notificationDismissedIntent, PendingIntent.FLAG_CANCEL_CURRENT)


        val notification = NotificationCompat.Builder(this, AppClass.CHANNEL_ID)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(bitmapArt)
            .setContentTitle(currentTrack.aName)
            .setContentText(currentTrack.aArtist)
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this,
                    PlaybackStateCompat.ACTION_STOP
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setShowCancelButton(true)
                    .setCancelButtonIntent(
                        MediaButtonReceiver.buildMediaButtonPendingIntent(
                            this,
                            PlaybackStateCompat.ACTION_STOP
                        )
                    ).setMediaSession(mMediaSession?.sessionToken)

            ).setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NOTIFICATION_SERVICE)


        notification.addAction(skipPrev)
        notification.addAction(pauseAction)
        notification.addAction(skipNext)


        startForeground(1, notification.build())

        // delay foreground state updating a bit, so the notification can be swiped away properly after initial display
        Handler(Looper.getMainLooper()).postDelayed({
            if (!getIsPlaying()) {
                stopForeground(false)
            }
        }, 200L)

        val playbackState =
            if (getIsPlaying()) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        try {
            mMediaSession!!.setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setState(playbackState, PlaybackState.PLAYBACK_POSITION_UNKNOWN, 1.0f)
                    .build()
            )
        } catch (ignored: IllegalStateException) {
        }

    }

    private fun getIntent(action: String): PendingIntent {
        val intent = Intent(this, ControlActionsListener::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(applicationContext, 0, intent, 0)
    }

    private fun setTrack() {

        mediaPlayer?.setDataSource(applicationContext, Uri.parse(currentTrack.path))
        mediaPlayer?.prepareAsync()

    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
        createNotification()
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        mediaPlayer?.reset()
        return false
    }

    override fun onCompletion(p0: MediaPlayer?) {
    }

    override fun onAudioFocusChange(p0: Int) {
    }


    override fun onDestroy() {
        super.onDestroy()
        closure()
    }

    private fun closure() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(p0: Intent?): Nothing? {
        super.onBind(p0)
        return null
    }


}