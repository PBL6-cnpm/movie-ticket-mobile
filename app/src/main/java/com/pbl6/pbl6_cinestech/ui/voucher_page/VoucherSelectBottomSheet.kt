package com.pbl6.pbl6_cinestech.ui.voucher_page

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.response.VoucherResponse
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.BottomSheetLayoutBinding
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import hoang.dqm.codebase.utils.singleClick

class VoucherSelectBottomSheet(val currentPrice: Int) : BottomSheetDialogFragment() {
    private val mainViewModel by activityViewModels <MainViewModel>()

    private var adapter: VoucherAdapter? = null

    private val viewModel: VoucherViewModel by lazy {
        val factory = VoucherViewModel.VoucherViewModelFactory(
            RepositoryProvider.bookingRepository
        )
        ViewModelProvider(this, factory)[VoucherViewModel::class.java]
    }

    private var _binding: BottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet = (dialogInterface as BottomSheetDialog)
                .findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)

                val maxHeight = (resources.displayMetrics.heightPixels * 0.8).toInt()
                it.layoutParams.height = maxHeight

                behavior.peekHeight = maxHeight
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = BottomSheetLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = VoucherAdapter(currentPrice)
        initListener()
        viewModel.getAllVoucher()
        setUpAdapter()
        setUpObserver()
        super.onViewCreated(view, savedInstanceState)
    }

    fun setUpAdapter(){
        adapter?.setOnClickItem{ position ->
            val itemSelected = adapter?.getItem(position)
            if (itemSelected ==null) return@setOnClickItem
            if ((itemSelected.minimumOrderValue ?: 0) > currentPrice){
                Toast.makeText(requireContext(), "The voucher is only applicable to orders from ${itemSelected.minimumOrderValue}k.", Toast.LENGTH_SHORT).show()
                return@setOnClickItem
            }
            adapter?.setItemSelected(position)
            updateUi(itemSelected)
        }

        binding.adapter.adapter = adapter
        binding.adapter.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    fun initListener(){
        binding.btnApplyVoucher.singleClick {
            val itemSelected = this@VoucherSelectBottomSheet.adapter?.getItemSelected()
            if (itemSelected==null) return@singleClick
            mainViewModel.setVoucherSelected(itemSelected)
            dismiss()
        }

        binding.btnBack.singleClick {
            mainViewModel.setVoucherSelected(this@VoucherSelectBottomSheet.adapter?.getItemSelected())
            dismiss()
        }
    }

    fun setUpObserver(){
        viewModel.allVoucherLiveData.observe(viewLifecycleOwner){ value ->
            if (value?.success == true){
                if (value?.data == null) return@observe
                adapter?.setList(value.data)
            }
        }
    }

    fun updateUi(voucherResponse: VoucherResponse){
        if (voucherResponse.discountValue == null){
            binding.voucherDiscountDescription.text = getString(
                R.string.text_discount_applied_you_save_percent,
                formatVND(
                    (currentPrice * voucherResponse.discountPercent!! / 100).coerceIn(
                        0,
                        voucherResponse.maxDiscountValue
                    ).toLong()
                )
            )
        }else{
            binding.voucherDiscountDescription.text = getString(
                R.string.text_discount_applied_you_save_value,
                formatVND(voucherResponse.discountValue!!.toLong())
            )

        }
    }
    fun formatVND(amount: Long) = "%,d₫".format(amount).replace(',', '.')

}
