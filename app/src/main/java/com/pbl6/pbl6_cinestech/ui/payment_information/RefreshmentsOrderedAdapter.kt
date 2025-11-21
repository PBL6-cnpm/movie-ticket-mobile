package com.pbl6.pbl6_cinestech.ui.payment_information

import com.pbl6.pbl6_cinestech.data.model.request.Refreshments
import com.pbl6.pbl6_cinestech.databinding.ItemRefreshmentsPaymentBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import hoang.dqm.codebase.utils.loadImageSketch

class RefreshmentsOrderedAdapter: BaseRecyclerViewAdapter<Refreshments, ItemRefreshmentsPaymentBinding>() {
    override fun bindData(
        binding: ItemRefreshmentsPaymentBinding,
        item: Refreshments,
        position: Int
    ) {
        binding.price.text = item.price.toString()
        binding.quantity.text ="x${item.quantity}"
        binding.name.text = item.name
        binding.imgRefreshments.loadImageSketch(item.imgPath)
    }
}