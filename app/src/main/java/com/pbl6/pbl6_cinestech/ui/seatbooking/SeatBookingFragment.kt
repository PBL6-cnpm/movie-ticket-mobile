package com.pbl6.pbl6_cinestech.ui.seatbooking

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.response.Seat
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentSeatBookingBinding
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.utils.loadImageSketch
import hoang.dqm.codebase.utils.singleClick
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class SeatBookingFragment : BaseFragment<FragmentSeatBookingBinding, SeatBookingViewModel>() {
    private val mainViewModel by activityViewModels <MainViewModel>()

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

    private val dayValue by lazy {
        arguments?.getString("dayValue")?: ""
    }

    private val timeStart by lazy {
        arguments?.getString("timeStart")?: ""
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        adjustInsetsForBottomNavigation(binding.btnBack)
        viewModel.getAllSeatByShowTimeId(idShowTime)
        updateUI()
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
                binding.seatView.setSeatSelectionListener(object: CinemaSeatView.OnSeatSelectionListener{
                    override fun onSeatSelected(seat: Seat) {
                        viewModel.addSeat(seat.type.price)
                    }

                    override fun onSeatDeselected(seat: Seat) {
                        viewModel.removeSeat(seat.type.price)
                    }
                })
            }
        }

        viewModel.price.observe(viewLifecycleOwner){ value ->
            binding.tvPrice.text = formatVND(value.toLong())
        }
    }

    override fun initListener() {
        binding.btnNext.singleClick { 
            if (viewModel.price.value == 0){
                Toast.makeText(requireContext(),
                    getString(R.string.text_please_make_sure_to_select_at_least_one_seat), Toast.LENGTH_SHORT).show()
                return@singleClick
            }
        }
    }

    override fun initData() {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUI(){
        binding.branchName.text = branchName
        val movie = mainViewModel.getMovieSelected()
        binding.branchName.text = branchName
        binding.imgMovie.loadImageSketch(movie.poster)
        binding.tvNameMovie.text = movie.name
        val time = getDayMonthYearWithWeekday(dayValue)
        val timeMovieStart = convertTo24Hour(timeStart)
        val timeEnd = getEndTime(timeStart, movie.duration)
        binding.tvDescription.text = timeMovieStart+"~"+timeEnd+" | "+time+" | "+"2D"

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
        val inputFormat = DateTimeFormatter.ofPattern("h:mm a",  Locale.US)
        val outputFormat = DateTimeFormatter.ofPattern("HH:mm")
        val start = LocalTime.parse(startTime.trim().uppercase(), inputFormat)
        val end = start.plusMinutes(durationMinutes.toLong())
        return end.format(outputFormat)
    }

    fun formatVND(amount: Long) = "%,d₫".format(amount).replace(',', '.')
}