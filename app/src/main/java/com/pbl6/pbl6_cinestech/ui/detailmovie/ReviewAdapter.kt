package com.pbl6.pbl6_cinestech.ui.detailmovie

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.response.ReviewResponse
import com.pbl6.pbl6_cinestech.databinding.ListItemReviewBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import hoang.dqm.codebase.utils.loadImageSketch
import hoang.dqm.codebase.utils.singleClick
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ReviewAdapter(val movieId: String): BaseRecyclerViewAdapter<ReviewResponse, ListItemReviewBinding>() {
    var myAccountId: String =""
    fun setId(value: String){
        myAccountId = value
        notifyDataSetChanged()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindData(
        binding: ListItemReviewBinding,
        item: ReviewResponse,
        position: Int
    ) {
        binding.tvRated.text = context.getString(R.string.text_rate_5, item.rating)
        binding.imgReviewer.loadImageSketch(item.account.avatarUrl?:"")
        binding.name.text = item.account.fullName
        binding.time.text = getTimeAgo(item.createdAt)
        binding.tvReview.text = item.comment
        binding.remove.isVisible = item.account.id == myAccountId
        binding.remove.singleClick { listener?.invoke(movieId, position) }
        binding.rvSelected.setBackgroundResource(if (item.account.id == myAccountId) R.drawable.shape_bg_reschdule_selected else R.drawable.shape_bg_reschdule)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeAgo(isoTime: String): String {
        val formatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)
        val time = Instant.from(formatter.parse(isoTime))
        val now = Instant.now()
        val duration = Duration.between(time, now)

        return when {
            duration.toMinutes() < 1 -> "▪ s" // few seconds ago
            duration.toHours() < 1 -> "▪ ${duration.toMinutes()}m"
            duration.toDays() < 1 -> "▪ ${duration.toHours()}h"
            duration.toDays() < 7 -> "▪ ${duration.toDays()}d"
            duration.toDays() < 30 -> "▪ ${duration.toDays() / 7}w"
            duration.toDays() < 365 -> "▪ ${duration.toDays() / 30}mo"
            else -> "▪ ${duration.toDays() / 365}y"
        }
    }

    var listener: ((movieId: String, position: Int)-> Unit)? = null
    fun setOnRemove(listener: (movieId: String, position: Int)-> Unit){
        this.listener = listener
    }
}