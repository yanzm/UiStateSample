package com.sample.uistatesample.ui.section3

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sample.uistatesample.data.Dog
import com.sample.uistatesample.data.DogId
import com.sample.uistatesample.data.OrderId
import com.sample.uistatesample.data.OrderInfo
import com.sample.uistatesample.ui.components.ErrorMessage
import com.sample.uistatesample.ui.section2.NextState
import com.sample.uistatesample.ui.theme.MyAppTheme
import java.io.IOException

@Composable
fun OrderInfoScreen(
    viewModel: OrderInfoViewModel,
    onClickBack: () -> Unit,
) {
    OrderInfoContent(
        uiState = viewModel.uiState,
        onClickBack = onClickBack,
        onClickReload = { viewModel.load() },
        onClickCancel = { viewModel.cancel() },
        onSubmitErrorShown = { viewModel.submitErrorShown() },
    )
}

@Composable
fun OrderInfoContent(
    uiState: UiState,
    onClickBack: () -> Unit = {},
    onClickReload: () -> Unit = {},
    onClickCancel: () -> Unit = {},
    onSubmitErrorShown: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Order Info")
                },
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                }
            )
        },
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
                val orderInfo = uiState.orderInfo
                Column {
                    Text(
                        text = "order id : ${orderInfo.id}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(16.dp)
                    )
                    Button(
                        onClick = onClickCancel,
                        enabled = !uiState.submitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (uiState.submitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("キャンセル")
                        }
                    }
                }

                when (uiState.submitState) {
                    SubmitState.Idle -> {}
                    SubmitState.Submitting -> {}
                    is SubmitState.Error -> {
                        AlertDialog(
                            onDismissRequest = onSubmitErrorShown,
                            confirmButton = {
                                TextButton(onClick = onSubmitErrorShown) {
                                    Text("OK")
                                }
                            },
                            text = {
                                Text("キャンセルできませんでした")
                            }
                        )
                    }
                    SubmitState.Submitted -> {
                        DisposableEffect(Unit) {
                            onClickBack()
                            onDispose { }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun OrderInfoContent_Preview_Loading() {
    MyAppTheme {
        OrderInfoContent(
            UiState.Loading
        )
    }
}

@Preview
@Composable
fun OrderInfoContent_Preview_Error() {
    MyAppTheme {
        OrderInfoContent(
            UiState.Error(IOException())
        )
    }
}

@Preview
@Composable
fun OrderInfoContent_Preview_Success_Idle() {
    MyAppTheme {
        OrderInfoContent(
            UiState.Success(
                OrderInfo(OrderId("1")),
                SubmitState.Idle
            )
        )
    }
}

@Preview
@Composable
fun OrderInfoContent_Preview_Success_Submitting() {
    MyAppTheme {
        OrderInfoContent(
            UiState.Success(
                OrderInfo(OrderId("1")),
                SubmitState.Submitting
            )
        )
    }
}
