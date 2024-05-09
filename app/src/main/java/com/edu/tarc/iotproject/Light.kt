package com.edu.tarc.iotproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edu.tarc.iotproject.databinding.ActivityLightBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class Light : AppCompatActivity() {
    private lateinit var binding: ActivityLightBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLightBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var onTime: String
        var offTime: String
        val db = Firebase.database.reference.child("Light Module")

        db.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lightSettings = snapshot.getValue(LightModule::class.java)
                Log.d("Light",lightSettings.toString())
                if (lightSettings != null) {
                    onTime = lightSettings.on_time
                    offTime = lightSettings.off_time
                    updateUI(lightSettings)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        binding.btnOnOff.setOnClickListener{
            if (binding.btnOnOff.text == "Turn Off"){
                db.child("always_on").setValue(false)
                //binding.btnOnOff.text = "Turn On"
                //binding.imgLight.setImageResource(R.drawable.light_off)

            }else{
                db.child("always_on").setValue(true)
                //binding.imgLight.setImageResource(R.drawable.light_on)
                //binding.btnOnOff.text = "Turn Off"
            }
        }

        binding.btnSaveOnTime.setOnClickListener{
            val onString = binding.etOnTime.text.toString()
            if (isValidTimeFormat(onString)){
                db.child("on_time").setValue(onString)
            }else{
                Toast.makeText(this, "Please Enter a valid time 'HH:MM'",Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSaveOffTime.setOnClickListener{
            val offString = binding.etOfftime.text.toString()
            if (isValidTimeFormat(offString)){
                db.child("off_time").setValue(offString)
            }else{
                Toast.makeText(this, "Please Enter a valid time 'HH:MM'",Toast.LENGTH_SHORT).show()
            }
        }


    }

    fun isValidTimeFormat(timeString: String): Boolean {
        val regex = Regex("^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$") // Regex pattern for "HH:MM" format
        return regex.matches(timeString)
    }

    fun updateUI(lightSettings: LightModule){
        if (lightSettings.always_on){
            binding.imgLight.setImageResource(R.drawable.light_on)
            binding.btnOnOff.text = "Turn Off"
        }else{
            binding.imgLight.setImageResource(R.drawable.light_off)
            binding.btnOnOff.text = "Turn On"
        }
        binding.etOnTime.setText(lightSettings.on_time)
        binding.etOfftime.setText(lightSettings.off_time)
    }

}



data class LightModule(
    val always_on: Boolean = true,
    val light_status: Boolean = true,
    val off_time: String = "",
    val on_time: String = ""
) {
    override fun toString(): String {
        return "LightModule(alwaysOn=$always_on, lightStatus=$light_status, offTime='$off_time', onTime='$on_time')"
    }
}