package com.pbl6.pbl6_cinestech.ui.refreshments

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
    private val idShowTime by lazy {
        arguments?.getString("idShowTime") ?: ""
    }

    private val ticketsPrice by lazy {
        arguments?.getInt("ticketsPrice") ?: 0
    }
    private val refreshmentsAdapter: RefreshmentsAdapter by lazy {
        RefreshmentsAdapter()
    }
    override fun initView() {
        updateUI()
        setUpAdapter()
        setUpObserver()
    }

    override fun initListener() {
        binding.btnBack.singleClick {
            popBackStack()
        }
        onBackPressed { popBackStack() }
        binding.btnNext.singleClick {

        }
    }

    override fun initData() {
    }

    fun updateUI(){
        adjustInsetsForBottomNavigation(binding.btnBack)
        binding.ticketPrice.text = formatVND(ticketsPrice.toLong())
        binding.total.text = formatVND(ticketsPrice.toLong())
    }

    private fun setUpObserver(){
        viewModel.allRefreshmentsResultLiveData.observe(viewLifecycleOwner){value ->
            if (value?.success == true) {
                if (value.data == null) return@observe
                refreshmentsAdapter.setList(value.data.items)
            }
        }
        viewModel.price.observe(viewLifecycleOwner) { value ->
            binding.refreshmentsPrice.text = formatVND(value.toLong())
            binding.total.text = formatVND((value + ticketsPrice).toLong())
        }
    }

    private fun setUpAdapter() {
        refreshmentsAdapter.setOnClick({ item, position ->
            viewModel.add(item.price)
            this@RefreshmentsFragment.refreshmentsAdapter.addRefreshment(item.id, position)
        }) { item, position ->
            viewModel.minus(item.price)
            this@RefreshmentsFragment.refreshmentsAdapter.minusRefreshment(item.id, position)
        }
        binding.rvRefreshments.adapter = refreshmentsAdapter
        binding.rvRefreshments.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    fun formatVND(amount: Long) = "%,d₫".format(amount).replace(',', '.')

}