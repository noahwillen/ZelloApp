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
import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

	@OptIn(ExperimentalMaterial3Api::class)
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
					topBar = {
						TopAppBar(
							navigationIcon = {
								Image(
									painter = painterResource(id = R.drawable.csc_logo),
									contentDescription = "CSC Logo",
									modifier = Modifier.size(80.dp).padding(10.dp))
							},
							colors = TopAppBarDefaults.topAppBarColors(
								containerColor = MaterialTheme.colorScheme.primary,
								titleContentColor = MaterialTheme.colorScheme.onPrimary,
								navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
								actionIconContentColor = MaterialTheme.colorScheme.onPrimary
							),
							title = {
								Text(
									text= "Zello", style = MaterialTheme.typography.titleLarge,
									color = MaterialTheme.colorScheme.onBackground,
									fontWeight = FontWeight.Bold,
									fontSize = 40.sp
								)
							},
							actions = {
								var showDialog by remember { mutableStateOf(false) }
								IconButton(onClick = { showDialog = true }) {
									Icon(
										imageVector = Icons.Default.AccountCircle,
										contentDescription = "Sign In",
										modifier = Modifier.size(40.dp),
										tint = MaterialTheme.colorScheme.onBackground
									)
								}
								if (showDialog) {
									AccountDialog(repository) { showDialog = false }
								}
							},
							modifier = Modifier.shadow(0.dp)
						)
					},
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
				}
			}
		}

		PermissionsManager(this).requestPermissions()
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
		Log.d("PTT Debug", "Key pressed: $keyCode")
		if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK) { // Common for PTT buttons
			println("sdfsd")
			return true
		}
		return super.onKeyDown(keyCode, event)
	}

}

@Composable
fun AccountDialog(repository: ZelloRepository, onDismissRequest: () -> Unit) {
	var network by remember { mutableStateOf("")}
	var username by remember { mutableStateOf("")}
	var password by remember { mutableStateOf("")}
	val isConnected by repository.isConnected.collectAsState()

	if (isConnected) {
		AlertDialog(
			onDismissRequest = onDismissRequest,
			title = { Text("Account") },
			text = {
				Column {
					OutlinedTextField(value = network, onValueChange = {}, label = { Text("Network") }, singleLine = true, readOnly = true)
					Spacer(modifier = Modifier.height(8.dp))
					OutlinedTextField(value = username, onValueChange = {}, label = { Text("Username") }, singleLine = true, readOnly = true)
				}
			},
			confirmButton = {
				Button(onClick = { repository.zello.disconnect() }) {
					Text("Sign Out")
				}
			}
		)
	} else {
		AlertDialog(
			onDismissRequest = onDismissRequest,
			title = { Text("Sign In") },
			text = {
				Column {
					OutlinedTextField(value = network, onValueChange = {network = it}, label = { Text("Network") }, singleLine = true)
					Spacer(modifier = Modifier.height(8.dp))
					OutlinedTextField(value = username, onValueChange = {username = it}, label = { Text("Username") }, singleLine = true)
					Spacer(modifier = Modifier.height(8.dp))
					OutlinedTextField(value = password, onValueChange = {password = it}, label = { Text("Password") }, singleLine = true, visualTransformation = PasswordVisualTransformation())
					Spacer(modifier = Modifier.height(8.dp))
				}
			},
			confirmButton = {
				Button(onClick = {
					repository.zello.connect(ZelloCredentials(network,username,password))
					onDismissRequest()
				}) {
					Text("Ok")
				}
			},
			dismissButton = {
				Button(onClick = onDismissRequest) {
					Text("Cancel")
				}
			}
		)
	}
}

