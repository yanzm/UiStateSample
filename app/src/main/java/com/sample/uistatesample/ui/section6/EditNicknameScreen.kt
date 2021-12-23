package com.sample.uistatesample.ui.section6

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sample.uistatesample.ui.components.ErrorMessage
import com.sample.uistatesample.ui.theme.MyAppTheme
import java.io.IOException

@Composable
fun EditNicknameScreen(
    viewModel: EditNicknameViewModel,
    onClickBack: () -> Unit,
) {
    EditNicknameContent(
        uiState = viewModel.uiState,
        nickname = viewModel.nickname,
        onNicknameChange = viewModel.onNicknameChange,
        isChanged = viewModel.isChanged,
        onClickBack = onClickBack,
        onClickReload = { viewModel.load() },
        onClickSubmit = { viewModel.updateNickname(it) },
        onSubmitErrorShown = { viewModel.submitErrorShown() },
    )
}

@Composable
private fun EditNicknameContent(
    uiState: UiState,
    nickname: String,
    onNicknameChange: (String) -> Unit,
    isChanged: Boolean,
    onClickBack: () -> Unit = {},
    onClickReload: () -> Unit = {},
    onClickSubmit: (String) -> Unit = {},
    onSubmitErrorShown: () -> Unit = {},
) {
    var showConfirmAlert by remember { mutableStateOf(false) }

    val onClickBackWithConfirm = {
        if (isChanged) {
            showConfirmAlert = true
        } else {
            onClickBack()
        }
    }

    BackHandler(onBack = onClickBackWithConfirm)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Edit Nickname")
                },
                navigationIcon = {
                    IconButton(onClick = onClickBackWithConfirm) {
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
                Column {
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = onNicknameChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                    Button(
                        onClick = { onClickSubmit(nickname) },
                        enabled = isChanged && !uiState.submitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (uiState.submitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("変更")
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
                                Text("変更できませんでした")
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

    if (showConfirmAlert) {
        AlertDialog(
            onDismissRequest = { showConfirmAlert = false },
            confirmButton = {
                TextButton(onClick = onClickBack) {
                    Text("破棄する")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmAlert = false }) {
                    Text("キャンセル")
                }
            },
            text = {
                Text("内容が破棄されますがよいですか")
            }
        )
    }
}


@Preview
@Composable
fun EditNicknameContent_Preview_Loading() {
    MyAppTheme {
        EditNicknameContent(
            uiState = UiState.Loading,
            nickname = "",
            onNicknameChange = {},
            isChanged = false,
        )
    }
}

@Preview
@Composable
fun EditNicknameContent_Preview_Error() {
    MyAppTheme {
        EditNicknameContent(
            uiState = UiState.Error(IOException()),
            nickname = "",
            onNicknameChange = {},
            isChanged = false,
        )
    }
}

@Preview
@Composable
fun EditNicknameContent_Preview_Success_Idle() {
    MyAppTheme {
        EditNicknameContent(
            uiState = UiState.Success(
                "Compose",
                SubmitState.Idle
            ),
            nickname = "Compose",
            onNicknameChange = {},
            isChanged = false,
        )
    }
}

@Preview
@Composable
fun EditNicknameContent_Preview_Success_Idle_Changed() {
    MyAppTheme {
        EditNicknameContent(
            uiState = UiState.Success(
                "Compose",
                SubmitState.Idle
            ),
            nickname = "Android",
            onNicknameChange = {},
            isChanged = true,
        )
    }
}


@Preview
@Composable
fun EditNicknameContent_Preview_Success_Submitting() {
    MyAppTheme {
        EditNicknameContent(
            uiState = UiState.Success(
                "Compose",
                SubmitState.Submitting
            ),
            nickname = "Android",
            onNicknameChange = {},
            isChanged = true,
        )
    }
}
