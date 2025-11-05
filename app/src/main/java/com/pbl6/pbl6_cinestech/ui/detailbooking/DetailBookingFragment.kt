package com.pbl6.pbl6_cinestech.ui.detailbooking

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pbl6.pbl6_cinestech.R
import com.pbl6.pbl6_cinestech.data.model.response.CinemaShowTime
import com.pbl6.pbl6_cinestech.data.model.response.DayOfWeek
import com.pbl6.pbl6_cinestech.data.repository.RepositoryProvider
import com.pbl6.pbl6_cinestech.databinding.FragmentDetailBookingBinding
import hoang.dqm.codebase.base.activity.BaseFragment
import hoang.dqm.codebase.base.activity.navigate
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


class DetailBookingFragment : BaseFragment<FragmentDetailBookingBinding, DetailBookingViewModel>() {
    override val viewModelFactory: ViewModelProvider.Factory
        get() = DetailBookingViewModel.DetailBookingViewModelFactory(
            RepositoryProvider.movieRepository,
            RepositoryProvider.branchRepository,
            RepositoryProvider.showTimeRepository
        )
    private val movieId by lazy {
        arguments?.getString("movieId")
    }

    private val duration by lazy {
        arguments?.getInt("duration")
    }

    private val timeAdapter: TimeAdapter by lazy {
        TimeAdapter()
    }
    private val adapterBranch: BranchAdapter by lazy {
        BranchAdapter()
    }


    private val cinemasShowTimeAdapter: CinemaShowTimeAdapter by lazy {
        CinemaShowTimeAdapter(duration?:0, {idShowTime ->
            val bundle = Bundle().apply {
                putString("idShowTime", idShowTime)
                putString("branchName",
                    this@DetailBookingFragment.adapterBranch.getSelectedBranch().name
                )
            }
            navigate(R.id.seatBookingFragment, bundle)
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        adjustInsetsForBottomNavigation(binding.btnBack)
//        movieId?.let {
//            viewModel.getBranchWithMovieId(it)
//        }
        setUpAdapter()
        setUpObserver()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpAdapter() {
        timeAdapter.setOnClickItem { position ->
            this@DetailBookingFragment.timeAdapter.setSelected(position)
            // query showtime
            viewModel.showTimeResponseLiveData.value?.let { value ->
                val showTime = value.data?.items?.find { it -> isSameDayVN(it.dayOfWeek.value, this@DetailBookingFragment.timeAdapter.getDaySelected().value) }
                Log.d("check showtime", "setUpObserver: ${value.data?.items?.get(0)?.dayOfWeek?.value} ${this@DetailBookingFragment.timeAdapter.getDaySelected().value}")
                showTime?.let {
                    cinemasShowTimeAdapter.setList(mutableListOf(CinemaShowTime(this@DetailBookingFragment.adapterBranch.getSelectedBranch(), showTime)))
                }
            }


        }

        binding.rvShowTimeDay.adapter = timeAdapter
        binding.rvShowTimeDay.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        timeAdapter.setList(generateNext14Days())

        adapterBranch.setOnClickItem { position ->
            movieId?.let {
                viewModel.allBranchResultLiveData.value?.data?.get(position)?.let { branch ->
                    viewModel.getShowTimeWithBranchAndMovie(it, branch.id)
                }
            }
            this@DetailBookingFragment.adapterBranch.setSelected(position)
        }
        binding.rvBranch.adapter = adapterBranch
        binding.rvBranch.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        cinemasShowTimeAdapter.setOnClickItem { position ->

        }
        binding.cinemasShowTime.adapter = cinemasShowTimeAdapter
        binding.cinemasShowTime.layoutManager = LinearLayoutManager(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpObserver() {
        viewModel.branchBookingResultLiveData.observe(viewLifecycleOwner) { value ->
            if (value?.success == true) {

            }

        }

        viewModel.allBranchResultLiveData.observe(viewLifecycleOwner) { value ->
            if (value?.success == true) {
                if (value.data == null) return@observe
                adapterBranch.setList(value.data)
                movieId?.let {
                    Log.d("check showtime", "setUpObserver: $it ${value.data[0].id}")
                    viewModel.getShowTimeWithBranchAndMovie(it, value.data[0].id)
                }
            }
        }

        viewModel.showTimeResponseLiveData.observe(viewLifecycleOwner) { value ->
            if (value?.success == true) {
                if (value.data!=null && !value.data.items.isEmpty()){
                    val showTime = value.data.items.find { it -> isSameDayVN(it.dayOfWeek.value, this@DetailBookingFragment.timeAdapter.getDaySelected().value) }
                    Log.d("check showtime", "setUpObserver: ${value.data.items.get(0).dayOfWeek.value} ${this@DetailBookingFragment.timeAdapter.getDaySelected().value}")
                    showTime?.let {
                        cinemasShowTimeAdapter.setList(mutableListOf(CinemaShowTime(this@DetailBookingFragment.adapterBranch.getSelectedBranch(), showTime)))
                    }
                }
                Log.d("check showTime", "setUpObserver: ${value.data}")
            }
        }
    }

    override fun initListener() {
    }

    override fun initData() {
    }

//    fun generateNext14Days(): List<DayOfWeek> {
//        val list = mutableListOf<DayOfWeek>()
//        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
//        sdf.timeZone = TimeZone.getTimeZone("UTC")
//
//        val calendar = Calendar.getInstance()
//
//        for (i in 0..14) {
//            val date = calendar.time
//
//            val dayName = if (i == 0) {
//                "Today"
//            } else {
//                SimpleDateFormat("EEEE", Locale.ENGLISH).format(date)
//            }
//
//            val isoDate = sdf.format(date)
//
//            list.add(DayOfWeek(dayName, isoDate))
//            calendar.add(Calendar.DAY_OF_MONTH, 1)
//        }
//
//        return list
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateNext14Days(): List<DayOfWeek> {
        val list = mutableListOf<DayOfWeek>()
        val vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh")

        val dayNameFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.ENGLISH)
        val isoFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

        val todayVN = LocalDate.now(vietnamZone)

        for (i in 0..14) {
            val dateVN = todayVN.plusDays(i.toLong())

            val dayName = if (i == 0) "Today" else dateVN.format(dayNameFormatter)

            val dateTimeVN = dateVN.atStartOfDay(vietnamZone)
            val isoDate = dateTimeVN.format(isoFormatter)

            list.add(DayOfWeek(dayName, isoDate))
        }

        return list
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isSameDayVN(dateStr1: String, dateStr2: String): Boolean {
        val vnZone = ZoneId.of("Asia/Ho_Chi_Minh")

        val d1 = OffsetDateTime.parse(dateStr1)
            .atZoneSameInstant(vnZone)
            .toLocalDate()

        val d2 = OffsetDateTime.parse(dateStr2)
            .atZoneSameInstant(vnZone)
            .toLocalDate()

        return d1 == d2
    }
}