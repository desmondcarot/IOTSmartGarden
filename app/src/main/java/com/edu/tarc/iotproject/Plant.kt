package com.edu.tarc.iotproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edu.tarc.iotproject.databinding.ActivityLightBinding
import com.edu.tarc.iotproject.databinding.ActivityPlantBinding

class Plant : AppCompatActivity() {
    private lateinit var binding: ActivityPlantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_plant)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}