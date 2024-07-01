package araya.gonzalo.reproductorayudantia

import android.animation.ObjectAnimator
import android.media.Image
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceHolder
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import araya.gonzalo.reproductorayudantia.AppConstants.Companion.CURRENT_SONG_INDEX
import araya.gonzalo.reproductorayudantia.AppConstants.Companion.LOG_MAIN_ACTIVITY
import araya.gonzalo.reproductorayudantia.AppConstants.Companion.MEDIA_PLAYER_POSITION
import araya.gonzalo.reproductorayudantia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var binding:ActivityMainBinding
    private var isPlaying: Boolean = false
    private var position: Int = 0
    private lateinit var currentSong: Song
    private var currentSongIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentSong = AppConstants.songs[currentSongIndex]

        Log.i(LOG_MAIN_ACTIVITY, "onCreate()")
        Toast.makeText(this, "estoy en onCreate", Toast.LENGTH_LONG).show()
        binding.imageView3.setOnClickListener {
            playOrPauseMusic()
        }
        binding.imageView4.setOnClickListener {
            playNextSong()
        }
        binding.imageView2.setOnClickListener {
            playPreviousSong()
        }
        savedInstanceState?.let {
            position = it.getInt(MEDIA_PLAYER_POSITION)
            currentSongIndex = it.getInt(CURRENT_SONG_INDEX)
            currentSong = AppConstants.songs[currentSongIndex]
        }
        updateUiSong()
    }



    override fun onStart() {
        super.onStart()
        Log.i(LOG_MAIN_ACTIVITY, "onStart()")
        mediaPlayer = MediaPlayer.create(this,currentSong.audioResId)
        progressSeekBar()
        if(isPlaying) {
            mediaPlayer?.start()
        }

        val movingTextView = findViewById<TextView>(R.id.textView2)
        val screenWidth = resources.displayMetrics.widthPixels // Get screen width

        val animation = ObjectAnimator.ofFloat(
            movingTextView,
            "translationX",
            -movingTextView.width.toFloat(), // Start off screen to the left
            screenWidth.toFloat() // End off screen to the right
        )
        animation.duration = 5000 // Animation duration in milliseconds
        animation.repeatCount = ObjectAnimator.INFINITE // Repeat indefinitely
        animation.repeatMode = ObjectAnimator.RESTART // Restart from beginning
        animation.interpolator = LinearInterpolator() // Use linear interpolation for smooth movement
        animation.start()
    }

    override fun onResume() {
        super.onResume()
        Log.i(LOG_MAIN_ACTIVITY, "onResume()")
        if(isPlaying) {
            mediaPlayer?.seekTo(position)
            mediaPlayer?.start()
            isPlaying = !isPlaying
        }
    }

    override fun onPause() {
        super.onPause()
        Log.i(LOG_MAIN_ACTIVITY, "onPause()")
        if(mediaPlayer != null)
            position = mediaPlayer!!.currentPosition
        mediaPlayer?.pause()
    }

    override fun onStop() {
        super.onStop()
        Log.i(LOG_MAIN_ACTIVITY, "onStop()")
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(LOG_MAIN_ACTIVITY, "onRestart()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(LOG_MAIN_ACTIVITY, "onDestroy()")
        Toast.makeText(this, "estoy en onDestroy", Toast.LENGTH_LONG).show()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(LOG_MAIN_ACTIVITY, "onSaveInstanceState")
        Toast.makeText(this, "onSaveInstanceState", Toast.LENGTH_LONG).show()
        outState.putInt(MEDIA_PLAYER_POSITION,position)
        outState.putInt(CURRENT_SONG_INDEX,currentSongIndex)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.i(LOG_MAIN_ACTIVITY, "onSaveInstanceState")
        Toast.makeText(this, "onSaveInstanceState", Toast.LENGTH_LONG).show()
        position = savedInstanceState.getInt(MEDIA_PLAYER_POSITION)
        currentSongIndex = savedInstanceState.getInt(CURRENT_SONG_INDEX)
        currentSong = AppConstants.songs[currentSongIndex]
        mediaPlayer?.seekTo(position)
      //  mediaPlayer?.start()
        playOrPauseMusic()
        updatePlayPauseButton()
    }

    private fun updateUiSong(){
        binding.textView2.text = currentSong.title
        binding.imageView.setImageResource(currentSong.imageResId)

    }

    fun playOrPauseMusic() {
        if(isPlaying){
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
        }
        isPlaying = !isPlaying
        updatePlayPauseButton()
    }

    private fun updatePlayPauseButton(){
        if(isPlaying){
            binding.imageView3.setImageResource(R.drawable.pause)
        } else{
            binding.imageView3.setImageResource(R.drawable.play)
        }
    }
    private fun playNextSong(){
        currentSongIndex = (currentSongIndex + 1) % AppConstants.songs.size
        currentSong = AppConstants.songs[currentSongIndex]
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this,currentSong.audioResId)
        mediaPlayer?.start()
        isPlaying = true
        updateUiSong()
        updatePlayPauseButton()
    }
    private fun playPreviousSong(){
        if(currentSongIndex == 0){
            currentSongIndex = AppConstants.songs.size - 1
        }
        currentSongIndex = (currentSongIndex - 1) % AppConstants.songs.size
        currentSong = AppConstants.songs[currentSongIndex]
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this,currentSong.audioResId)
        mediaPlayer?.start()
        isPlaying = true
        updateUiSong()
        updatePlayPauseButton()
    }

    fun progressSeekBar(){
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        progressBar.max = mediaPlayer!!.duration // Set max progress to media duration
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                progressBar.progress = mediaPlayer!!.currentPosition
                handler.postDelayed(this, 1000) // Update every 1 second
            }
        }
        handler.postDelayed(runnable, 1000)
    }

}

