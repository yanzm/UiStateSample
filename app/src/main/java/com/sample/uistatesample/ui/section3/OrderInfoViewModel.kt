package com.sample.uistatesample.ui.section3

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.data.OrderId
import com.sample.uistatesample.data.OrderInfo
import com.sample.uistatesample.di.extractActivity
import com.sample.uistatesample.usecase.CancelOrderUseCase
import com.sample.uistatesample.usecase.GetOrderInfoUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import kotlinx.coroutines.launch

class OrderInfoViewModel @AssistedInject constructor(
    @Assisted private val data: InitialData,
    private val getOrderInfoUseCase: GetOrderInfoUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
) : ViewModel() {

    // TODO: 現状 @Assisted に value class を使うとコンパイルエラーになるのでラッパークラスを用意している。
    data class InitialData(val id: OrderId)

    @AssistedFactory
    interface Factory {
        fun create(
            data: InitialData
        ): OrderInfoViewModel
    }

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ActivityCreatorEntryPoint {
        fun getOrderInfoViewModelFactory(): Factory
    }

    companion object {
        fun create(context: Context, id: OrderId): OrderInfoViewModel {
            return EntryPointAccessors.fromActivity<ActivityCreatorEntryPoint>(context.extractActivity())
                .getOrderInfoViewModelFactory()
                .create(
                    InitialData(id)
                )
        }
    }

    private val orderId: OrderId = data.id

    var uiState by mutableStateOf<UiState>(UiState.Initial)
        private set

    init {
        load()
    }

    fun load() {
        if (uiState is UiState.Loading) {
            return
        }

        uiState = UiState.Loading

        viewModelScope.launch {
            uiState = when (val result = getOrderInfoUseCase(orderId)) {
                is ApiResult.Success -> UiState.Success(
                    result.data,
                    SubmitState.Idle
                )
                is ApiResult.Error -> UiState.Error(result.e)
            }
        }
    }

    fun cancel() {
        val last = uiState
        if (last !is UiState.Success) {
            return
        }

        if (last.submitting) {
            return
        }

        uiState = last.copy(submitState = SubmitState.Submitting)

        viewModelScope.launch {
            uiState = when (val result = cancelOrderUseCase(orderId)) {
                is ApiResult.Success -> last.copy(submitState = SubmitState.Submitted)
                is ApiResult.Error -> last.copy(submitState = SubmitState.Error(result.e))
            }
        }
    }

    fun submitErrorShown() {
        val last = uiState
        if (last !is UiState.Success) {
            return
        }

        uiState = last.copy(submitState = SubmitState.Idle)
    }
}

sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Error(val e: Throwable) : UiState()
    data class Success(
        val orderInfo: OrderInfo,
        val submitState: SubmitState
    ) : UiState() {
        val submitting: Boolean
            get() = submitState != SubmitState.Idle
    }
}

sealed class SubmitState {
    object Idle : SubmitState()
    object Submitting : SubmitState()
    data class Error(val e: Exception) : SubmitState()
    object Submitted : SubmitState()
}
