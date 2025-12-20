package com.pbl6.pbl6_cinestech.ui.detailbooking

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import androidx.core.view.isVisible
import com.pbl6.pbl6_cinestech.data.model.response.DayOfWeek
import com.pbl6.pbl6_cinestech.databinding.ItemShowTimeDayBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.Locale

class TimeAdapter : BaseRecyclerViewAdapter<DayOfWeek, ItemShowTimeDayBinding>() {
    private var itemSelected = 0
    fun getDaySelected(): DayOfWeek{
        return dataList[itemSelected]
    }
    fun setSelected(value: Int) {
        if (itemSelected == value) return
        val oldValue = itemSelected
        itemSelected = value
        notifyItemChanged(oldValue)
        notifyItemChanged(value)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindData(
        binding: ItemShowTimeDayBinding,
        item: DayOfWeek,
        position: Int
    ) {
        binding.icNoMovie.isVisible = listDayOfShowTimeInList.none { day ->
            isSameDayVN(day, item.value)
        }
        if (position != itemSelected) {
            binding.item.strokeColor = "#737373".toColorInt()
            binding.day.setTextColor("#000000".toColorInt())
            binding.dayOfWeek.setTextColor("#989898".toColorInt())
            binding.dayOfWeek.setBackgroundColor("#33737373".toColorInt())
        }else{
            binding.item.strokeColor = "#FE7E32".toColorInt()
            binding.day.setTextColor("#FE7E32".toColorInt())
            binding.dayOfWeek.setTextColor("#FFFFFF".toColorInt())
            binding.dayOfWeek.setBackgroundColor("#FE7E32".toColorInt())
        }
        binding.day.text = getMonthDay(item.value)
        binding.dayOfWeek.text = item.name
    }

    fun setOnClickItem(listener: (position: Int) -> Unit) {
        setOnClickItemRecyclerView { pattern, position ->
            listener.invoke(position)
        }
    }

    fun getMonthDay(isoString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val date = inputFormat.parse(isoString)
        val outputFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        return outputFormat.format(date!!)
    }

    var listDayOfShowTimeInList = listOf<String>()
    fun setListDay(list: List<String>){
        listDayOfShowTimeInList = list
        notifyDataSetChanged()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun goToDayHasShowTime(): Int {
        val startIndex = itemSelected
        if (startIndex == -1) return -1

        val size = dataList.size

        for (i in startIndex until size) {
            val item = dataList[i]
            if (listDayOfShowTimeInList.any { day -> isSameDayVN(day, item.value) }) {
                itemSelected = i
                notifyDataSetChanged()
                return itemSelected
            }
        }

        for (i in 0 until startIndex) {
            val item = dataList[i]
            if (listDayOfShowTimeInList.any { day -> isSameDayVN(day, item.value) }) {
                itemSelected = i
                notifyDataSetChanged()
                return itemSelected
            }
        }
        return itemSelected
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isSameDayVN(dateStr1: String, dateStr2: String): Boolean {
        val vnZone = ZoneId.of("Asia/Ho_Chi_Minh")

        val d1 = OffsetDateTime.parse(dateStr1)
            .atZoneSameInstant(vnZone)
            .toLocalDate()

        val d2 = OffsetDateTime.parse(dateStr2)
            .atZoneSameInstant(vnZone)
            .toLocalDate()

        return d1 == d2
    }

}