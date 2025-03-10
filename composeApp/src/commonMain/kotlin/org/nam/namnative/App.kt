package org.nam.namnative

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext
import org.koin.compose.currentKoinScope
import org.nam.namnative.mediaplayer.MediaPlayerScreen
import org.nam.namnative.record.RecordScreen
import org.nam.namnative.recordlist.RecordListScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        KoinContext {
            val navController = rememberNavController()
            NavHost(navController, startDestination = "recordList") {
                composable("recordList") {
                    RecordListScreen(
                        onRecordClicked = {
                            navController.navigate("record")
                        },
                        onRecordItemClicked = {
                            navController.navigate("record/$it")
                        },
                    )
                }

                composable("record") {
                    RecordScreen {
                        navController.popBackStack()
                    }
                }

                composable("record/{id}") {
                    val id = it.arguments?.getString("id") ?: ""
                    MediaPlayerScreen(id) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> koinViewModel(): T {
    val scope = currentKoinScope()
    return viewModel { scope.get<T>() }
}