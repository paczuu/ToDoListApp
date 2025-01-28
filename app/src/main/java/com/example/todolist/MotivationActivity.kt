package com.example.todolist

import android.net.Uri
import android.os.Bundle
import android.content.Intent
import android.content.Context
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityMotivationBinding

class MotivationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMotivationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMotivationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ustaw focus na audio
        requestAudioFocus()

        // Ustawienie pliku wideo
        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.speech}")
        binding.videoView.setVideoURI(videoUri)

        binding.videoView.setOnCompletionListener {
            // Po zakończeniu filmu wróć do MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.videoView.start()
    }

    private fun requestAudioFocus() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val result = audioManager.requestAudioFocus(
            null, // Nie potrzebujemy AudioManager.OnAudioFocusChangeListener
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
        )

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Focus na audio został przyznany
        }
    }
}