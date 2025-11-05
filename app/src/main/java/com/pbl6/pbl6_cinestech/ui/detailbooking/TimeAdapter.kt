package com.pbl6.pbl6_cinestech.ui.detailbooking

import androidx.core.graphics.toColorInt
import com.pbl6.pbl6_cinestech.data.model.response.DayOfWeek
import com.pbl6.pbl6_cinestech.databinding.ItemShowTimeDayBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

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
    override fun bindData(
        binding: ItemShowTimeDayBinding,
        item: DayOfWeek,
        position: Int
    ) {
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



}