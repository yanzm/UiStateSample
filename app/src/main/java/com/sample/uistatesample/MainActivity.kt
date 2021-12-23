package com.sample.uistatesample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sample.uistatesample.data.OrderId
import com.sample.uistatesample.di.assistedViewModel
import com.sample.uistatesample.ui.section1.DogListScreen
import com.sample.uistatesample.ui.section2.PagingDogListScreen
import com.sample.uistatesample.ui.section3.OrderInfoScreen
import com.sample.uistatesample.ui.section3.OrderInfoViewModel
import com.sample.uistatesample.ui.section4.SettingScreen
import com.sample.uistatesample.ui.section5.AddNoteScreen
import com.sample.uistatesample.ui.section6.EditNicknameScreen
import com.sample.uistatesample.ui.section7.EditNicknameScreen2
import com.sample.uistatesample.ui.theme.MyAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "top"
    ) {
        composable("top") {
            TopScreen(
                onClickSection1 = {
                    navController.navigate("section1")
                },
                onClickSection2 = {
                    navController.navigate("section2")
                },
                onClickSection3 = {
                    navController.navigate("section3/1")
                },
                onClickSection4 = {
                    navController.navigate("section4")
                },
                onClickSection5 = {
                    navController.navigate("section5")
                },
                onClickSection6 = {
                    navController.navigate("section6")
                },
                onClickSection7 = {
                    navController.navigate("section7")
                }
            )
        }

        composable("section1") {
            DogListScreen(
                viewModel = hiltViewModel(),
                onClickDog = {
                    // TODO
                }
            )
        }

        composable("section2") {
            PagingDogListScreen(
                viewModel = hiltViewModel(),
                onClickDog = {
                    // TODO
                }
            )
        }

        composable("section3/{orderId}") {
            val arguments = requireNotNull(it.arguments)
            val orderId = OrderId(requireNotNull(arguments.getString("orderId")))

            OrderInfoScreen(
                viewModel = assistedViewModel {
                    OrderInfoViewModel.create(LocalContext.current, orderId)
                },
                onClickBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("section4") {
            SettingScreen(
                viewModel = hiltViewModel(),
                onClickBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("section5") {
            AddNoteScreen(
                viewModel = hiltViewModel(),
                onClickBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("section6") {
            EditNicknameScreen(
                viewModel = hiltViewModel(),
                onClickBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("section7") {
            EditNicknameScreen2(
                viewModel = hiltViewModel(),
                onClickBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun TopScreen(
    onClickSection1: () -> Unit,
    onClickSection2: () -> Unit,
    onClickSection3: () -> Unit,
    onClickSection4: () -> Unit,
    onClickSection5: () -> Unit,
    onClickSection6: () -> Unit,
    onClickSection7: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("UiStateSample")
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            ListItem(
                text = "① データ表示のみ",
                onClick = onClickSection1,
            )
            ListItem(
                text = "② データ表示（Paging）のみ",
                onClick = onClickSection2,
            )
            ListItem(
                text = "③ データ表示 + Submit",
                onClick = onClickSection3,
            )
            ListItem(
                text = "④ データ表示 + 部分変更",
                onClick = onClickSection4,
            )
            ListItem(
                text = "⑤ 入力 + Submit",
                onClick = onClickSection5,
            )
            ListItem(
                text = "⑥ データ表示 + 変更 + Submit",
                onClick = onClickSection6,
            )
            ListItem(
                text = "⑥ データ表示 + 変更 + Submit 2",
                onClick = onClickSection7,
            )
        }
    }
}

@Composable
private fun ListItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    )
}
