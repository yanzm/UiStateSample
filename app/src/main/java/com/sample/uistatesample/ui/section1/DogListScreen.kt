package com.sample.uistatesample.ui.section1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sample.uistatesample.data.Dog
import com.sample.uistatesample.data.DogId
import com.sample.uistatesample.ui.components.ErrorMessage
import com.sample.uistatesample.ui.theme.MyAppTheme
import java.io.IOException

/**
 * ViewModel に依存する stateful な Composable
 */
@Composable
fun DogListScreen(
    viewModel: DogListViewModel,
    onClickDog: (Dog) -> Unit,
) {
    DogListContent(
        uiState = viewModel.uiState,
        onClickReload = { viewModel.load() },
        onClickDog = onClickDog
    )
}

/**
 * stateless な Composable
 */
@Composable
fun DogListContent(
    uiState: UiState,
    onClickReload: () -> Unit = {},
    onClickDog: (Dog) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Dog List")
                }
            )
        }
    ) {
        when (uiState) {
            UiState.Initial,
            UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
            is UiState.Error -> {
                ErrorMessage(
                    message = "読み込めませんでした",
                    onClickReload = onClickReload,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val list = uiState.list
                    if (list.isEmpty()) {
                        item {
                            Text(
                                text = "データがありません",
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .wrapContentSize()
                            )
                        }
                    } else {
                        items(uiState.list) {
                            Text(
                                text = it.id.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onClickDog(it)
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DogListContent_Preview_Loading() {
    MyAppTheme {
        DogListContent(
            UiState.Loading
        )
    }
}

@Preview
@Composable
fun DogListContent_Preview_Error() {
    MyAppTheme {
        DogListContent(
            UiState.Error(IOException())
        )
    }
}

@Preview
@Composable
fun DogListContent_Preview_Success() {
    MyAppTheme {
        DogListContent(
            UiState.Success(
                listOf(
                    Dog(DogId("0")),
                    Dog(DogId("1")),
                    Dog(DogId("2")),
                )
            )
        )
    }
}

@Preview
@Composable
fun DogListContent_Preview_Success_Empty() {
    MyAppTheme {
        DogListContent(
            UiState.Success(
                emptyList()
            )
        )
    }
}
