package com.sample.uistatesample.ui.section6

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
class EditNicknameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNicknameUseCase: GetNicknameUseCase,
    private val updateNicknameUseCase: UpdateNicknameUseCase,
) : ViewModel() {

    var uiState by mutableStateOf<UiState>(UiState.Initial)
        private set

    var nickname by mutableStateOf("")
        private set

    val onNicknameChange: (String) -> Unit = {
        nickname = it
    }

    private val editingNickname: String?

    init {
        savedStateHandle.setSavedStateProvider("EditNicknameViewModel") {
            if (uiState is UiState.Success) {
                bundleOf("nickname" to nickname)
            } else {
                bundleOf()
            }
        }

        editingNickname = savedStateHandle.get<Bundle>("EditNicknameViewModel")
            ?.getString("nickname")

        load()
    }

    val isChanged: Boolean
        get() = when (val last = uiState) {
            UiState.Initial,
            UiState.Loading,
            is UiState.Error -> false
            is UiState.Success -> nickname != last.savedNickname
        }

    fun load() {
        if (uiState is UiState.Loading) {
            return
        }

        uiState = UiState.Loading

        viewModelScope.launch {
            uiState = when (val result = getNicknameUseCase()) {
                is ApiResult.Success -> {
                    val savedNickname = result.data
                    nickname = editingNickname ?: savedNickname
                    UiState.Success(savedNickname, SubmitState.Idle)
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

        uiState = last.copy(submitState = SubmitState.Submitting)

        viewModelScope.launch {
            uiState = when (val result = updateNicknameUseCase(nickname)) {
                is ApiResult.Success -> UiState.Success(
                    savedNickname = nickname,
                    submitState = SubmitState.Submitted
                )
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
        val savedNickname: String,
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
