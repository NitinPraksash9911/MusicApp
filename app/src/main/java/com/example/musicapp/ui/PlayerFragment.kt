package com.example.musicapp.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.palette.graphics.Palette
import com.example.musicapp.AppClass
import com.example.musicapp.MusicService
import com.example.musicapp.R
import com.example.musicapp.databinding.FragmentPlayerBinding
import com.example.musicapp.helper.INIT
import com.example.musicapp.ui.HomeFragment.Companion.tempAudioList
import com.example.musicapp.viewmodel.MusicDataViewModel
import com.example.musicapp.viewmodel.ViewModelProviderFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates
import kotlin.random.Random


class PlayerFragment : Fragment() {


    private lateinit var binding: FragmentPlayerBinding

    private val args: PlayerFragmentArgs by navArgs()
    private var mediaPlayer: MediaPlayer? = null

    private val liveData = MutableLiveData<Int>()

    private var itemPosition by Delegates.notNull<Int>()
    private var currentPosition = 0

    private var isShuffled = false
    private var isRepeat = false

    lateinit var bitmapArt: Bitmap

    @Inject
    lateinit var viewModelProviderFactory: ViewModelProviderFactory

    private val viewModel: MusicDataViewModel by navGraphViewModels(R.id.nav_graph, ({
        viewModelProviderFactory
    }))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPlayerBinding.inflate(inflater, container, false)


        AppClass.getComponent()!!.inject(this)


        initViews(args)

//        setPlayerControls()
        itemPosition = args.position
        Intent(requireContext(), MusicService::class.java).apply {
            putExtra("pos", this@PlayerFragment.itemPosition)
            action = INIT
            requireContext().startService(this)
        }
        Log.d("Instancesss", "onCreateView: $this")

        return binding.root
    }


    private fun initViews(args: PlayerFragmentArgs) {

        val audio = args.audioObject

        setGradientColor(audio.path)

        itemPosition = args.position

        binding.music = audio
        binding.totalDuration.text = formattedTime(audio.duration.toInt())

        binding.playImv.setImageResource(R.drawable.ic_pause)

        viewModel.getCurrentMusic().observe(viewLifecycleOwner, Observer {

            Log.d("currentMusic", "initViews: $it")

        })


//
//        if (mediaPlayer != null) {
//            mediaPlayer?.stop()
//            mediaPlayer?.release()
//        }
//        mediaPlayer =
//            MediaPlayer.create(requireActivity().applicationContext, Uri.parse(audio.path))
//        mediaPlayer?.start()
//
//        binding.musicSeekbar.max = mediaPlayer?.duration?.div(1000)!!
//
//        binding.musicSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
//
//                if (mediaPlayer != null && fromUser) {
//                    mediaPlayer?.seekTo(progress * 1000)
//                }
//            }
//
//            override fun onStartTrackingTouch(seekbar: SeekBar?) {
//            }
//
//            override fun onStopTrackingTouch(seekbar: SeekBar?) {
//            }
//
//        })
//
//        updateSeekBar()
//
//        liveData.observe(viewLifecycleOwner, {
//            binding.musicSeekbar.progress = it
//            binding.durationPlayed.text = formattedTime(it)
//            updateSeekBar()
//        })


    }


    private fun setPlayerControls() {

        /*play and pause*/
        binding.playImv.setOnClickListener {
            if (mediaPlayer?.isPlaying!!) {
                mediaPlayer?.pause()
                binding.playImv.setImageResource(R.drawable.ic_play)
            } else {
                if (currentPosition.div(1000) == mediaPlayer?.duration?.div(1000)) {
                    currentPosition = 0
                }
                mediaPlayer?.seekTo(currentPosition)
                mediaPlayer?.start()
                binding.playImv.setImageResource(R.drawable.ic_pause)

            }
        }

        /*go to next song*/
        binding.goToNext.setOnClickListener {
            if (isShuffled) {
                itemPosition = (0 until tempAudioList.size).random()

            } else {
                if (itemPosition < tempAudioList.size - 1) {
                    ++itemPosition
                } else {
                    itemPosition = 0
                }
            }
            goToNextAndPrevious(itemPosition)
        }

        /*go to prev song*/
        binding.goToPrev.setOnClickListener {
            if (isShuffled) {
                itemPosition = (0 until tempAudioList.size).random()
            } else if (itemPosition > 0) {
                --itemPosition
            }
            goToNextAndPrevious(itemPosition)
        }

        /*shuffling*/
        binding.shuffleImv.setOnClickListener {
            isShuffled = !isShuffled
            binding.shuffleImv.setImageResource(if (isShuffled) R.drawable.ic_shuffle_on else R.drawable.ic_shuffle_off)
        }

        /* repeat mode*/
        binding.repeatImv.setOnClickListener {
            isRepeat = !isRepeat
            binding.repeatImv.setImageResource(if (isRepeat) R.drawable.ic_repeat_on else R.drawable.ic_repeat_off)
        }

        val ss = Random(3)

        /*on end of songs*/
        mediaPlayer?.setOnCompletionListener {

            if (isRepeat) {
                it?.start()
            } else {
                binding.goToNext.performClick()
            }
        }
    }


    private fun goToNextAndPrevious(position: Int) {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }

        val directions = PlayerFragmentDirections.actionHomeFragmentToPlayerFragment(
            tempAudioList[position],
            position
        )

        findNavController().navigate(directions)


    }

    private fun updateSeekBar() {
        lifecycleScope.launch {
            delay(1000)
            try {
                val currentValue = mediaPlayer?.currentPosition!!.div(1000)
                currentPosition = mediaPlayer?.currentPosition!!
                liveData.value = currentValue

            } catch (e: Exception) {

            }

            Log.d("updatede", "runing ")
        }
    }

    private fun setGradientColor(uri: String) {
        lifecycleScope.launch {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val art = retriever.embeddedPicture
            if (art != null) {
                bitmapArt = BitmapFactory.decodeByteArray(art, 0, art.size)
                Palette.from(bitmapArt).generate {

                    val swatch = it!!.dominantSwatch

                    val gradientDrawable = GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        intArrayOf(swatch?.rgb ?: 0xff000000.toInt(), 0x00000000)
                    )

                    val gradientDrawableBg = GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        intArrayOf(
                            swatch?.rgb ?: 0xff000000.toInt(),
                            swatch?.rgb ?: 0xff000000.toInt()
                        )
                    )

                    binding.coverImage.foreground = gradientDrawable

                    binding.mContainer.background = gradientDrawableBg

                    binding.songNameTv.setTextColor(swatch!!.titleTextColor)
                    binding.artistNameTv.setTextColor(swatch.bodyTextColor)
                }
            }
        }

    }

    private fun formattedTime(current: Int): String {

        var totalOut = ""
        var totalNew = ""
        val seconds = current.rem(60)
        val minutes = current.div(60)

        totalOut = "$minutes:$seconds"
        totalNew = "$minutes:0$seconds"

        return if (seconds.toString().length == 1) {
            totalNew
        } else {
            totalOut
        }
    }


}