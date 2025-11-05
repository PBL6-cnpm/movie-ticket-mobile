package com.pbl6.pbl6_cinestech.ui.refreshments

import androidx.lifecycle.ViewModelProvider
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentRefreshmentsBinding
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.onBackPressed
import hoang.dqm.codebase.base.activity.popBackStack
import hoang.dqm.codebase.utils.singleClick


class RefreshmentsFragment : BaseFragment<FragmentRefreshmentsBinding, RefreshmentsViewModel>() {
    override val viewModelFactory: ViewModelProvider.Factory
        get() = RefreshmentsViewModel.RefreshmentsViewModelFactory(
            RepositoryProvider.refreshmentsRepository
        )
    override fun initView() {
        setUpObserver()
    }

    override fun initListener() {
        binding.btnBack.singleClick {
            popBackStack()
        }
        onBackPressed { popBackStack() }
    }

    override fun initData() {
    }

    private fun setUpObserver(){
        viewModel.allRefreshmentsResultLiveData.observe(viewLifecycleOwner){value ->

        }
    }
}