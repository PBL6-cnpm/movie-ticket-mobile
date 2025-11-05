package com.pbl6.pbl6_cinestech.ui.detailbooking

import android.os.Build
import androidx.annotation.RequiresApi
import com.pbl6.pbl6_cinestech.data.model.response.ShowTimeResponse
import com.pbl6.pbl6_cinestech.data.model.response.Time
import com.pbl6.pbl6_cinestech.databinding.ItemShowtimeBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class ShowTimeAdapter(listTime: List<Time>,val duration: Int): BaseRecyclerViewAdapter<Time, ItemShowtimeBinding>() {
    init {
         setList(listTime.toMutableList())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindData(
        binding: ItemShowtimeBinding,
        item: Time,
        position: Int
    ) {
        binding.timeStart.text = convertTo24Hour(item.time)
        binding.timeEnd.text = buildString {
            append("~")
            append(getEndTime(item.time, duration))
        }
        binding.slot.text = "17/21 left"
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
}