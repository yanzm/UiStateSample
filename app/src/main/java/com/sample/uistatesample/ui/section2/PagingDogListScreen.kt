package com.sample.uistatesample.ui.section2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sample.uistatesample.data.Dog
import com.sample.uistatesample.data.DogId
import com.sample.uistatesample.ui.components.ErrorMessage
import com.sample.uistatesample.ui.theme.MyAppTheme
import java.io.IOException

@Composable
fun PagingDogListScreen(
    viewModel: PagingDogListViewModel,
    onClickDog: (Dog) -> Unit,
) {
    PagingDogListContent(
        uiState = viewModel.uiState,
        onClickReload = { viewModel.load() },
        onClickDog = onClickDog,
        onLoadNext = { viewModel.loadNext() },
        showSnackbar = viewModel.showSnackbar,
        onSnackbarShown = { viewModel.snackbarShown() },
    )
}

@Composable
fun PagingDogListContent(
    uiState: UiState,
    onClickReload: () -> Unit = {},
    onClickDog: (Dog) -> Unit = {},
    onLoadNext: () -> Unit = {},
    showSnackbar: Boolean = false,
    onSnackbarShown: () -> Unit = {},
) {
    val scaffoldState = rememberScaffoldState()

    if (showSnackbar) {
        LaunchedEffect(scaffoldState) {
            scaffoldState.snackbarHostState.showSnackbar("読み込めませんでした")
            onSnackbarShown()
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text("Dog List with Paging")
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
                        item {
                            val nextState = uiState.nextState
                            when (nextState) {
                                NextState.NoNext -> {
                                }
                                NextState.HasNext,
                                NextState.Loading -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillParentMaxWidth()
                                            .padding(16.dp)
                                            .wrapContentWidth()
                                    )
                                }
                                is NextState.Error -> {
                                    Text(
                                        text = "読み込めませんでした\nタップして再読み込み",
                                        modifier = Modifier
                                            .fillParentMaxWidth()
                                            .clickable(onClick = onLoadNext)
                                            .padding(16.dp)
                                            .wrapContentWidth()
                                    )
                                }
                            }

                            if (nextState is NextState.HasNext) {
                                DisposableEffect(nextState) {
                                    onLoadNext()
                                    onDispose { }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun PagingDogListContent_Preview_Loading() {
    MyAppTheme {
        PagingDogListContent(
            UiState.Loading
        )
    }
}

@Preview
@Composable
fun PagingDogListContent_Preview_Error() {
    MyAppTheme {
        PagingDogListContent(
            UiState.Error(IOException())
        )
    }
}

@Preview
@Composable
fun PagingDogListContent_Preview_Success_Empty() {
    MyAppTheme {
        PagingDogListContent(
            UiState.Success.from(emptyList(), false)
        )
    }
}

@Preview
@Composable
fun PagingDogListContent_Preview_Success_NoNext() {
    MyAppTheme {
        PagingDogListContent(
            UiState.Success(
                list = listOf(
                    Dog(DogId("0")),
                    Dog(DogId("1")),
                    Dog(DogId("2")),
                ),
                nextState = NextState.NoNext,
            )
        )
    }
}

@Preview
@Composable
fun PagingDogListContent_Preview_Success_Loading() {
    MyAppTheme {
        PagingDogListContent(
            UiState.Success(
                list = listOf(
                    Dog(DogId("0")),
                    Dog(DogId("1")),
                    Dog(DogId("2")),
                ),
                nextState = NextState.Loading,
            )
        )
    }
}

@Preview
@Composable
fun PagingDogListContent_Preview_Success_Error() {
    MyAppTheme {
        PagingDogListContent(
            UiState.Success(
                list = listOf(
                    Dog(DogId("0")),
                    Dog(DogId("1")),
                    Dog(DogId("2")),
                ),
                nextState = NextState.Error(IOException()),
            )
        )
    }
}
