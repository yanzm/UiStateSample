package com.sample.uistatesample.ui.section2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.uistatesample.data.ApiResult
import com.sample.uistatesample.data.Dog
import com.sample.uistatesample.usecase.GetDogListPagingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PagingDogListViewModel @Inject constructor(
    private val getDogListPagingUseCase: GetDogListPagingUseCase
) : ViewModel() {

    var uiState by mutableStateOf<UiState>(UiState.Initial)
        private set

    var showSnackbar by mutableStateOf(false)
        private set

    fun snackbarShown() {
        showSnackbar = false
    }

    init {
        load()
    }

    fun load() {
        if (uiState is UiState.Loading) {
            return
        }

        uiState = UiState.Loading

        viewModelScope.launch {
            uiState = when (val result = getDogListPagingUseCase(null)) {
                is ApiResult.Success -> UiState.Success.from(
                    list = result.data.list,
                    hasNext = result.data.hasNext
                )
                is ApiResult.Error -> UiState.Error(result.e)
            }
        }
    }

    fun loadNext() {
        val last = uiState
        if (last !is UiState.Success) {
            return
        }

        val lastNextState = last.nextState
        if (lastNextState is NextState.NoNext || lastNextState is NextState.Loading) {
            return
        }

        val lastId = last.list.lastOrNull()?.id ?: return

        uiState = last.copy(nextState = NextState.Loading)

        viewModelScope.launch {
            uiState = when (val result = getDogListPagingUseCase(lastId)) {
                is ApiResult.Success -> UiState.Success.from(
                    list = last.list + result.data.list,
                    hasNext = result.data.hasNext
                )
                is ApiResult.Error -> {
                    showSnackbar = true
                    last.copy(nextState = NextState.Error(result.e))
                }
            }
        }
    }
}

sealed class UiState {
    object Initial : UiState()
    object Loading : UiState()
    data class Error(val e: Throwable) : UiState()
    data class Success(
        val list: List<Dog>,
        val nextState: NextState
    ) : UiState() {
        companion object {
            fun from(list: List<Dog>, hasNext: Boolean): Success {
                return Success(
                    list,
                    when {
                        list.isEmpty() -> NextState.NoNext
                        hasNext -> NextState.HasNext
                        else -> NextState.NoNext
                    }
                )
            }
        }
    }
}

sealed class NextState {
    object NoNext : NextState()
    object HasNext : NextState()
    object Loading : NextState()
    data class Error(val e: Throwable) : NextState()
}
