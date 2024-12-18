package com.first.project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.lifecycleScope
import com.first.project.model.Contact
import com.first.project.services.CallService
import com.first.project.ui.theme.FirstProjectTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var requestContactsPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestCallPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var requestNotificationPermissionLauncher: ActivityResultLauncher<String>
    private var showBottomSheet = mutableStateOf(false)
    private var contactsList: List<Contact> = ArrayList()

    private val viewModel: MainViewModel by viewModels {
        ContactsViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirstProjectTheme(darkTheme = viewModel.themeState.value) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CreateUi()
                    if (showBottomSheet.value) {
                        SelectContactSheet()
                    }
                }
            }
        }

        requestContactsPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    showBottomSheet.value = true
                } else {
                    Toast.makeText(this, "Contacts permission needed", Toast.LENGTH_SHORT).show()
                }
            }

        requestCallPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    if (!PermissionUtils.checkNotificationPermission(this)) {
                        PermissionUtils.askNotificationPermission(
                            this,
                            requestNotificationPermissionLauncher
                        )
                    } else {
                        startCallService(this)
                    }
                } else {
                    Toast.makeText(this, "Call permission needed", Toast.LENGTH_SHORT).show()
                }
            }

        requestNotificationPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startCallService(this)
                } else {
                    Toast.makeText(this, "Notification permission needed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    @Preview
    @Composable
    fun CreateUi() {
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.background
                )
        ) {
            Image(
                painter = painterResource(
                    if (viewModel.themeState.value) {
                        R.drawable.ic_light_theme
                    } else {
                        R.drawable.ic_dark_theme
                    }
                ),
                contentDescription = "theme_button",
                modifier = Modifier
                    .clickable {
                        viewModel.themeState.value = !viewModel.themeState.value
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
                            requestContactsPermissionLauncher
                        )
                    }
                }) {
                    Text(text = "Browse Contacts")
                }
                TextField(
                    value = viewModel.contact.value.phone.toString(),
                    onValueChange = {
                        viewModel.contact.value.phone
                    },
                    modifier = Modifier.background(Color.White)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    if (!PermissionUtils.checkCallPermission(context)) {
                        PermissionUtils.askCallPermission(context, requestCallPermissionLauncher)
                    } else {
                        if (!PermissionUtils.checkNotificationPermission(context)) {
                            PermissionUtils.askNotificationPermission(
                                context,
                                requestNotificationPermissionLauncher
                            )
                        } else {
                            startCallService(context)
                        }
                    }
                }, modifier = Modifier.padding(20.dp)) {
                    Text(text = "Start call service")
                }
                Button(onClick = {
                    stopCallService(context)
                }, modifier = Modifier.padding(20.dp)) {
                    Text(text = "End Call service")
                }
                Text(
                    text = "Do not press power button or close app when call service is running.",
                    color = Color.Gray,
                    modifier = Modifier.padding(20.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    private fun startCallService(context: Context) {
        if (viewModel.validatePhone()) {
            Toast.makeText(context, "Started calling service...", Toast.LENGTH_SHORT).show()

            val serviceIntent = Intent(context, CallService::class.java)
            serviceIntent.putExtra(Constants.PHONE_NUMBER, viewModel.contact.value)
            startForegroundService(context, serviceIntent)
        } else {
            Toast.makeText(context, "Please select a contact", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopCallService(context: Context) {
        context.stopService(Intent(context, CallService::class.java))
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    private fun SelectContactSheet() {
        if (contactsList.isEmpty()) {
            getContacts()
        }

        val sheetState = rememberModalBottomSheetState()
        val scope = rememberCoroutineScope()
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            // Sheet content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Select Contact", fontSize = 20.sp)
                    Image(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = "close bottom sheet",
                        modifier = Modifier.clickable {
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet.value = false
                                }
                            }
                        }
                    )
                }
                HorizontalDivider(thickness = 1.dp, color = Color.Gray)
                if (viewModel.contactsProgress.value) {
                    Row(modifier = Modifier.padding(0.dp, 50.dp, 0.dp, 0.dp)) {
                        Text(text = "Reading Contacts")
                        CircularProgressIndicator(
                            color = Color.DarkGray,
                            strokeWidth = 4.dp,
                            modifier = Modifier
                                .padding(10.dp, 0.dp, 0.dp, 0.dp)
                                .size(20.dp)
                        )
                    }
                } else {
                    LazyColumn {
                        items(contactsList) { item ->
                            ContactItem(item)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ContactItem(contact: Contact) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                showBottomSheet.value = false
                viewModel.contact.value = contact
            }) {
            Text(
                text = contact.name.toString(),
                //color = Color.White,
                modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)
            )
            Text(text = contact.phone.toString(), color = Color.Gray)
        }
    }

    private fun getContacts() {
        lifecycleScope.launch {
            viewModel.contactsFlow.collect { contactList ->
                contactsList = contactList.sortedBy { it.name }
                viewModel.contactsProgress.value = false
            }
        }
    }
}

