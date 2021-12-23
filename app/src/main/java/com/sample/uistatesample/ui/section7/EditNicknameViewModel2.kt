package com.sample.uistatesample.ui.section7

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.usecase.GetNicknameUseCase
import com.sample.uistatesample.usecase.UpdateNicknameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNicknameViewModel2 @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNicknameUseCase: GetNicknameUseCase,
    private val updateNicknameUseCase: UpdateNicknameUseCase,
) : ViewModel() {

    var uiState by mutableStateOf<UiState>(UiState.Initial)
        private set

    private val editingNickname: String?

    init {
        savedStateHandle.setSavedStateProvider("EditNicknameViewModel2") {
            val last = uiState
            if (last is UiState.Success) {
                bundleOf("nickname" to last.nickname)
            } else {
                bundleOf()
            }
        }

        editingNickname = savedStateHandle.get<Bundle>("EditNicknameViewModel2")
            ?.getString("nickname")

        load()
    }

    fun load() {
        if (uiState is UiState.Loading) {
            return
        }

        uiState = UiState.Loading

        viewModelScope.launch {
            uiState = when (val result = getNicknameUseCase()) {
                is ApiResult.Success -> UiState.Success(result.data).apply {
                    if (editingNickname != null) {
                        onNicknameChange(editingNickname)
                    }
                }
                is ApiResult.Error -> UiState.Error(result.e)
            }
        }
    }

    fun updateNickname(nickname: String) {
        val last = uiState
        if (last !is UiState.Success) {
            return
        }

        if (last.submitting) {
            return
        }

        last.submitState = SubmitState.Submitting

        viewModelScope.launch {
            when (val result = updateNicknameUseCase(nickname)) {
                is ApiResult.Success -> {
                    uiState = UiState.Success(nickname).apply {
                        submitState = SubmitState.Submitted
                    }
                }
                is ApiResult.Error -> {
                    last.submitState = SubmitState.Error(result.e)
                }
            }
        }
    }

    fun submitErrorShown() {
        val last = uiState
        if (last !is UiState.Success) {
            return
        }

        last.submitState = SubmitState.Idle
    }
}

sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Error(val e: Throwable) : UiState()
    data class Success(
        val savedNickname: String,
    ) : UiState() {

        var nickname by mutableStateOf(savedNickname)
            private set

        val onNicknameChange: (String) -> Unit = {
            nickname = it
        }

        var submitState by mutableStateOf<SubmitState>(SubmitState.Idle)

        val submitting: Boolean
            get() = submitState != SubmitState.Idle

        val isChanged: Boolean
            get() = nickname != savedNickname
    }
}

sealed class SubmitState {
    object Idle : SubmitState()
    object Submitting : SubmitState()
    data class Error(val e: Exception) : SubmitState()
    object Submitted : SubmitState()
}
