package com.pbl6.pbl6_cinestech.ui.home

import android.accounts.Account
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.response.MovieResponse
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentHomeBinding
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import com.pbl6.pbl6_cinestech.utils.SecurePrefs
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.navigate
import hoang.dqm.codebase.data.ItemList
import hoang.dqm.codebase.utils.loadImageSketch
import hoang.dqm.codebase.utils.singleClick

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {
    override val viewModelFactory: ViewModelProvider.Factory
        get() = HomeViewModel.HomeViewModelFactory(
            RepositoryProvider.movieRepository,
            RepositoryProvider.reviewRepository,
            RepositoryProvider.authRepository
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

    }

    fun setUpAdapter() {
        adapterNowShowing.setOnClickItem { position ->
            val movieSelected = this@HomeFragment.adapterNowShowing.getItem(position)
            if (movieSelected is ItemList.DataItem<MovieResponse>) {
                val realItem = movieSelected.item
                Log.d("check movieSelected", "setUpAdapter: ${realItem.id}")
                // navigate detail
                val bundle = Bundle().apply {
                    putString("movieId", realItem.id)
                }
                mainViewModel.setMovieSelected(realItem)
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
        mainViewModel.isLoginLiveData.observe(viewLifecycleOwner) { value ->
            if (value) {
                binding.btnLogin.setImageResource(R.drawable.shape_bg_verify)
                viewModel.getAccountResult()
            }
        }
        viewModel.accountResultLiveData.observe(viewLifecycleOwner){value ->
            if (value?.success == true){
                if (value.data == null) return@observe
                value.data.avatarUrl?.let {
                    binding.avatar.loadImageSketch(it)
                }
                mainViewModel.setAccount(value.data)
                binding.btnLogin.isVisible = false
                binding.tvLog.isVisible = false
                binding.avatar.isVisible = true
                binding.name.text = value?.data?.fullName
                binding.email.text = value?.data?.email
            }
        }
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

        binding.avatar.singleClick {
            binding.optionBoard.isVisible = !binding.optionBoard.isVisible
        }
        binding.btnProfile.singleClick {
            navigate(R.id.profileFragment)
        }
        binding.btnBookingHistory.singleClick {
            navigate(R.id.bookingHistoryFragment)
        }
        binding.btnLogout.singleClick {
            mainViewModel.setLogin(false)
            mainViewModel.setAccount(null)
            SecurePrefs.clear(requireContext())
            navigate(R.id.loginFragment)
        }
    }

    override fun initData() {
    }
}