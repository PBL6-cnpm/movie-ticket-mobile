package com.pbl6.pbl6_cinestech.ui.payment_information

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.databinding.FragmentPaymentInformationBinding
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.utils.loadImageSketch

class PaymentInformationFragment : BaseFragment<FragmentPaymentInformationBinding, PaymentInformationViewModel>() {
    private val mainViewModel by activityViewModels <MainViewModel>()

    override fun initView() {
        updateUI()
    }

    override fun initListener() {
    }

    override fun initData() {
    }

    private fun updateUI(){
        adjustInsetsForBottomNavigation(binding.btnBack)
        val movie = mainViewModel.getMovieSelected()
        binding.imgMovie.loadImageSketch(movie.poster)
        binding.nameMovie.text = movie.name
//        binding.imgBranch.loadImageSketch()
//        binding.branchName.text = ""
        binding.ageMax.text = "${movie.ageLimit}+"
        binding.tvAgeDescription.text = getString(
            R.string.text_this_movie_is_rated_pg_for_intense_action_sequences_and_some_language,
            movie.ageLimit
        )
    }

    private fun setUpObserver(){

    }
}