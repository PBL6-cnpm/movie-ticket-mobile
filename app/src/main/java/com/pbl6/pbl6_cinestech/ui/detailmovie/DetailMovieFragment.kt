package com.pbl6.pbl6_cinestech.ui.detailmovie

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.response.GenreResponse
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentDetailMovieBinding
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.navigate
import hoang.dqm.codebase.base.activity.popBackStack
import hoang.dqm.codebase.utils.loadImageSketch
import hoang.dqm.codebase.utils.singleClick
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class DetailMovieFragment : BaseFragment<FragmentDetailMovieBinding, DetailMovieViewModel>() {
    private val mainViewModel by activityViewModels <MainViewModel>()

    override val viewModelFactory: ViewModelProvider.Factory
        get() = DetailMovieViewModel.DetailViewModelFactory(
            RepositoryProvider.movieRepository
        )
    private val idMovie: String by lazy {
        arguments?.getString("movieId") ?: ""
    }
    private val adapterActors: ActorAdapter by lazy {
        ActorAdapter()
    }

    override fun initView() {
        adjustInsetsForBottomNavigation(binding.btnBack)
        viewModel.getMovieDetail(idMovie)
        setUpAdapter()
        setUpObserver()
    }

    fun setUpAdapter() {
        binding.rvActor.adapter = adapterActors
        binding.rvActor.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    fun setUpObserver() {
        viewModel.movieDetailResultLiveData.observe(viewLifecycleOwner) { value ->
            if (value?.success == true) {
                if (value.data == null) return@observe
                binding.tvNameMovie.text = value.data.name
                binding.tvGenreMovie.text = genresToString(value.data.genres)
                binding.tvAgeDescription.text = getString(
                    R.string.text_this_movie_is_rated_pg_for_intense_action_sequences_and_some_language,
                    value.data.ageLimit
                )
                binding.ageMax.text = "${value.data.ageLimit}+"
                binding.imgMovie.loadImageSketch(value.data.poster)
                binding.btnTrailer.singleClick {
                    val bundle = bundleOf("video_id" to extractYoutubeId(value.data.trailer))
                    findNavController().navigate(
                        R.id.fullScreenVideoFragment,
                        bundle,
                        navOptions {
                            anim {
                                enter = hoang.dqm.codebase.R.anim.slide_in_up
                                exit = hoang.dqm.codebase.R.anim.slide_fade_out
                                popEnter = hoang.dqm.codebase.R.anim.slide_fade_in
                                popExit = hoang.dqm.codebase.R.anim.slide_out_down
                            }
                        }
                    )
                }
                binding.textContentScreeningDay.text = formatDate(value.data.screeningStart)
                binding.textContentDuration.text = formatMinutesToHourMinute(value.data.duration)
                binding.textContentLanguage.text = getString(R.string.text_subtitled)
                binding.movieStoryLine.text = value.data.description
                adapterActors.setList(value.data.actors)
                val videoId = extractYoutubeId(value.data.trailer)
                val thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
                binding.imgTrailer.loadImageSketch(thumbnailUrl)
            }
        }
    }

    override fun initListener() {
        binding.btnBooking.singleClick {
            val bundle = Bundle().apply {
                putString("movieId", idMovie)
                putInt("duration", viewModel.movieDetailResultLiveData.value?.data?.duration ?: 0)
            }
            navigate(R.id.detailBookingFragment, bundle)
        }
        binding.btnBack.singleClick {
            popBackStack()
        }
        binding.imgTrailer.singleClick {
            viewModel.movieDetailResultLiveData.value?.data?.trailer?.let {
                val bundle = bundleOf("video_id" to extractYoutubeId(it))
                findNavController().navigate(
                    R.id.fullScreenVideoFragment,
                    bundle,
                    navOptions {
                        anim {
                            enter = hoang.dqm.codebase.R.anim.slide_in_up
                            exit = hoang.dqm.codebase.R.anim.slide_fade_out
                            popEnter = hoang.dqm.codebase.R.anim.slide_fade_in
                            popExit = hoang.dqm.codebase.R.anim.slide_out_down
                        }
                    }
                )
            }
        }

    }

    override fun initData() {
    }

    fun genresToString(genres: List<GenreResponse>): String {
        return genres.joinToString(", ") { it -> it.name }
    }

    fun formatDate(input: String?): String {
        if (input.isNullOrEmpty()) return ""

        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")  // xử lý múi giờ Z (UTC)

            val date = isoFormat.parse(input)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            ""
        }
    }

    fun formatMinutesToHourMinute(minutes: Int?): String {
        if (minutes == null) return ""
        val hours = minutes / 60
        val mins = minutes % 60
        return when {
            hours > 0 && mins > 0 -> "${hours}h ${mins}m"
            hours > 0 -> "${hours}h"
            else -> "${mins}m"
        }
    }

    fun extractYoutubeId(url: String?): String {
        if (url.isNullOrEmpty()) return ""
        val pattern = "(?<=v=)[^#&?]*".toRegex()
        return pattern.find(url)?.value ?: ""
    }
}