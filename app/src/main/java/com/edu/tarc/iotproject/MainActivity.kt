package com.edu.tarc.iotproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edu.tarc.iotproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnSmartPond.setOnClickListener{
            val intent = Intent(this, Aquarium::class.java)
            startActivity(intent)
        }

        binding.btnSmartLight.setOnClickListener{
            val intent = Intent(this, Light::class.java)
            startActivity(intent)
        }

        binding.btnSmartBirdFeed.setOnClickListener{
            val intent = Intent(this, BirdFeed::class.java)
            startActivity(intent)
        }

        binding.btnSmartPlant.setOnClickListener{
            val intent = Intent(this, Plant::class.java)
            startActivity(intent)
        }


    }
}