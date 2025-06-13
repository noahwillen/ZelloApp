package com.zello.sdk.example.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.zello.sdk.ZelloCredentials
import com.zello.sdk.example.app.repositories.ZelloRepository
import com.zello.sdk.example.app.utils.PermissionsManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.compose.composable
import com.zello.sdk.example.app.screens.Screen
import com.zello.sdk.example.app.screens.channels.ChannelsScreen
import com.zello.sdk.example.app.screens.recents.RecentsScreen
import com.zello.sdk.example.app.screens.recents.RecentsViewModel
import com.zello.sdk.example.app.screens.channels.ChannelsViewModel


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	@Inject lateinit var repository: ZelloRepository

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		repository.zello.start()

		enableEdgeToEdge()
		supportActionBar?.hide()
		setContent {
			ApplicationTheme{
				val navController = rememberNavController()

				Scaffold(
					modifier = Modifier
						.fillMaxSize(),
					bottomBar = {BottomNavBar(navController)}
				) { innerPadding ->
					val graph =
						navController.createGraph(startDestination = Screen.Channels.rout) {
							composable(route = Screen.Channels.rout) {
								val viewModel: ChannelsViewModel = hiltViewModel()
								ChannelsScreen(viewModel)
							}
							composable(route = Screen.Recents.rout) {
								val viewModel: RecentsViewModel = hiltViewModel()
								RecentsScreen(viewModel)
							}
						}
					NavHost(
						navController = navController,
						graph = graph,
						modifier = Modifier.padding(innerPadding)
					)
					AccountDialog(repository)
				}
			}
		}

		PermissionsManager(this).requestPermissions()
	}
}


@Composable
fun AccountDialog(
	repository: ZelloRepository) {
	var showDialog by remember { mutableStateOf(false)}
	var network by remember { mutableStateOf("")}
	var username by remember { mutableStateOf("")}
	var password by remember { mutableStateOf("")}
	val isConnected by repository.isConnected.collectAsState()

	IconButton(
		onClick = {showDialog = true},
		modifier = Modifier
			.windowInsetsPadding(WindowInsets.statusBars)
			.size(48.dp)
	) {
		Icon(
			imageVector = Icons.Default.AccountCircle,
			contentDescription = "Sign In",
			modifier = Modifier.size(40.dp),
			tint = MaterialTheme.colorScheme.onBackground
		)
	}
	if (showDialog) {
		if (isConnected) {
			AlertDialog(
				onDismissRequest = { showDialog = false},
				title = {Text("Account")},
				text = {
					Column {
						OutlinedTextField(
							value = network,
							onValueChange = {},
							label = {Text("Network")},
							singleLine = true,
							readOnly = true
						)
						Spacer(modifier = Modifier.height(8.dp))
						OutlinedTextField(
							value = username,
							onValueChange = {},
							label = {Text("Username")},
							singleLine = true,
							readOnly = true
						)
					}
			    },
				confirmButton = {
					Button(onClick = {
						repository.zello.disconnect()
					}) {
						Text("Sign Out")
					}
				}
			)
		} else {
			AlertDialog(
				onDismissRequest = {showDialog = false},
				title = {Text("Sign In")},
				text = {
					Column {
						OutlinedTextField(
							value = network,
							onValueChange = {network = it},
							label = {Text("Network")},
							singleLine = true
						)
						Spacer(modifier = Modifier.height(8.dp))
						OutlinedTextField(
							value = username,
							onValueChange = {username = it},
							label = {Text("Username")},
							singleLine = true
						)
						Spacer(modifier = Modifier.height(8.dp))
						OutlinedTextField(
							value = password,
							onValueChange = {password = it},
							label = {Text("Password")},
							singleLine = true,
							visualTransformation = PasswordVisualTransformation()
						)
						Spacer(modifier = Modifier.height(8.dp))
					}
				},
				confirmButton = {
					Button(onClick = {
						repository.zello.connect(ZelloCredentials(network, username, password))
						showDialog = false
					}) {
						Text("Ok")
					}
				},
				dismissButton = {
					Button(onClick = {showDialog = false}) {
						Text("Cancel")
					}
				}
			)
		}
	}
}
