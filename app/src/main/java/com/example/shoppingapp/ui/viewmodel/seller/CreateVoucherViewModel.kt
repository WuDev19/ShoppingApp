package com.example.shoppingapp.ui.viewmodel.seller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppingapp.data.remote.dto.request.VoucherRequest
import com.example.shoppingapp.domain.usecase.seller_usecase.CreateShopVoucherUseCase
import com.example.shoppingapp.ui.state.StateListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateVoucherViewModel @Inject constructor(
    private val createShopVoucherUseCase: CreateShopVoucherUseCase
) : ViewModel() {

    //channel có thể dùng để chia tải để xử lý việc nặng do tính chất 1 người nhận 1 data, ko bị lẫn lộn hay cùng nhận 1 data
    private val _createVoucherScope = Channel<StateListener<Unit>>() //mối quan hệ 1-1, nếu có nhiều nơi lắng nghe (collect) thì chỉ có 1 nơi nhận được data
    val createVoucherState = _createVoucherScope.receiveAsFlow() //sự khác nhau giữa receivedAsFlow : collect được lần tiếp theo sau lần trước đó, sau khi collect thì channel vẫn mở, dùng cho sự kiện 1 lần ko lặp lại
                                                                    //consumeAsFlow: collect xong đóng ngay channel gốc => ko collect được lần 2

    fun createVoucher(voucherRequest: VoucherRequest){
        viewModelScope.launch {
            _createVoucherScope.send(StateListener.Loading)
            _createVoucherScope.send(createShopVoucherUseCase(voucherRequest))
        }
    }
}