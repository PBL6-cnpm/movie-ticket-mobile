package com.pbl6.pbl6_cinestech.ui.voucher_page

import android.view.View
import androidx.core.view.isVisible
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.response.VoucherResponse
import com.pbl6.pbl6_cinestech.databinding.ItemVoucherBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import hoang.dqm.codebase.utils.singleClick

class VoucherAdapter(val currentPrice: Int) : BaseRecyclerViewAdapter<VoucherResponse, ItemVoucherBinding>() {
    private var itemSelected: Int? = null
    fun setItemSelected(position: Int) {
        val oldValue = itemSelected
        itemSelected = position
        oldValue?.let { notifyItemChanged(it) }
        notifyItemChanged(position)
    }

    fun getItemSelected(): VoucherResponse? {
        return itemSelected?.let { dataList[it] }
    }

    override fun bindData(
        binding: ItemVoucherBinding,
        item: VoucherResponse,
        position: Int
    ) {
        binding.icVoucher.setImageResource(if (item.discountValue == null) R.drawable.ic_precent else R.drawable.ic_dollar)
        binding.nameVoucher.text = item.name
        binding.codeVoucher.text = item.code
        binding.discountVoucher.text =
            if (item.discountValue == null) "${item.discountPercent}% (Max: ${item.maxDiscountValue}k)" else "${item.discountValue}k"
        binding.icSelected.setImageResource(if (position == itemSelected) R.drawable.ic_check else R.drawable.shape_circle)
        binding.icInforVoucher.singleClick {
            binding.inforMessage.visibility =
                if (binding.inforMessage.isVisible) View.GONE else View.VISIBLE
        }
        binding.inforMessage.text =
            if (item.minimumOrderValue != null && item.minimumOrderValue != 0) "The voucher is only applicable to orders from ${item.minimumOrderValue}k."
            else "This voucher is valid for all purchases."
        binding.informationVoucher.text = "Minimum order required: ${item.minimumOrderValue}k"
        if (item.minimumOrderValue != null  && item.minimumOrderValue != 0 && currentPrice < (item.minimumOrderValue?:0)){
            binding.informationVoucher.visibility = View.VISIBLE
        } else {
            binding.informationVoucher.visibility = View.GONE
        }
    }

    fun setOnClickItem(listener: (position: Int) -> Unit) {
        setOnClickItemRecyclerView { voucher, position ->
            listener.invoke(position)
        }
    }
}