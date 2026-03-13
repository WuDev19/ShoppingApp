package com.example.shoppingapp.ui.view.fragment.seller

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.shoppingapp.data.remote.dto.request.VoucherRequest
import com.example.shoppingapp.databinding.FragmentCreateVoucherBinding
import com.example.shoppingapp.ui.state.StateListener
import com.example.shoppingapp.ui.viewmodel.seller.CreateVoucherViewModel
import com.example.shoppingapp.utils.StringConstants
import com.example.shoppingapp.utils.ToastUtils
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@AndroidEntryPoint
class CreateVoucherFragment : Fragment() {

    private val viewModel: CreateVoucherViewModel by viewModels()
    private var _binding: FragmentCreateVoucherBinding? = null
    private val binding: FragmentCreateVoucherBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateVoucherBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createVoucherState.collectLatest {
                    when (it) {
                        is StateListener.Idle -> Unit
                        is StateListener.Loading -> {
                            ToastUtils.showToast(requireContext(), "Loading")
                        }

                        is StateListener.Error -> {
                            Log.e(StringConstants.DEBUG, "onViewCreated: ${it.e?.message}")
                        }

                        is StateListener.Success -> {
                            ToastUtils.showToast(requireContext(), "Create successfully")
                            binding.apply {
                                edtVoucherCode.setText("")
                                edtVoucherValue.setText("")
                                edtVoucherDescription.setText("")
                                edtMaximumDiscount.setText("")
                                edtVoucherMinimumPrice.setText("")
                                edtVoucherMaximumAmount.setText("")
                                pickerStartDate.setText(StringConstants.SELECT_START_DATE)
                                pickerEndDate.setText(StringConstants.SELECT_END_DATE)
                            }
                        }
                    }
                }
            }
        }

        val startDatePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText(StringConstants.SELECT_START_DATE)
                .build() //hiện chọn ngày
        val startTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("Select start time")
            .build()
        val endDatePicker =
            MaterialDatePicker.Builder.datePicker().setTitleText(StringConstants.SELECT_END_DATE)
                .build() //hiện chọn ngày
        val endTimePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText("Select end time")
            .build()
        var startTime: Long = 0
        var endTime: Long = 0
        var voucherCode: String = ""
        binding.apply {
            pickerStartDate.setOnClickListener {
                startDatePicker.show(childFragmentManager, "start_date_picker")
            }
            startDatePicker.addOnPositiveButtonClickListener { selection ->
                startTime = selection
                startTimePicker.show(childFragmentManager, "start_time_picker")
            }
            startTimePicker.addOnPositiveButtonClickListener {
                startTime = takeTime(startTime, startTimePicker)
                pickerStartDate.setText(convertEasilyFormat(startTime))
            }
            pickerEndDate.setOnClickListener {
                endDatePicker.show(childFragmentManager, "end_date_picker")
            }
            endDatePicker.addOnPositiveButtonClickListener { selection ->
                endTime = selection
                endTimePicker.show(childFragmentManager, "end_time_picker")
            }
            endTimePicker.addOnPositiveButtonClickListener {
                endTime = takeTime(endTime, endTimePicker)
                pickerEndDate.setText(convertEasilyFormat(endTime))
            }

            btnGen.setOnClickListener {
                voucherCode = UUID.randomUUID().toString()
                edtVoucherCode.setText(voucherCode)
            }

            imgBack.setOnClickListener {
                findNavController().popBackStack()
            }

            btnCreateVoucher.setOnClickListener {
                val voucherCode = edtVoucherCode.text.toString().trim()
                val minimumPrice = edtVoucherMinimumPrice.text.toString().trim()
                val maximumDiscount = edtMaximumDiscount.text.toString().trim()
                val maximumAmount = edtVoucherMaximumAmount.text.toString()
                val voucherValue = edtVoucherValue.text.toString().trim()
                if (voucherCode.isEmpty() ||
                    startTime == 0L ||
                    endTime == 0L ||
                    voucherCode.isEmpty() ||
                    minimumPrice.isEmpty() ||
                    maximumDiscount.isEmpty() ||
                    maximumAmount.isEmpty() ||
                    voucherValue.isEmpty() ||
                    (rbFixed.isChecked && voucherValue != maximumDiscount)
                ) {
                    ToastUtils.showToast(
                        requireContext(),
                        "Lack of information, please enter fully"
                    )
                    return@setOnClickListener
                }
                val voucherRequest = VoucherRequest(
                    voucherCode,
                    if (edtVoucherDescription.text.toString()
                            .isEmpty()
                    ) null else edtVoucherDescription.text.toString().trim(),
                    if (rbFixed.isChecked) rbFixed.text.toString() else rbPercent.text.toString(),
                    minimumPrice.toInt(),
                    maximumAmount.toInt(),
                    voucherValue.toInt(),
                    maximumDiscount.toInt(),
                    startTime,
                    endTime
                )
                viewModel.createVoucher(voucherRequest)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun takeTime(time: Long, timePicker: MaterialTimePicker): Long {
        val hour = timePicker.hour
        val minutes = timePicker.minute
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minutes)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertEasilyFormat(time: Long): String {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.getDefault())
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
        return dateTime.format(formatter)
    }

}