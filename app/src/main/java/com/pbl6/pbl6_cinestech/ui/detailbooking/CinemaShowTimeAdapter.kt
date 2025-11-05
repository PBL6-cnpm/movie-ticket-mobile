package com.pbl6.pbl6_cinestech.ui.detailbooking

import androidx.recyclerview.widget.GridLayoutManager
import com.pbl6.pbl6_cinestech.data.model.response.CinemaShowTime
import com.pbl6.pbl6_cinestech.databinding.ItemCinemaShowtimeBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter

class CinemaShowTimeAdapter(val duration: Int,val listener: (idShowTime: String) -> Unit ): BaseRecyclerViewAdapter<CinemaShowTime, ItemCinemaShowtimeBinding>() {
    override fun bindData(
        binding: ItemCinemaShowtimeBinding,
        item: CinemaShowTime,
        position: Int
    ) {
        binding.nameBranch.text = item.branch.name
        binding.tvDescription.text = "Favourite"
        binding.distance.text = "5km"
        binding.tvAddress.text = item.branch.address
        val showTimeAdapter =  ShowTimeAdapter(item.showTime.times, duration)
        binding.rvShowTime.adapter = showTimeAdapter
        showTimeAdapter.setOnClickItemRecyclerView { _, position ->
            listener.invoke(item.showTime.times[position].id)
        }
        binding.rvShowTime.layoutManager = GridLayoutManager(context, 3)
        binding.rvShowTime.setHasFixedSize(true)
        binding.rvShowTime.isNestedScrollingEnabled = false

    }

    fun setOnClickItem(listener: (position: Int) -> Unit) {
        setOnClickItemRecyclerView { pattern, position ->
            listener.invoke(position)
        }
    }
}