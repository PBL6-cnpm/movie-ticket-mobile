package com.pbl6.pbl6_cinestech.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.response.MovieResponse
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentHomeBinding
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.navigate
import hoang.dqm.codebase.data.ItemList
import hoang.dqm.codebase.utils.singleClick

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override val viewModelFactory: ViewModelProvider.Factory
        get() = HomeViewModel.HomeViewModelFactory(
            RepositoryProvider.movieRepository,
            RepositoryProvider.reviewRepository
        )
    private val mainViewModel: MainViewModel by activityViewModels()
    private val adapterNowShowing: MovieAdapter by lazy {
        MovieAdapter()
    }
    private val adapterUpComing: MovieAdapter by lazy {
        MovieAdapter(isShowing = false)
    }

    private val adapterReview: ReviewAdapter by lazy {
        ReviewAdapter()
    }

    override fun initView() {
        adjustInsetsForBottomNavigation(binding.top)
        setUpAdapter()
        setUpObserver()
        mainViewModel.isLoginLiveData.observe(viewLifecycleOwner) { value ->
            if (value) binding.btnLogin.setImageResource(R.drawable.shape_bg_verify)
        }
    }

    fun setUpAdapter() {
        adapterNowShowing.setOnClickItem { position ->
            val movieSelected = this@HomeFragment.adapterNowShowing.getItem(position)
            if (movieSelected is ItemList.DataItem<MovieResponse>) {
                val realItem = movieSelected.item
                // navigate detail
                val bundle = Bundle().apply {
                    putString("movieId", realItem.id)
                }
                navigate(R.id.detailMovieFragment, bundle)
            } else {
                // navigate list movie
            }
        }
        binding.rvNowShowing.adapter = adapterNowShowing
        binding.rvNowShowing.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        adapterUpComing.setOnClickItem { position ->
            val movieSelected = this@HomeFragment.adapterUpComing.getItem(position)
            if (movieSelected is ItemList.DataItem<MovieResponse>) {
                val realItem = movieSelected.item
                // navigate detail
            } else {
                // navigate list movie
            }
        }
        binding.rvUpComing.adapter = adapterUpComing
        binding.rvUpComing.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )

        adapterReview.setOnClickItem { position ->
            val itemSelected = this@HomeFragment.adapterReview.getItem(position)
            //navigate detail
            val bundle = Bundle().apply {
                putString("movieId", itemSelected.movie.id)
            }
            navigate(R.id.detailMovieFragment, bundle)
        }
        binding.rvReview.adapter = adapterReview
        binding.rvReview.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    fun setUpObserver() {
        viewModel.movieNowShowingResultLiveData.observe(viewLifecycleOwner) { value ->
            if (value?.success == true) {
                if (value.data == null) return@observe
                val data = value.data.items.map {
                    ItemList.DataItem(it)
                }
                val difSize = 1
                val difList = List(difSize) { ItemList.Placeholder as ItemList<MovieResponse> }
                val combinedList = mutableListOf<ItemList<MovieResponse>>()
                combinedList.addAll(data)
                combinedList.addAll(difList)
                this.adapterNowShowing.setList(combinedList)
            }
        }
        viewModel.movieUpComingResultLiveData.observe(viewLifecycleOwner) { value ->
            if (value?.success == true) {
                if (value.data == null) return@observe
                val data = value.data.items.map {
                    ItemList.DataItem(it)
                }
                val difSize = 1
                val difList = List(difSize) { ItemList.Placeholder as ItemList<MovieResponse> }
                val combinedList = mutableListOf<ItemList<MovieResponse>>()
                combinedList.addAll(data)
                combinedList.addAll(difList)
                this.adapterUpComing.setList(combinedList)
            }
        }
        viewModel.reviewResultLiveData.observe(viewLifecycleOwner) { value ->
            if (value?.success == true) {
                adapterReview.setList(value.data)
            }
        }
    }

    override fun initListener() {
        binding.btnLogin.singleClick {
            navigate(R.id.loginFragment)
        }
    }

    override fun initData() {
    }
}