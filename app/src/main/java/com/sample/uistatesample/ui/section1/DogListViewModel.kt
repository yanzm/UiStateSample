package com.sample.uistatesample.ui.section1

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.data.Dog
import com.sample.uistatesample.usecase.GetDogListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogListViewModel @Inject constructor(
    private val getDogListUseCase: GetDogListUseCase,
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
            uiState = when (val result = getDogListUseCase()) {
                is ApiResult.Success -> UiState.Success(result.data)
                is ApiResult.Error -> UiState.Error(result.e)
            }
        }
    }
}

sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Error(val e: Throwable) : UiState()
    data class Success(val list: List<Dog>) : UiState()
}
