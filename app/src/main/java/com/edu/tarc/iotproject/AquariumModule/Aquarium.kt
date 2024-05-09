package com.edu.tarc.iotproject.AquariumModule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edu.tarc.iotproject.R
import com.edu.tarc.iotproject.databinding.ActivityAquariumBinding
import com.google.firebase.Firebase
import com.google.firebase.database.*

class Aquarium : AppCompatActivity() {

    private lateinit var binding: ActivityAquariumBinding
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAquariumBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = Firebase.database.reference.child("Aquarium Module").child("config")


        var sensitivity = resources.getStringArray(R.array.Sensitivity)
        var spinner = binding.spinner

        if (spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sensitivity)
            spinner.adapter = adapter
        }

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val config = snapshot.getValue(Config::class.java)
                Log.d("Config Log", "Config: $config")
                config?.let { updateUI(it) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Aquarium, "Nice bruh", Toast.LENGTH_SHORT).show()
            }
        })




        binding.btnDataHistory.setOnClickListener {
            startActivity(Intent(this, AquariumViewData::class.java))
        }

        binding.btnViewMotionCapture.setOnClickListener {
            startActivity(Intent(this, MotionCapActivity::class.java))
        }

        binding.swBuzzer.setOnCheckedChangeListener { _, isChecked -> updateConfig("buzzer_enabled", isChecked) }
        binding.swDHT.setOnCheckedChangeListener { _, isChecked -> updateConfig("dht_enabled", isChecked) }
        binding.swMotCap.setOnCheckedChangeListener{_, isChecked -> updateConfig("motion_cap_enabled", isChecked)}
        binding.swCapImg.setOnCheckedChangeListener{_, isChecked -> updateConfig("capture_img", isChecked)}
        binding.btnSetTemp.setOnClickListener { updateTemperature() }
        binding.btnSetHum.setOnClickListener { updateHumidity() }
        binding.btnSetInterval.setOnClickListener { updateInterval() }

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                updateConfig("motion_cap_sens", sensitivity[p2])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun updateConfig(key: String, value: Any) {
        db.child(key).setValue(value).addOnSuccessListener {
            Log.d("Config Changes", "Changed $key to $value")
        }
    }
    private fun updateTemperature() {
        val minTemp = binding.minTemp.text.toString().toDoubleOrNull() ?: return
        val maxTemp = binding.maxTemp.text.toString().toDoubleOrNull() ?: return

        if (minTemp >= maxTemp || minTemp < 0.0 || maxTemp > 50.0) {
            Toast.makeText(
                this@Aquarium,
                "Invalid temperature values",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        updateConfig("temp_max", maxTemp)
        updateConfig("temp_min", minTemp)
    }

    private fun updateHumidity() {
        val minHum = binding.minHumidity.text.toString().toIntOrNull() ?: return
        val maxHum = binding.maxHumidity.text.toString().toIntOrNull() ?: return

        if (minHum >= maxHum || minHum < 0 || minHum > 100) {
            Toast.makeText(
                this@Aquarium,
                "Invalid humidity values",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        updateConfig("hum_max", maxHum)
        updateConfig("hum_min", minHum)
    }

    private fun updateInterval() {
        val interval = binding.etInterval.text.toString().toIntOrNull() ?: return
        if (interval <= 0) {
            Toast.makeText(
                this@Aquarium,
                "Interval must be greater than 0",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        updateConfig("interval", interval)
    }

    private fun updateUI(config: Config) {
        with(binding) {
            swBuzzer.isChecked = config.buzzer_enabled ?: false
            swDHT.isChecked = config.dht_enabled ?: false
            minHumidity.setText(config.hum_min?.toString() ?: "")
            maxHumidity.setText(config.hum_max?.toString() ?: "")
            etInterval.setText(config.interval?.toString() ?: "")
            maxTemp.setText(config.temp_max?.toString() ?: "")
            minTemp.setText(config.temp_min?.toString() ?: "")
            swCapImg.isChecked = config.capture_img ?: false
            swMotCap.isChecked = config.motion_cap_enabled ?: false
            val selectedIndex = when (config.motion_cap_sens) {
                "Low" -> 0
                "High" -> 2
                else -> 1
            }

            Log.d("selected Index", selectedIndex.toString())
            spinner.setSelection(selectedIndex)
        }
    }

    data class Config(
        var buzzer_enabled: Boolean? = false,
        var dht_enabled: Boolean? = false,
        var hum_max: Int? = 0,
        var hum_min: Int? = 0,
        var interval: Int? = 0,
        var lastCaptureTime: String? = "",
        var temp_max: Int? = 0,
        var temp_min: Int? = 0,
        var motion_cap_enabled: Boolean? = false,
        var motion_cap_sens: String? = "",
        var capture_img: Boolean? = false
    )
}