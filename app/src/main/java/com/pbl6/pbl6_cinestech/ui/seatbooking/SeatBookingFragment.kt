package com.pbl6.pbl6_cinestech.ui.seatbooking

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.request.HoldingRequest
import com.pbl6.pbl6_cinestech.data.model.response.Seat
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentSeatBookingBinding
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import com.pbl6.pbl6_cinestech.ui.reschedule.RescheduleFragment
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.navigate
import hoang.dqm.codebase.base.activity.onBackPressed
import hoang.dqm.codebase.base.activity.popBackStack
import hoang.dqm.codebase.utils.collectLatestFlow
import hoang.dqm.codebase.utils.loadImageSketch
import hoang.dqm.codebase.utils.singleClick
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class SeatBookingFragment : BaseFragment<FragmentSeatBookingBinding, SeatBookingViewModel>() {
    private val mainViewModel by activityViewModels<MainViewModel>()

    override val viewModelFactory: ViewModelProvider.Factory
        get() = SeatBookingViewModel.SeatBookingViewModelFactory(
            RepositoryProvider.seatRepository,
            RepositoryProvider.bookingRepository
        )
    private val idShowTime by lazy {
        arguments?.getString("idShowTime") ?: ""
    }

    private val branchName by lazy {
        arguments?.getString("branchName") ?: ""
    }

    private val dayValue by lazy {
        arguments?.getString("dayValue") ?: ""
    }

    private val timeStart by lazy {
        arguments?.getString("timeStart") ?: ""
    }

    private val duration by lazy {
        arguments?.getInt("duration")?:120
    }

    private var timeText: String = ""


    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        adjustInsetsForBottomNavigation(binding.btnBack)
        updateUI()
        setUpObserver()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpObserver() {
        collectLatestFlow(viewModel.allSeatResult) { value ->
            if (value?.success == true) {
                if (value.data == null) return@collectLatestFlow
                viewModel.setPrice(0)
                val seatType = value.data.typeSeatList
                binding.seatView.setSeatsData(
                    value.data.seatLayout.rows,
                    value.data.seatLayout.cols,
                    value.data.seatLayout.seats,
                    value.data.seatLayout.occupiedSeats.map { id -> id.id }
                )
                binding.seatView.setSeatSelectionListener(object :
                    CinemaSeatView.OnSeatSelectionListener {
                    override fun onSeatSelected(seat: Seat) {
                        viewModel.addSeat(seat.type.price)
                    }

                    override fun onSeatDeselected(seat: Seat) {
                        viewModel.removeSeat(seat.type.price)
                    }
                })
            }
        }

        viewModel.price.observe(viewLifecycleOwner) { value ->
            binding.tvPrice.text = formatVND(value.toLong())
        }

//        viewModel.holdSeatResultLiveData.observe(viewLifecycleOwner) { value ->
//            Log.d("check hold", "setUpObserver: $value")
//            if (value?.success == true){
//                if (value.data == null) return@observe
//                val seatIds = binding.seatView.getSelectedSeats().map { it-> it.id }
//                val seatNumbers = binding.seatView.getSelectedSeats().map { it-> it.name }
//                val bundle = Bundle().apply {
//                    putInt("ticketsPrice", viewModel.price.value!!)
//                    putString("idShowTime", idShowTime)
//                    putStringArrayList("seatIds", ArrayList(seatIds))
//                    putStringArrayList("seatNumbers", ArrayList(seatNumbers))
//                    putString("bookingId", value.data.bookingId)
//                    putString("timeText", timeText)
//                    putString("bookingId", value.data.bookingId)
//                }
//                navigate(R.id.refreshmentsFragment, bundle)
//            } else if (value?.success == false) {
//                Toast.makeText(requireContext(),
//                    getString(R.string.text_something_went_wrong_please_try_again), Toast.LENGTH_SHORT).show()
//            }
//        }

        lifecycleScope.launchWhenStarted {
            viewModel.holdSeatResult.collect { value ->
                Log.d("check hold", "setUpObserver: $value")
                if (value?.success == true) {
                    if (value.data == null) return@collect
                    val seatIds = binding.seatView.getSelectedSeats().map { it -> it.id }
                    val seatNumbers = binding.seatView.getSelectedSeats().map { it -> it.name }
                    val bundle = Bundle().apply {
                        putInt("ticketsPrice", viewModel.price.value!!)
                        putString("idShowTime", idShowTime)
                        putStringArrayList("seatIds", ArrayList(seatIds))
                        putStringArrayList("seatNumbers", ArrayList(seatNumbers))
                        putString("bookingId", value.data.bookingId)
                        putString("timeText", timeText)
                        putString("bookingId", value.data.bookingId)
                    }
                    navigate(R.id.refreshmentsFragment, bundle)
                } else if (value?.success == false) {
                    Toast.makeText(
                        requireContext(),
                        value.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    reloadPage()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initListener() {
        binding.btnChangeShowTime.singleClick {
            val bottomSheet = RescheduleFragment(duration, branchName, idShowTime)
            bottomSheet.show(parentFragmentManager, "BOTTOM_SHEET")
            parentFragmentManager.setFragmentResultListener(
                "bottom_sheet_result",
                viewLifecycleOwner
            ) { key, bundle ->
                bundle?.getString("idShowTime") ?.let {
                    idShowTime ->
                    val timeStart = bundle.getString("timeStart")?:""
                    reloadPage(idShowTime, timeStart)
                }
            }
        }
        binding.btnBack.singleClick {
            popBackStack()
        }
        onBackPressed {
            popBackStack()
        }
        binding.btnNext.singleClick {
            if (viewModel.price.value == 0) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.text_please_make_sure_to_select_at_least_one_seat),
                    Toast.LENGTH_SHORT
                ).show()
                return@singleClick
            }
            val seatIds = binding.seatView.getSelectedSeats().map { it -> it.id }
            viewModel.holdSeat(HoldingRequest(idShowTime, seatIds))
        }
    }

    override fun initData() {
        viewModel.getAllSeatByShowTimeId(idShowTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(timeStart: String = this.timeStart) {
        binding.branchName.text = branchName
        val movie = mainViewModel.getMovieSelected()
        binding.branchName.text = branchName
        binding.imgMovie.loadImageSketch(movie.poster)
        binding.tvNameMovie.text = movie.name
        val time = getDayMonthYearWithWeekday(dayValue)
        val timeMovieStart = convertTo24Hour(timeStart)
        val timeEnd = getEndTime(timeStart, movie.duration)
        binding.tvDescription.text = timeMovieStart + "~" + timeEnd + " | " + time + " | " + "2D"
        timeText = "$timeMovieStart~$timeEnd\n$time"
    }

    fun getDayMonthYearWithWeekday(isoString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val date = inputFormat.parse(isoString)
        val outputFormat = SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.getDefault())
        return outputFormat.format(date!!)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertTo24Hour(time12h: String): String {
        val inputFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
        val outputFormat = DateTimeFormatter.ofPattern("HH:mm")
        val time = LocalTime.parse(time12h.trim().uppercase(), inputFormat)
        return time.format(outputFormat)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEndTime(startTime: String, durationMinutes: Int): String {
        val inputFormat = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
        val outputFormat = DateTimeFormatter.ofPattern("HH:mm")
        val start = LocalTime.parse(startTime.trim().uppercase(), inputFormat)
        val end = start.plusMinutes(durationMinutes.toLong())
        return end.format(outputFormat)
    }

    fun formatVND(amount: Long) = "%,d₫".format(amount).replace(',', '.')

    @RequiresApi(Build.VERSION_CODES.O)
    private fun reloadPage(idShowTime: String= this.idShowTime, timeStart: String = this.timeStart) {
        // Reset UI
        updateUI(timeStart)

        // Reset seat + price
        viewModel.setPrice(0)
        Log.d("check seat", "reloadPage: $idShowTime")
        viewModel.getAllSeatByShowTimeId(idShowTime)

        // Nếu cần reset seatView
        binding.seatView.clearSelection()
    }
}