package com.edu.tarc.iotproject

import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edu.tarc.iotproject.BirdModule.BirdFeed
import com.edu.tarc.iotproject.databinding.ActivityLightBinding
import com.edu.tarc.iotproject.databinding.ActivityPlantBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.protobuf.Value
import java.util.EventListener

class Plant : AppCompatActivity() {
    private lateinit var binding: ActivityPlantBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val db = Firebase.database.reference.child("Plant Module")
        db.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val plantcondition = snapshot.getValue(plantcondition::class.java)
                Log.d("plant", plantcondition.toString())
                updateUI(plantcondition!!)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        db.child("Datapoint").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val dataPoint = StringBuilder()
                if (snapshot == null){
                    Toast.makeText(this@Plant, "Data Has not been initialized in database",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                var x = snapshot.childrenCount
                for (childSnapshot in snapshot.children) {
                    val plantdatapoint = childSnapshot.getValue(plantDataPoint::class.java)!!
                    dataPoint.insert(0,plantdatapoint.toString() + "<br/><br/>")
                    dataPoint.insert(0,"<b><font color=\"#00AA00\">" + childSnapshot.key + "</font></b><br/>")

                    x--
                }
                binding.ListPlantData.movementMethod = ScrollingMovementMethod.getInstance()
                binding.ListPlantData.text = Html.fromHtml(dataPoint.toString(),Html.FROM_HTML_MODE_COMPACT)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    fun updateUI(p: plantcondition) {
        binding.txtHumidity.text = formatValueWithMessage(p.humidity.toFloat(), "%", "Humidity", 5f, 20f)
        binding.txtLight.text = formatValueWithMessage(p.light.toFloat(), "%", "Light level", 5f)
        binding.txtTemperature.text = formatValueWithMessage(p.temperature.toFloat(), "°C", "Temperature", 15f, 30f)
        binding.txtSoilMoisture.text = formatValueWithMessage(p.moisture.toFloat(), "%", "Soil moisture", 10f, 40f)
    }

    fun formatValueWithMessage(value: Float, unit: String, parameter: String, lowThreshold: Float, highThreshold: Float = Float.MAX_VALUE): String {
        val formattedValue = "%.1f".format(value)
        return if (value <= lowThreshold) {
            "$formattedValue$unit\n$parameter is too low"
        } else if (value >= highThreshold) {
            "$formattedValue$unit\n$parameter is too high"
        } else {
            "$formattedValue$unit\n$parameter is just nice"
        }
    }
}
data class plantcondition(
    val humidity: String = "",
    val light: String= "",
    val moisture: String= "",
    val temperature: String=""
){
    override fun toString(): String {
        return "PlantCondition(humidity='$humidity', light='$light', moisture='$moisture', temperature='$temperature')"
    }
}

data class plantDataPoint(
    var humidity: String = "",
    var light: String= "",
    var moisture: String= "",
    var temperature: String="",
){
    override fun toString(): String {
        return "humidity:'$humidity', light:'$light', moisture: $moisture, temperature: $temperature"
    }
}