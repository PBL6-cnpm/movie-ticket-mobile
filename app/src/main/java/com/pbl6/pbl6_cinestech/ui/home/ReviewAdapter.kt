package com.pbl6.pbl6_cinestech.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.response.ReviewResponse
import com.pbl6.pbl6_cinestech.databinding.ItemReviewBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewAdapter
import hoang.dqm.codebase.utils.loadImageSketch
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class ReviewAdapter : BaseRecyclerViewAdapter<ReviewResponse, ItemReviewBinding>() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindData(
        binding: ItemReviewBinding,
        item: ReviewResponse,
        position: Int
    ) {
        binding.tvTitle.text = context.getString(R.string.text_review, item.movie.name)
        binding.tvDescription.text = item.account.fullName
        binding.imgMovie.loadImageSketch(item.movie.poster)
        if (!item.account.avatarUrl.isNullOrEmpty()) binding.imgReviewer.loadImageSketch(item.account.avatarUrl!!)
        else binding.imgReviewer.setImageResource(R.drawable.avatar_default)
        binding.tvTime.text = getTimeAgo(item.createdAt)
        binding.tvRated.text = context.getString(R.string.text_rate_5, item.rating)
        binding.tvComment.text = item.comment
        binding.imgRated.setImageResource(
            when (item.rating){
                1 -> R.drawable.img_star_1
                2 -> R.drawable.img_star_2
                3 -> R.drawable.img_star_3
                4 -> R.drawable.img_star_4
                5 -> R.drawable.img_star_5
                else -> R.drawable.img_star_1
            }
        )
        binding.bgImgContentMovie.loadImageSketch(item.movie.poster)
        binding.tvTitleMovie.text = item.movie.name
        binding.tvGenre.text = item.movie.genres.joinToString(", ") { it.name }
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

    fun setOnClickItem(listener: (position: Int) -> Unit) {
        setOnClickItemRecyclerView { pattern, position ->
            listener.invoke(position)
        }
    }
}