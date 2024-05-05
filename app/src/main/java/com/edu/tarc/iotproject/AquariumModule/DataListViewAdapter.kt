package com.edu.tarc.iotproject.AquariumModule

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edu.tarc.iotproject.databinding.AquariumListLayoutBinding
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale

class DataListViewAdapter(context: Context, private val data: List<AquariumData>):
    RecyclerView.Adapter<DataListViewAdapter.ViewHolder>(){

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AquariumListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(private val binding: AquariumListLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AquariumData) {
            val inputDate = data.id!!
            val outputFormat = "dd MMMM yyyy 'Time:' HH:mm:ss" // Desired output format
            val outputDateFormat = SimpleDateFormat(outputFormat, Locale.getDefault())
            val inputDateFormat = SimpleDateFormat("ddMMyyyyHHmmss", Locale.getDefault())
            val date = inputDateFormat.parse(inputDate)
            val formattedDate = outputDateFormat.format(date)
            binding.textViewDate.text = formattedDate

            Log.d("Nice", "${data.humidity} ${data.id}")
            Log.d("Not Nice", "${data.temperature} ${data.id}")

            if (data.humidity == 999){
                binding.textViewHumidity.text = String.format("Humidity: Not Captured")
            }else{
                binding.textViewHumidity.text = String.format("Humidity: %d%%", data.humidity)
            }

            if (data.temperature == 999){
                binding.txtViewTemperature.text = String.format("Temperature: Not Captured")
            }else if (data.temperature is Int){
                binding.txtViewTemperature.text = String.format("Temperature: %d%%", data.temperature)
                Log.d("img showing", "${data.temperature}")
            }

            if (data.img.equals("Not Captured")){
                binding.btnShowImg.visibility = View.GONE
                binding.txtNoImg.visibility = View.VISIBLE
                binding.capturedImg.visibility = View.GONE
            }else{
                binding.btnShowImg.visibility = View.VISIBLE
                binding.txtNoImg.visibility = View.GONE
            }

            binding.btnShowImg.setOnClickListener{
                if (binding.btnShowImg.text == "View Image"){
                    binding.btnShowImg.text = "Hide"
                    Log.d("img showing", "${data.img}")
                    val storage = FirebaseStorage.getInstance()
                    val capimg  = storage.reference.child("images").child(data.img!!)
                    binding.capturedImg.visibility = View.VISIBLE
                    Glide.with(binding.root)
                        .load(capimg)
                        .into(binding.capturedImg)
                }else{
                    binding.capturedImg.visibility = View.GONE
                    binding.btnShowImg.text = "View Image"
                }
            }
        }

    }
}