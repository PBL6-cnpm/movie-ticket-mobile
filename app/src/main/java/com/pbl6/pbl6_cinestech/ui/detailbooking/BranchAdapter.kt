package com.pbl6.pbl6_cinestech.ui.detailbooking

import androidx.core.graphics.toColorInt
import com.pbl6.pbl6_cinestech.data.model.response.BranchResponse
import com.pbl6.pbl6_cinestech.databinding.ItemBranchBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import hoang.dqm.codebase.utils.loadImageSketch

class BranchAdapter: BaseRecyclerViewAdapter<BranchResponse, ItemBranchBinding>() {
    private var itemSelected: Int = 0
    fun setSelected(value : Int){
        if (itemSelected==value) return
        val oldValue = itemSelected
        itemSelected = value
        notifyItemChanged(oldValue)
        notifyItemChanged(itemSelected)

    }

    fun getSelectedBranch(): BranchResponse {
        return dataList[itemSelected]
    }
    override fun bindData(
        binding: ItemBranchBinding,
        item: BranchResponse,
        position: Int
    ) {
        if (position == itemSelected) {
            binding.cardImg.strokeColor = "#E86017".toColorInt()
            binding.name.setTextColor("#E86017".toColorInt())
        }else {
            binding.cardImg.strokeColor = "#737373".toColorInt()
            binding.name.setTextColor("#737373".toColorInt())
        }
        item.imgPath?.let {
            binding.img.loadImageSketch(it)
        }
        binding.name.text = item.name.substringBefore(" ")
    }

    fun setOnClickItem(listener: (position: Int) -> Unit) {
        setOnClickItemRecyclerView { pattern, position ->
            listener.invoke(position)
        }
    }
}