package com.edu.tarc.iotproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edu.tarc.iotproject.databinding.ActivityAquariumBinding
import com.edu.tarc.iotproject.databinding.ActivityBirdFeedBinding

class BirdFeed : AppCompatActivity() {

    private lateinit var binding: ActivityBirdFeedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBirdFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.birdTitle.setText("Nice")

        binding.birdTitle.setOnClickListener{
            binding.birdTitle.setText("three")
        }





    }
}