package com.pbl6.pbl6_cinestech.ui.refreshments

import com.pbl6.pbl6_cinestech.data.model.request.Refreshments
import com.pbl6.pbl6_cinestech.data.model.response.RefreshmentsResponse
import com.pbl6.pbl6_cinestech.databinding.ItemRefreshmentsBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import hoang.dqm.codebase.utils.loadImageSketch
import hoang.dqm.codebase.utils.singleClick

class RefreshmentsAdapter: BaseRecyclerViewAdapter<RefreshmentsResponse, ItemRefreshmentsBinding>() {
    private var listRefreshmentsOption: MutableList<Refreshments> = mutableListOf()
    fun addRefreshment(item: RefreshmentsResponse, position: Int) {
        listRefreshmentsOption.find { it.refreshmentId == item.id }?.let {
            it.quantity += 1
        }?: listRefreshmentsOption.add(Refreshments(item.id,1, item.price, item.name, item.picture))
        notifyItemChanged(position)
    }
    fun minusRefreshment(id: String, position: Int) {
        listRefreshmentsOption.find { it.refreshmentId == id }?.let { item ->
            if (item.quantity > 1) item.quantity -= 1
            else listRefreshmentsOption.remove(item)
        }
        notifyItemChanged(position)
    }

    fun getListRefreshments(): MutableList<Refreshments> {
        return listRefreshmentsOption
    }
    override fun bindData(
        binding: ItemRefreshmentsBinding,
        item: RefreshmentsResponse,
        position: Int
    ) {
        binding.btnAdd.singleClick {
            handleAdd?.invoke(item, position)
        }
        binding.btnMinus.singleClick {
            if (listRefreshmentsOption.find { it.refreshmentId == item.id } == null) return@singleClick
            handleMinus?.invoke(item, position)
        }
        binding.imgRefreshments.loadImageSketch(item.picture)
        binding.name.text = item.name
        binding.price.text = formatVND(item.price.toLong())
        binding.quantity.text = (listRefreshmentsOption.firstOrNull { it.refreshmentId == item.id }?.quantity ?: 0).toString()
    }

    private var handleAdd: ((item: RefreshmentsResponse, position: Int)-> Unit)? = null
    private var handleMinus: ((item: RefreshmentsResponse, position: Int)-> Unit)? = null
    fun setOnClick(handleAdd: (item: RefreshmentsResponse, position: Int) -> Unit, handleMinus: (item: RefreshmentsResponse, position: Int) -> Unit){
        this.handleAdd = handleAdd
        this.handleMinus = handleMinus
    }
    fun formatVND(amount: Long) = "%,d₫".format(amount).replace(',', '.')
}