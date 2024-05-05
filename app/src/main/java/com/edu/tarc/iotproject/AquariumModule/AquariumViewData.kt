package com.edu.tarc.iotproject.AquariumModule

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.edu.tarc.iotproject.R
import com.edu.tarc.iotproject.databinding.ActivityAquariumViewDataBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value

class AquariumViewData : AppCompatActivity() {

    private lateinit var binding: ActivityAquariumViewDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAquariumViewDataBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var data = mutableListOf<AquariumData>()

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Aquarium Module").child("data")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children){
                    var dataitem = item.getValue(AquariumData::class.java)
                    if (dataitem != null) {
                        dataitem.id = item.key
                    }
                    data.add(dataitem!!)
                }

                var adapter = DataListViewAdapter(this@AquariumViewData, data)
                binding.recDataList.layoutManager = LinearLayoutManager(this@AquariumViewData)
                binding.recDataList.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })










    }

}