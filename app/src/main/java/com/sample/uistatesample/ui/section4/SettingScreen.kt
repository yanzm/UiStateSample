package com.sample.uistatesample.ui.section4

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sample.uistatesample.data.SettingId
import com.sample.uistatesample.ui.components.ErrorMessage
import com.sample.uistatesample.ui.theme.MyAppTheme
import java.io.IOException

@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    onClickBack: () -> Unit,
) {
    SettingContent(
        uiState = viewModel.uiState,
        onClickBack = onClickBack,
        onClickReload = { viewModel.load() },
        onCheckedChange = { id, newValue ->
            viewModel.updateSetting(id, newValue)
        }
    )
}

@Composable
private fun SettingContent(
    uiState: UiState,
    onClickBack: () -> Unit = {},
    onClickReload: () -> Unit = {},
    onCheckedChange: (SettingId, Boolean) -> Unit = { _, _ -> }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Setting")
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.settingInfo, key = { it.id.value }) {
                        val checked = it.checked
                        val submitting = it.submitting
                        Row(
                            modifier = Modifier
                                .clickable(
                                    enabled = !submitting,
                                    onClick = {
                                        onCheckedChange(it.id, !checked)
                                    }
                                )
                                .padding(16.dp)
                        ) {
                            Text(text = it.name)
                            Spacer(modifier = Modifier.weight(1f))
                            Switch(
                                checked = checked,
                                onCheckedChange = null,
                                enabled = !submitting
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
fun SettingContent_Preview_Loading() {
    MyAppTheme {
        SettingContent(
            UiState.Loading
        )
    }
}

@Preview
@Composable
fun SettingContent_Preview_Error() {
    MyAppTheme {
        SettingContent(
            UiState.Error(IOException())
        )
    }
}

@Preview
@Composable
fun SettingContent_Preview_Success() {
    MyAppTheme {
        SettingContent(
            UiState.Success(
                listOf(
                    SwitchState(
                        id = SettingId("1"),
                        name = "A",
                        checked = true,
                        submitting = false
                    ),
                    SwitchState(
                        id = SettingId("2"),
                        name = "B",
                        checked = false,
                        submitting = false
                    ),
                    SwitchState(
                        id = SettingId("3"),
                        name = "C",
                        checked = true,
                        submitting = true
                    ),
                    SwitchState(
                        id = SettingId("4"),
                        name = "D",
                        checked = false,
                        submitting = true
                    ),
                )
            )
        )
    }
}
