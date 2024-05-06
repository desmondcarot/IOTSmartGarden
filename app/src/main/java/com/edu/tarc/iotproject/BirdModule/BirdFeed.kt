package com.edu.tarc.iotproject.BirdModule

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.edu.tarc.iotproject.R
import com.edu.tarc.iotproject.databinding.ActivityBirdFeedBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat

class BirdFeed : AppCompatActivity() {
    private lateinit var binding: ActivityBirdFeedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBirdFeedBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dateFormat = SimpleDateFormat("MM-dd-yyyy HH:mm:ss")

        var db = Firebase.database.getReference()
        var BirdModule = db.child("Bird Module")

        BirdModule.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val birdEvents = mutableListOf<BirdEvent>()
                if (snapshot == null){
                    Toast.makeText(this@BirdFeed, "Data Has not been initialized in database", Toast.LENGTH_SHORT).show()
                    return
                }
                for (childSnapshot in snapshot.children) {
                    val birdEvent = childSnapshot.getValue(BirdEvent::class.java)
                    birdEvents.add(birdEvent!!)

                    Log.d("BIrd Event", birdEvent.distance.toString())
                }

                val latestEntry = birdEvents.maxByOrNull {
                    dateFormat.parse(it.timestamp)
                }

                binding.txtdistance.setText(latestEntry!!.distance.toString() + "cm")
                binding.txtsoundLevel.setText(latestEntry!!.sound_level.toString()+"dB")
                binding.txtfeedLevel.setText(latestEntry!!.feed_level)
                binding.txtStatus.setText(latestEntry!!.event)
                binding.txtCurrentTime.setText(latestEntry.timestamp)

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        binding.ManualFeeding.setOnClickListener{
            db.child("BirdModuleConfig").setValue(true)

            Toast.makeText(this, "Feeding Bird", Toast.LENGTH_SHORT).show()
        }

        binding.FeedingHistory.setOnClickListener {
            BirdModule.addValueEventListener(object: ValueEventListener {
                @SuppressLint("DefaultLocale")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val birdList = StringBuilder()
                    if (snapshot == null) {
                        Toast.makeText(
                            this@BirdFeed,
                            "Data Has not been initialized in database",
                            Toast.LENGTH_SHORT
                        ).show()
                        return
                    }
                    var x = 1
                    for (childSnapshot in snapshot.children) {
                        val birdEvent = childSnapshot.getValue(BirdEvent::class.java)!!
                        birdList.append(String.format("%d) Time: %s, sound level: %d, distance: %d, feed_level: %s, event: %s\n\n",
                            x,
                            birdEvent.timestamp,
                            birdEvent.sound_level,
                            birdEvent.distance,
                            birdEvent.feed_level,
                            birdEvent.event))
                        Log.d("BIrd Event", birdEvent.distance.toString())
                        x++
                    }

                    binding.txtHistoryList.text = birdList
                    binding.txtHistoryList.movementMethod = ScrollingMovementMethod.getInstance()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            )
        }
    }

    data class BirdEvent (
        val timestamp: String,
        val event: String,
        val sound_level: Int,
        val distance: Int,
        val feed_level: String,
    ){  constructor() : this("", "", 2, 1,"")
    }
}