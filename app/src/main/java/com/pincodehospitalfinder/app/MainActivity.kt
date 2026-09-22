@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.pincodehospitalfinder.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pincodehospitalfinder.app.data.Hospital
import com.pincodehospitalfinder.app.preferences.ThemePreferences
import com.pincodehospitalfinder.app.viewmodel.HospitalViewModel
import com.pincodehospitalfinder.app.viewmodel.SearchState
import kotlinx.coroutines.launch

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF0D6EFD),
    background = androidx.compose.ui.graphics.Color(0xFFFDF8FF),
    surface = androidx.compose.ui.graphics.Color(0xFFFDF8FF)
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF4C9AFF),
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E)
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themePrefs = ThemePreferences(applicationContext)
        setContent {
            val isDarkMode by themePrefs.isDarkMode.collectAsState(initial = false)
            val scope = rememberCoroutineScope()

            MaterialTheme(colorScheme = if (isDarkMode) DarkColors else LightColors) {
                HomeScreen(
                    isDarkMode = isDarkMode,
                    onToggleTheme = {
                        scope.launch { themePrefs.setDarkMode(!isDarkMode) }
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: HospitalViewModel = viewModel(),
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit
) {
    var pinCode by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pincode Hospital Finder", fontSize = 18.sp) },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = if (isDarkMode) "Switch to light mode" else "Switch to dark mode"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Find trusted hospitals near any PIN code.",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = pinCode,
                onValueChange = {
                    if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                        pinCode = it
                        validationError = null
                    }
                },
                label = { Text("Enter PIN Code") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = validationError != null
            )

            validationError?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (pinCode.length != 6) {
                        validationError = "Please enter a valid 6-digit PIN code"
                    } else {
                        validationError = null
                        viewModel.searchHospitals(pinCode)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Search Hospitals")
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (val state = viewModel.searchState) {
                is SearchState.Idle -> {
                    Text(
                        text = "Enter a PIN code to discover nearby hospitals.",
                        fontSize = 13.sp
                    )
                }
                is SearchState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Finding hospitals near you…", fontSize = 13.sp)
                        }
                    }
                }
                is SearchState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp
                    )
                }
                is SearchState.Success -> {
                    Text(
                        text = "Top hospitals near $pinCode",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(state.hospitals) { hospital ->
                            HospitalCard(hospital = hospital, onDirectionsClick = {
                                val uri = Uri.parse("geo:${hospital.latitude},${hospital.longitude}?q=${hospital.latitude},${hospital.longitude}(${hospital.name})")
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                            })
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Hospital information is provided for discovery purposes. Please verify services and availability directly with the hospital.",
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HospitalCard(hospital: Hospital, onDirectionsClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = hospital.name, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = hospital.address, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "%.1f km away".format(hospital.distanceKm), fontSize = 13.sp)

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Specialty: ${hospital.specialty ?: "Specialty information unavailable"}",
                fontSize = 12.sp
            )

            hospital.openingHours?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Hours: $it", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onDirectionsClick, modifier = Modifier.weight(1f)) {
                    Text("View Directions")
                }
            }
        }
    }
}
