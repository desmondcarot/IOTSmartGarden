package com.edu.tarc.iotproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edu.tarc.iotproject.databinding.ActivityAquariumBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

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
        var config: Config
        db = Firebase.database.reference

        db.child("Aquarium Module").child("config").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                config = snapshot.getValue(Config::class.java)!!
                Log.d("Config Log", "Config: $config")
                if(config != null){
                    binding.swBuzzer.isChecked = config.buzzer_enabled!!
                    binding.swDHT.isChecked = config.dht_enabled!!
                    binding.minHumidity.setText(config.hum_min!!.toString())
                    binding.maxHumidity.setText(config.hum_max!!.toString())
                    binding.etInterval.setText(config.interval!!.toString())
                    binding.maxTemp.setText(config.temperature_threshold!!.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Aquarium, "Nice bruh", Toast.LENGTH_SHORT).show()
            }
        })


        binding.swBuzzer.setOnCheckedChangeListener{ buttonView, isChecked ->

            if (isChecked){
                db.child("Aquarium Module").child("config").child("buzzer_enabled").setValue(true).addOnSuccessListener {
                    Log.d("Config Changes", "Changed to true")
                }
            }else{
                db.child("Aquarium Module").child("config").child("buzzer_enabled").setValue(false).addOnSuccessListener {
                    Log.d("Config Changes", "Changed to false")
                }
            }
        }

        binding.swDHT.setOnCheckedChangeListener{btnView, isChecked ->
            if (isChecked){
                db.child("Aquarium Module").child("config").child("dht_enabled").setValue(true).addOnSuccessListener {
                    Log.d("Config Changes", "Changed to true")
                }
            }else{
                db.child("Aquarium Module").child("config").child("dht_enabled").setValue(false).addOnSuccessListener {
                    Log.d("Config Changes", "Changed to false")
                }
            }
        }

        binding.btnSetTemp.setOnClickListener {
            val tempmin = binding.minTemp.text.toString()
            val tempmax = binding.maxTemp.text.toString()

            if (tempmin.isNotEmpty() && tempmax.isNotEmpty()) {
                val minTemp = tempmin.toDouble()
                val maxTemp = tempmax.toDouble()

                if (minTemp < 0.0 || maxTemp > 50.0) {
                    Toast.makeText(
                        this@Aquarium,
                        "Please Enter temperature threshold between 0°Cto 50°C",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                if (minTemp >= maxTemp) {
                    Toast.makeText(
                        this@Aquarium,
                        "Please make sure max temp is more than min temp",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                db.child("Aquarium Module").child("config").child("temperature_threshold").setValue(maxTemp)
                    .addOnSuccessListener {
                        Log.d("Config Changes", "hum max: $minTemp")
                    }
//                db.child("Aquarium Module").child("config").child("hum_min").setValue(maxTemp).addOnSuccessListener {
//                        Log.d("Config Changes", "hum min: $maxTemp")
//                    }

            } else {
                Toast.makeText(
                    this@Aquarium,
                    "Please make sure the field is not empty",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("Config Changes", "Min or max temp is empty")
            }
        }

        binding.btnSetHum.setOnClickListener{
            val hum_min = binding.minHumidity.text.toString()
            val hum_max = binding.maxHumidity.text.toString()


            if (hum_min.isNotEmpty() && hum_max.isNotEmpty()) {
                val minHum = hum_min.toInt()
                val maxHum = hum_max.toInt()

                if (minHum < 0 || minHum > 100){
                    Toast.makeText(this@Aquarium, "Please Enter humidity threshold between 0% to 100%", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (minHum >= maxHum){
                    Toast.makeText(this@Aquarium, "Please make sure max hum is more than min hum", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                db.child("Aquarium Module").child("config").child("hum_max").setValue(maxHum).addOnSuccessListener {
                    Log.d("Config Changes", "hum max: $minHum")
                }
                db.child("Aquarium Module").child("config").child("hum_min").setValue(minHum).addOnSuccessListener {
                    Log.d("Config Changes", "hum min: $maxHum")
                }

            } else {
                Toast.makeText(this@Aquarium, "Please make sure the field is not empty", Toast.LENGTH_SHORT).show()
                Log.d("Config Changes", "Min or max temp is empty")
            }
        }

        binding.btnSetInterval.setOnClickListener {
            val intervalString = binding.etInterval.text.toString()

            if (intervalString.isNotEmpty()){
                val interval = intervalString.toInt()

                if (interval <= 0){
                    Toast.makeText(this@Aquarium, "Please make sure interval is more than 0", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                db.child("Aquarium Module").child("config").child("interval").setValue(interval).addOnSuccessListener {
                    Log.d("Config Changes", "Interval: $interval")
                }

            }
        }
    }

    data class Config(
        var buzzer_enabled: Boolean? = null,
        var dht_enabled: Boolean? = null,
        var hum_max: Int? = null,
        var hum_min: Int? = null,
        var interval: Int? = null,
        var lastCaptureTime: String? = null,
        var temperature_threshold: Int? = null
    ) {
        constructor() : this(null, null, null, null, null, null, null)
    }
}