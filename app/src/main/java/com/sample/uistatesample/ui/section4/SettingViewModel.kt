package com.sample.uistatesample.ui.section4

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.data.SettingId
import com.sample.uistatesample.usecase.GetSettingInfoUseCase
import com.sample.uistatesample.usecase.UpdateSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getSettingInfoUseCase: GetSettingInfoUseCase,
    private val updateSettingUseCase: UpdateSettingUseCase,
) : ViewModel() {

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
            uiState = when (val result = getSettingInfoUseCase()) {
                is ApiResult.Success -> UiState.Success(
                    result.data.map {
                        SwitchState(
                            id = it.id,
                            name = it.name,
                            checked = it.checked,
                            submitting = false
                        )
                    }
                )
                is ApiResult.Error -> UiState.Error(result.e)
            }
        }
    }

    fun updateSetting(id: SettingId, newValue: Boolean) {
        val last = uiState
        if (last !is UiState.Success) {
            return
        }

        val list = last.settingInfo
        val index = list.indexOfFirst { it.id == id }
        val setting = list.getOrNull(index) ?: return

        if (setting.checked == newValue || setting.submitting) {
            return
        }

        uiState = last.copy(
            settingInfo = list.replace(
                index,
                setting.copy(checked = newValue, submitting = true)
            )
        )

        updateSettingInternal(id, setting.checked, newValue)
    }

    private fun updateSettingInternal(id: SettingId, lastValue: Boolean, newValue: Boolean) {
        viewModelScope.launch {
            val result = updateSettingUseCase(id, newValue)

            val last = uiState
            if (last !is UiState.Success) {
                return@launch
            }


            val list = last.settingInfo
            val index = list.indexOfFirst { it.id == id }
            val setting = list.getOrNull(index) ?: return@launch

            uiState = last.copy(
                settingInfo = list.replace(
                    index,
                    setting.copy(
                        checked = when (result) {
                            is ApiResult.Success -> newValue
                            is ApiResult.Error -> lastValue // 失敗したので戻す
                        },
                        submitting = false
                    )
                )
            )
        }
    }
}

private fun <T> List<T>.replace(index: Int, item: T): List<T> {
    return toMutableList().apply {
        removeAt(index)
        add(index, item)
    }
}

sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Error(val e: Throwable) : UiState()
    data class Success(
        val settingInfo: List<SwitchState>
    ) : UiState()
}

data class SwitchState(
    val id: SettingId,
    val name: String,
    val checked: Boolean,
    val submitting: Boolean
)
