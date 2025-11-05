package com.pbl6.pbl6_cinestech.ui.refreshments

import com.pbl6.pbl6_cinestech.data.model.request.RefreshmentsOption
import com.pbl6.pbl6_cinestech.data.model.response.RefreshmentsResponse
import com.pbl6.pbl6_cinestech.databinding.ItemRefreshmentsBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import hoang.dqm.codebase.utils.loadImageSketch
import hoang.dqm.codebase.utils.singleClick

class RefreshmentsAdapter: BaseRecyclerViewAdapter<RefreshmentsResponse, ItemRefreshmentsBinding>() {
    private var listRefreshmentsOption: MutableList<RefreshmentsOption> = mutableListOf()
    fun addRefreshment(id: String, position: Int) {
        listRefreshmentsOption.find { it.refreshmentId == id }?.let {
            it.quantity += 1
        }?: listRefreshmentsOption.add(RefreshmentsOption(id,1))
        notifyItemChanged(position)
    }
    fun minusRefreshment(id: String, position: Int) {
        listRefreshmentsOption.find { it.refreshmentId == id }?.let { item ->
            if (item.quantity > 1) item.quantity -= 1
            else listRefreshmentsOption.remove(item)
        }
        notifyItemChanged(position)
    }
    override fun bindData(
        binding: ItemRefreshmentsBinding,
        item: RefreshmentsResponse,
        position: Int
    ) {
        binding.btnAdd.singleClick {
            handleAdd?.invoke()
        }
        binding.btnMinus.singleClick {
            handleMinus?.invoke()
        }
        binding.imgRefreshments.loadImageSketch(item.picture)
        binding.name.text = item.name
        binding.price.text = formatVND(item.price.toLong())
        binding.quantity.text = (listRefreshmentsOption.firstOrNull { it.refreshmentId == item.id }?.quantity ?: 0).toString()
    }

    private var handleAdd: (()-> Unit)? = null
    private var handleMinus: (()-> Unit)? = null
    fun setOnClick(handleAdd: () -> Unit, handleMinus: () -> Unit){
        this.handleAdd = handleAdd
        this.handleMinus = handleMinus
    }
    fun formatVND(amount: Long) = "%,d₫".format(amount).replace(',', '.')
}