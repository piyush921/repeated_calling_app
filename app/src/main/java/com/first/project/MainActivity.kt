package com.first.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.viewmodel.compose.viewModel
import com.first.project.ui.theme.FirstProjectTheme
import com.first.project.ui.theme.materialBlack
import com.first.project.ui.theme.materialLight
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var viewModel: MainViewModel
    private var showBottomSheet = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirstProjectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    viewModel = viewModel()
                    CreateUi()
                    if (showBottomSheet.value) {
                        BottomSheet()
                    }
                }
            }
        }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Permission granted
                    showBottomSheet.value = true
                } else {
                    // Permission denied
                }
            }
    }

    @Composable
    @Preview
    fun CreateUi() {
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (viewModel.themeState.value == MainViewModel.LIGHT_THEME) {
                        materialBlack
                    } else {
                        materialLight
                    }
                )
        ) {
            Image(
                painter = painterResource(
                    if (viewModel.themeState.value == MainViewModel.LIGHT_THEME) {
                        R.drawable.ic_light_theme
                    } else {
                        R.drawable.ic_dark_theme
                    }
                ),
                contentDescription = "theme_button",
                modifier = Modifier
                    .clickable {
                        if (viewModel.themeState.value == MainViewModel.LIGHT_THEME) {
                            viewModel.themeState.value = MainViewModel.DARK_THEME
                        } else {
                            viewModel.themeState.value = MainViewModel.LIGHT_THEME
                        }
                    }
                    .padding(10.dp),
                alignment = Alignment.TopEnd,
            )

            Row(modifier = Modifier.padding(10.dp, 40.dp, 0.dp, 0.dp)) {
                Button(onClick = {
                    if (PermissionUtils.checkReadContactsPermission(context)) {
                        showBottomSheet.value = true
                    } else {
                        PermissionUtils.askReadContactsPermission(
                            context,
                            requestPermissionLauncher
                        )
                    }
                }) {
                    Text(text = "Browse Contacts")
                }
                TextField(value = viewModel.contact.value, onValueChange = {
                    viewModel.contact.value
                }, modifier = Modifier.background(Color.White))
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    /*Toast.makeText(context, "Started calling service...", Toast.LENGTH_SHORT).show()
                val serviceIntent = Intent(context, CallService::class.java)
                startForegroundService(context, serviceIntent)*/
                }, modifier = Modifier.padding(20.dp)) {
                    Text(text = "Start call service")
                }
                Button(onClick = {
                    /*context.stopService(Intent(context, CallService::class.java))*/
                }, modifier = Modifier.padding(20.dp)) {
                    Text(text = "End Call service")
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun BottomSheet() {
        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            // Sheet content
            Text(text = "Sheet content")
            Button(onClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet.value = false
                    }
                }
            }) {
                Text("Hide bottom sheet")
            }
        }
    }
}

