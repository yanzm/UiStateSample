package com.sample.uistatesample.ui.section5

import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.usecase.AddNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {

    var note by mutableStateOf("")
        private set

    val onNoteChange: (String) -> Unit = {
        note = it
    }

    var submitState by mutableStateOf<SubmitState>(SubmitState.Idle)
        private set

    init {
        savedStateHandle.setSavedStateProvider("AddNoteViewModel") {
            bundleOf("note" to note)
        }

        note = savedStateHandle.get<Bundle>("AddNoteViewModel")
            ?.getString("note") ?: ""
    }

    fun addNote(note: String) {
        if (submitState !is SubmitState.Idle) {
            return
        }

        submitState = SubmitState.Submitting

        viewModelScope.launch {
            submitState = when (val result = addNoteUseCase(note)) {
                is ApiResult.Success -> SubmitState.Submitted
                is ApiResult.Error -> SubmitState.Error(result.e)
            }
        }
    }

    fun submitErrorShown() {
        submitState = SubmitState.Idle
    }
}

sealed class SubmitState {
    object Idle : SubmitState()
    object Submitting : SubmitState()
    data class Error(val e: Exception) : SubmitState()
    object Submitted : SubmitState()
}
