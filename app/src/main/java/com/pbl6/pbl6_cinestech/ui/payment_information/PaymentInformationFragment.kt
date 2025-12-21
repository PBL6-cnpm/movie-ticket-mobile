package com.pbl6.pbl6_cinestech.ui.payment_information

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.request.ApplyVoucherRequest
import com.pbl6.pbl6_cinestech.data.model.request.BookingRequest
import com.pbl6.pbl6_cinestech.data.model.request.PaymentRequest
import com.pbl6.pbl6_cinestech.data.model.request.RefreshmentsOrder
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentPaymentInformationBinding
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import com.pbl6.pbl6_cinestech.ui.voucher_page.VoucherSelectBottomSheet
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.navigate
import hoang.dqm.codebase.base.activity.onBackPressed
import hoang.dqm.codebase.base.activity.popBackStack
import hoang.dqm.codebase.utils.loadImageSketch
import hoang.dqm.codebase.utils.singleClick

class PaymentInformationFragment :
    BaseFragment<FragmentPaymentInformationBinding, PaymentInformationViewModel>() {
    override val viewModelFactory: ViewModelProvider.Factory
        get() = PaymentInformationViewModel.PaymentInformationViewModelFactory(
            RepositoryProvider.bookingRepository,
            RepositoryProvider.paymentRepository
        )

    private lateinit var paymentSheet: PaymentSheet
    private var clientSecret = ""

    private val bookingId by lazy {
        arguments?.getString("bookingId") ?: ""
    }
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val seatIds by lazy {
        arguments?.getStringArrayList("seatIds") ?: arrayListOf()
    }
    private val seatNumbers by lazy {
        arguments?.getStringArrayList("seatNumbers") ?: arrayListOf()
    }
    private val ticketsPrice by lazy {
        arguments?.getInt("ticketsPrice") ?: 0
    }
    private val refreshmentPrice by lazy {
        arguments?.getInt("refreshmentPrice") ?: 0
    }
    private val timeText by lazy {
        arguments?.getString("timeText") ?: ""
    }

    private val adapter by lazy {
        RefreshmentsOrderedAdapter()
    }

    override fun initView() {
        updateUI()
        setUpAdapter()
        setUpObserver()
        preparePayment()
    }

    override fun initListener() {
        binding.btnBack.singleClick {
            popBackStack(R.id.refreshmentsFragment)
        }
        onBackPressed {
            popBackStack(R.id.refreshmentsFragment)
        }
        binding.btnNext.singleClick {
            binding.btnNext.isEnabled = false
            viewModel.bookingSeat(
                BookingRequest(
                    bookingId,
                    mainViewModel.getListRefreshments()
                        .map { RefreshmentsOrder(it.refreshmentId, it.quantity) },
                    viewModel.applyVoucherResponseLiveData.value?.data?.code
                )
            )
            binding.lottieLoading.visibility = View.VISIBLE
            binding.lottieLoading.playAnimation()
        }

        binding.selectVoucher.singleClick {
            val bottomSheet = VoucherSelectBottomSheet(ticketsPrice + refreshmentPrice)
            bottomSheet.show(parentFragmentManager, "MyBottomSheet")
        }
    }

    override fun initData() {
    }

    private fun updateUI() {
        adjustInsetsForBottomNavigation(binding.btnBack)
        val movie = mainViewModel.getMovieSelected()
        val branch = mainViewModel.getBranchSelected()
        val account = mainViewModel.getAccount()
        binding.imgMovie.loadImageSketch(movie.poster)
        binding.nameMovie.text = movie.name
        branch.imgPath?.let {
            binding.imgBranch.loadImageSketch(it)
        }
        binding.tvBranchName.text = branch.name
        binding.ageMax.text = "${movie.ageLimit}+"
        binding.tvAgeDescription.text = getString(
            R.string.text_this_movie_is_rated_pg_for_intense_action_sequences_and_some_language,
            movie.ageLimit
        )
        binding.seat.text = seatNumbers.joinToString(", ")
        binding.ticketPrice.text = formatVND(ticketsPrice.toLong())
        binding.refreshmentsPrice.text = formatVND(refreshmentPrice.toLong())
        val price =
            if (viewModel.applyVoucherResponseLiveData.value == null) (ticketsPrice) else viewModel.applyVoucherResponseLiveData.value?.data?.finalPrice
        binding.total.text =
            formatVND((price?.toLong() ?: (ticketsPrice).toLong()) + refreshmentPrice)
        Log.d("check price", "updateUI: $price $ticketsPrice $refreshmentPrice")
        binding.showTime.text = timeText
        binding.name.text = account.fullName
        binding.mail.text = account.phoneNumber + "-" + account.email
        binding.btnEdit.singleClick {
//            navigate(R.id.editProfileFragment)
        }
    }

    private fun setUpObserver() {
        lifecycleScope.launchWhenStarted {
            viewModel.paymentResult.collect { value ->
                if (value?.success == true) {
                    if (value.data == null) return@collect
                    binding.lottieLoading.visibility = View.GONE
                    clientSecret = value.data.clientSecret
                    if (clientSecret.isBlank()) {
                        Toast.makeText(
                            requireContext(),
                            "Missing clientSecret!", Toast.LENGTH_SHORT
                        ).show()
                        return@collect
                    }
                    val config = PaymentSheet.Configuration(
                        merchantDisplayName = "CineStech"
                    )
                    paymentSheet.presentWithPaymentIntent(clientSecret, config)
                    binding.btnNext.isEnabled = true
                } else {
                    binding.btnNext.isEnabled = true
                    binding.lottieLoading.visibility = View.GONE
                }
            }
        }
//        viewModel.paymentResultLiveData.observe(viewLifecycleOwner) { value ->
//            if (value?.success == true) {
//                if (value.data == null) return@observe
//                binding.lottieLoading.visibility = View.GONE
//                clientSecret = value.data.clientSecret
//                if (clientSecret.isBlank()) {
//                    Toast.makeText(requireContext(),
//                        "Missing clientSecret!", Toast.LENGTH_SHORT).show()
//                    return@observe
//                }
//                val config = PaymentSheet.Configuration(
//                    merchantDisplayName = "CineStech"
//                )
//                paymentSheet.presentWithPaymentIntent(clientSecret, config)
//                binding.btnNext.isEnabled = true
//            } else {
//                binding.btnNext.isEnabled = true
//                binding.lottieLoading.visibility = View.GONE
//            }
//        }

//        viewModel.bookingResultLiveData.observe(viewLifecycleOwner) { value ->
//            Log.d("BOOKING_JSON", "call")
//            if (value?.success == true) {
//                if (value.data == null) return@observe
//                viewModel.createPayment(PaymentRequest(value.data.bookingId))
//                  }
//            } else if (value?.success == false) {
//                binding.btnNext.isEnabled = true
//                binding.lottieLoading.visibility = View.GONE
//                showDialogBookAgain()
//            }
//        }

        lifecycleScope.launchWhenStarted {
            viewModel.bookingResult.collect { value ->
                if (value?.success == true) {
                    if (value.data == null) return@collect
                    viewModel.createPayment(PaymentRequest(value.data.bookingId))
                } else if (value?.success == false) {
                    binding.btnNext.isEnabled = true
                    binding.lottieLoading.visibility = View.GONE
                    showDialogBookAgain()
                }
            }
        }

        mainViewModel.voucherSelected.observe(viewLifecycleOwner) { value ->
            if (value != null) {
                binding.voucherTextIsSelect.text = "${value.code} >"
                viewModel.applyVoucher(ApplyVoucherRequest(bookingId, value.code))
            } else {
                binding.voucherTextIsSelect.text = "Select >"
            }
        }

        viewModel.applyVoucherResponseLiveData.observe(viewLifecycleOwner) { value ->
            if (value?.success == true) {
                if (value.data == null) return@observe
                binding.voucherTextIsSelect.text = "${value.data.code} >"
                binding.voucher.text = formatVND(value.data.voucherAmount.toLong())
                binding.total.text = formatVND(value.data.finalPrice.toLong())

            }

        }
    }

    private fun setUpAdapter() {
        binding.rvOrdered.adapter = adapter
        binding.rvOrdered.layoutManager = LinearLayoutManager(requireContext())
        adapter.setList(mainViewModel.getListRefreshments())
    }

    fun formatVND(amount: Long) = "%,d₫".format(amount).replace(',', '.')

    private fun preparePayment() {
        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
    }

    // 4. Xử lý kết quả thanh toán
    private fun onPaymentSheetResult(result: PaymentSheetResult) {
        when (result) {
            is PaymentSheetResult.Completed -> {
                Toast.makeText(
                    requireContext(),
                    "Payment success!", Toast.LENGTH_SHORT
                ).show()
                Log.d("payment", "Success")
                navigate(R.id.bookingHistoryFragment, isPop = true)
            }

            is PaymentSheetResult.Canceled -> {
                Toast.makeText(
                    requireContext(),
                    "Payment canceled", Toast.LENGTH_SHORT
                ).show()
                Log.d("payment", "Canceled")
            }

            is PaymentSheetResult.Failed -> {
                Toast.makeText(
                    requireContext(),
                    "Payment failed: ${result.error.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("payment", "Error", result.error)
            }
        }
    }

    fun showDialogBookAgain() {
        if (!isAdded || isDetached || view == null) return
        context?.let { ctx ->
            val dialogView = layoutInflater.inflate(R.layout.dialog_book_again, null)
            val dialog =
                AlertDialog.Builder(ctx).setView(dialogView).setCancelable(false).create()
            dialogView.findViewById<ImageView>(R.id.btn_home).singleClick {
                popBackStack(R.id.homeFragment)
                dialog.dismiss()

            }
            dialogView.findViewById<ImageView>(R.id.btn_book_again).singleClick {
                popBackStack(R.id.detailBookingFragment)
                dialog.dismiss()
            }
            dialog.window?.setBackgroundDrawableResource(R.color.black_88000000)
            dialog.show()
        }
    }

}