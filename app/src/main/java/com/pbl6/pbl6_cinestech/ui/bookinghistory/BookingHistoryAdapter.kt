package com.pbl6.pbl6_cinestech.ui.bookinghistory

import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.response.BookingHistoryResponse
import com.pbl6.pbl6_cinestech.databinding.ItemBookingHistoryBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import hoang.dqm.codebase.utils.loadImageSketch
import hoang.dqm.codebase.utils.singleClick
import java.text.SimpleDateFormat
import java.util.Locale

class BookingHistoryAdapter: BaseRecyclerViewAdapter<BookingHistoryResponse, ItemBookingHistoryBinding>() {
    override fun bindData(
        binding: ItemBookingHistoryBinding,
        item: BookingHistoryResponse,
        position: Int
    ) {
        binding.imgMovie.loadImageSketch(item.showTime.movie.poster)
        binding.movieName.text = item.showTime.movie.name
        binding.timeMovie.text = getDayMonthYearWithWeekday(item.showTime.timeStart)
        binding.room.text = item.seats.get(0).room.name
        binding.seat.text = item.seats.joinToString { it.name }
        binding.refreshments.text = item.refreshmentss.joinToString { it.name }
        binding.totalPrice.text = formatVND(item.totalBookingPrice.toLong())
        binding.btnSave.singleClick {
            listener?.invoke(item.qrUrl)
        }
        binding.btnSave.setImageResource(if (item.qrUrl == null)R.drawable.shape_bg_disable
        else R.drawable.shape_bg_enter)
    }

    var listener: ((qrUrl: String?)-> Unit)? = null
    fun setOnClickShowQrCode(listener: ((qrUrl: String?)-> Unit)?){
        this.listener = listener
    }

    fun formatVND(amount: Long) = "%,d₫".format(amount).replace(',', '.')

    fun getDayMonthYearWithWeekday(isoString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val date = inputFormat.parse(isoString)
        val outputFormat = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault())
        return outputFormat.format(date!!)
    }

}