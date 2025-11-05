package com.pbl6.pbl6_cinestech.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.response.GenreResponse
import com.pbl6.pbl6_cinestech.data.model.response.MovieResponse
import com.pbl6.pbl6_cinestech.databinding.ItemMovieBinding
import hoang.dqm.codebase.base.adapter.BaseRecyclerViewItemAdapter
import hoang.dqm.codebase.utils.loadImageSketch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MovieAdapter(var isShowing: Boolean = true) :
    BaseRecyclerViewItemAdapter<MovieResponse, ItemMovieBinding>() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun bindData(
        binding: ItemMovieBinding,
        item: MovieResponse,
        position: Int
    ) {
        binding.img.loadImageSketch(item.poster)
        binding.tvRate.isVisible = isShowing
        binding.tvAdditional.text = if (isShowing) {
            if (item.rated != null) "⭐ ${item.rated}/5"
            else context.getString(R.string.text_no_reviews_yet)
        } else convertDayShowing(item.screeningStart!!)
        binding.tvAdditional.isVisible = !isShowing
        binding.tvNameMovie.text = item.name
        binding.tvGenre.text = genresToString(item.genres)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertDayShowing(dayShowing: String): String {
        val dateTime = ZonedDateTime.parse(dayShowing)
        val formatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)
        val formattedDate = dateTime.format(formatter)
        return formattedDate
    }

    fun genresToString(genres: List<GenreResponse>): String {
        return genres.joinToString(", ") { it -> it.name }
    }

    fun setOnClickItem(listener: (position: Int) -> Unit) {
        setOnClickItemRecyclerView { pattern, position ->
            listener.invoke(position)
        }
    }
}