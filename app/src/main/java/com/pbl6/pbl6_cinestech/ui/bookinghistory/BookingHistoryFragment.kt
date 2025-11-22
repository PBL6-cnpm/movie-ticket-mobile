package com.pbl6.pbl6_cinestech.ui.bookinghistory

import android.media.Image
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentBookingHistoryBinding
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.onBackPressed
import hoang.dqm.codebase.base.activity.popBackStack
import hoang.dqm.codebase.utils.loadImageSketch
import hoang.dqm.codebase.utils.singleClick


class BookingHistoryFragment :
    BaseFragment<FragmentBookingHistoryBinding, BookingHistoryViewModel>() {
    private val mainViewModel by activityViewModels<MainViewModel>()
    override val viewModelFactory: ViewModelProvider.Factory
        get() = BookingHistoryViewModel.BookingHistoryFactory(
            RepositoryProvider.bookingRepository
        )
    val adapter: BookingHistoryAdapter by lazy {
        BookingHistoryAdapter()
    }

    override fun initView() {
        adjustInsetsForBottomNavigation(binding.btnBack)
        setUpAdapter()
        setUpObserver()
    }

    fun setUpAdapter() {
        adapter.setOnClickShowQrCode { qrUrl ->
            if (qrUrl == null) {
                Toast.makeText(requireContext(), "QR Code Unavailable", Toast.LENGTH_SHORT).show()
            } else
                showDialogQr(qrUrl)
        }
        binding.rvHistory.adapter = adapter
        binding.rvHistory.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    fun setUpObserver() {
        viewModel.historyResultLiveData.observe(viewLifecycleOwner) { value ->
            if (value?.success == true) {
                if (value.data == null) return@observe
                value.data?.let {
                    adapter.setList(value.data.items)
                }
            }
        }
    }

    override fun initListener() {
        binding.btnBack.singleClick {
            popBackStack(R.id.homeFragment)
        }
        onBackPressed {
            popBackStack(R.id.homeFragment)
        }
    }

    override fun initData() {
    }

    fun showDialogQr(qrUrl: String) {
        if (!isAdded || isDetached || view == null) return
        context?.let { ctx ->
            val dialogView = layoutInflater.inflate(R.layout.dialog_show_qr, null)

            dialogView.findViewById<ImageView>(R.id.ic_dialog).loadImageSketch(qrUrl)

            val dialog = AlertDialog.Builder(ctx).setView(dialogView).create()
            dialog.window?.setBackgroundDrawableResource(R.color.black_88000000)
            dialog.show()
        }
    }
}