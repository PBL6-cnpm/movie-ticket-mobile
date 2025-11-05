package com.pbl6.pbl6_cinestech.ui.seatbooking

import androidx.lifecycle.ViewModelProvider
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentSeatBookingBinding
import hoang.dqm.codebase.base.activity.BaseFragment


class SeatBookingFragment : BaseFragment<FragmentSeatBookingBinding, SeatBookingViewModel>() {
    override val viewModelFactory: ViewModelProvider.Factory
        get() = SeatBookingViewModel.SeatBookingViewModelFactory(
            RepositoryProvider.seatRepository
        )
    private val idShowTime by lazy {
        arguments?.getString("idShowTime") ?: ""
    }

    private val branchName by lazy {
        arguments?.getString("branchName")?: ""
    }
    override fun initView() {
        adjustInsetsForBottomNavigation(binding.btnBack)
        viewModel.getAllSeatByShowTimeId(idShowTime)
        binding.branchName.text = branchName
        setUpObserver()
    }

    fun setUpObserver(){
        viewModel.allSeatResultLiveData.observe(viewLifecycleOwner){ value ->
            if (value?.success == true){
                if (value.data == null) return@observe
                val seatType = value.data.typeSeatList
                binding.seatView.setSeatsData(
                    value.data.seatLayout.rows,
                    value.data.seatLayout.cols,
                    value.data.seatLayout.seats,
                    value.data.seatLayout.occupiedSeats
                )
            }

        }
    }

    override fun initListener() {
    }

    override fun initData() {
    }
}