package com.sample.uistatesample.ui.section5

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sample.uistatesample.ui.theme.MyAppTheme

@Composable
fun AddNoteScreen(
    viewModel: AddNoteViewModel,
    onClickBack: () -> Unit,
) {
    AddNoteContent(
        note = viewModel.note,
        onNoteChange = viewModel.onNoteChange,
        submitState = viewModel.submitState,
        onClickBack = onClickBack,
        onClickSubmit = { viewModel.addNote(it) },
        onSubmitErrorShown = { viewModel.submitErrorShown() },
    )
}

@Composable
private fun AddNoteContent(
    note: String,
    onNoteChange: (String) -> Unit,
    submitState: SubmitState,
    onClickBack: () -> Unit = {},
    onClickSubmit: (String) -> Unit = {},
    onSubmitErrorShown: () -> Unit = {},
) {
    val submitting = submitState !is SubmitState.Idle

    var showConfirmAlert by remember { mutableStateOf(false) }

    val onClickBackWithConfirm = {
        if (note.isNotEmpty()) {
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
                    Text("Add Note")
                },
                navigationIcon = {
                    IconButton(onClick = onClickBackWithConfirm) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                }
            )
        }
    ) {
        Column {
            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            Button(
                onClick = { onClickSubmit(note) },
                enabled = note.isNotEmpty() && !submitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (submitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text("追加")
                }
            }
        }
    }

    when (submitState) {
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
                    Text("追加できませんでした")
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
fun AddNoteContent_Preview_Idle_Empty() {
    MyAppTheme {
        AddNoteContent(
            note = "",
            onNoteChange = {},
            submitState = SubmitState.Idle
        )
    }
}

@Preview
@Composable
fun AddNoteContent_Preview_Idle() {
    MyAppTheme {
        AddNoteContent(
            note = "hello",
            onNoteChange = {},
            submitState = SubmitState.Idle
        )
    }
}


@Preview
@Composable
fun AddNoteContent_Preview_Submitting() {
    MyAppTheme {
        AddNoteContent(
            note = "hello",
            onNoteChange = {},
            submitState = SubmitState.Submitting
        )
    }
}
