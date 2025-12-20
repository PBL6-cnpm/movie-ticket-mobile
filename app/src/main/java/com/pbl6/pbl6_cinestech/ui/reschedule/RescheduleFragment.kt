package com.pbl6.pbl6_cinestech.ui.reschedule

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pbl6.pbl6_cinestech.databinding.FragmentRescheduleBinding
import com.pbl6.pbl6_cinestech.ui.main.MainViewModel
import hoang.dqm.codebase.base.activity.BaseBottomSheetFragment
import hoang.dqm.codebase.utils.singleClick
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class RescheduleFragment(val duration: Int, val branchName: String, val idShowTimeSelected: String): BaseBottomSheetFragment<FragmentRescheduleBinding>() {
    private val mainViewModel: MainViewModel by activityViewModels()
    private var adapter: RescheduleAdapter? = null
    override fun getVB(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRescheduleBinding {
        _binding = FragmentRescheduleBinding.inflate(inflater, container, false)
        return binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = RescheduleAdapter(duration, branchName)
        super.onViewCreated(view, savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        setUpAdapter()
        mainViewModel.showTimeResponse.observe(viewLifecycleOwner){ value ->
            adapter?.setList(value.times)
            val time = value.times.find { it ->it.id == idShowTimeSelected }
            if (time != null){
                binding.branchName.text = branchName
                binding.timeStart.text = convertTo24Hour(time.time)
                binding.timeEnd.text = buildString {
                    append("~")
                    append(getEndTime(time.time, duration))
                }
                binding.availableSeat.text = "Available seats: ${time.availableSeats}/${time.totalSeats}"
            }
        }
        binding.btnBack.singleClick {
            dismiss()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setUpAdapter(){
        adapter?.setOnClickItem { item, position ->
            val bundle = Bundle().apply {
                putString("idShowTime", item.id)
                val timeStart = item.time
                putString("timeStart", timeStart)
            }
            parentFragmentManager.setFragmentResult("bottom_sheet_result", bundle)
            dismiss()
        }

        binding.rvShowTime.adapter = adapter
        binding.rvShowTime.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val window = dialog?.window
        window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        dialog?.let {
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                val container = it.findViewById<ViewGroup>(android.R.id.content)
                container?.setPadding(0, 0, 0, 0)

                (sheet.parent as? View)?.let { parent ->
                    parent.setBackgroundColor(Color.TRANSPARENT)
                    parent.setPadding(0, 0, 0, 0)

                    val parentParams = parent.layoutParams as? ViewGroup.MarginLayoutParams
                    parentParams?.setMargins(0, 0, 0, 0)
                    parent.layoutParams = parentParams
                }

                val layoutParams = sheet.layoutParams
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = (resources.displayMetrics.heightPixels * 0.85).toInt()
                sheet.layoutParams = layoutParams

                val behavior = BottomSheetBehavior.from(sheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = false
                behavior.isHideable = true
                behavior.skipCollapsed = true
                behavior.peekHeight = layoutParams.height

                sheet.requestLayout()
//                (sheet.parent as? View)?.let { parent ->
//                    parent.setBackgroundColor(Color.TRANSPARENT)
//                    // XÓA HẾT PADDING
//                    parent.setPadding(0, 0, 0, 0)
//                    val parentParams = parent.layoutParams as? ViewGroup.MarginLayoutParams
//                    parentParams?.setMargins(0, 0, 0, 0)
//                    parent.layoutParams = parentParams
//                }
//                sheet.requestLayout()
//                dialog?.window?.setLayout(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
            }
        }
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
}