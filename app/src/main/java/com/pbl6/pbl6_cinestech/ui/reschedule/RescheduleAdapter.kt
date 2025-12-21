package com.pbl6.pbl6_cinestech.ui.reschedule

import android.os.Build
import androidx.annotation.RequiresApi
import com.pbl6.pbl6_cinestech.data.model.response.Time
import com.pbl6.pbl6_cinestech.databinding.ItemRescheduleBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class RescheduleAdapter(val duration: Int = 120, val branchName: String): BaseRecyclerViewAdapter<Time, ItemRescheduleBinding>() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindData(
        binding: ItemRescheduleBinding,
        item: Time,
        position: Int
    ) {
        binding.branchName.text = branchName
        binding.timeStart.text = convertTo24Hour(item.time)
        binding.timeEnd.text = buildString {
            append("~")
            append(getEndTime(item.time, duration))
        }
        binding.availableSeat.text = "Available seats: ${item.availableSeats}/${item.totalSeats}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertTo24Hour(time12h: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
        val outputFormat = DateTimeFormatter.ofPattern("HH:mm")
        val time = LocalTime.parse(time12h.trim().uppercase(), inputFormat)
        return time.format(outputFormat)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEndTime(startTime: String, durationMinutes: Int): String {
        val inputFormat = DateTimeFormatter.ofPattern("h:mm a",  Locale.US)
        val outputFormat = DateTimeFormatter.ofPattern("HH:mm")
        val start = LocalTime.parse(startTime.trim().uppercase(), inputFormat)
        val end = start.plusMinutes(durationMinutes.toLong())
        return end.format(outputFormat)
    }

    fun setOnClickItem(listener: (item: Time, position: Int)-> Unit){
        setOnClickItemRecyclerView { pattern, position ->
            listener.invoke(pattern, position)
        }
    }
}