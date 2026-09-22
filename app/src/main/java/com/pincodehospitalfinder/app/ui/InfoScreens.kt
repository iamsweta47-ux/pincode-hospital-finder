package com.pincodehospitalfinder.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(title: String, content: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = content, fontSize = 14.sp, lineHeight = 22.sp)
        }
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    InfoScreen(
        title = "About",
        content = """
            Pincode Hospital Finder helps you discover hospitals near any Indian PIN code.

            Simply enter a 6-digit PIN code, and we'll show you the top 3 hospitals nearby, along with directions, specialties (when available), and contact details.

            This app is designed for discovery purposes only. Always verify hospital services and availability directly with the hospital before visiting.
        """.trimIndent(),
        onBack = onBack
    )
}

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    InfoScreen(
        title = "Privacy Policy",
        content = """
            Data We Process:
            The PIN code you enter is used only to find nearby hospitals and is not stored on our servers.

            Location Usage:
            If you choose to use the "Use My Location" feature, your location is used only for that search and is not stored or shared.

            Third-Party Services:
            We use OpenStreetMap-based services to convert PIN codes into locations and to find nearby hospitals. These requests are sent directly to those services.

            Advertising:
            If advertisements are shown in this app, they may be provided by third-party ad networks, which may collect data as described in their own privacy policies.

            Data Retention:
            We do not knowingly store personal or sensitive health information.
        """.trimIndent(),
        onBack = onBack
    )
}

@Composable
fun TermsScreen(onBack: () -> Unit) {
    InfoScreen(
        title = "Terms of Use",
        content = """
            By using Pincode Hospital Finder, you agree to the following:

            Hospital information shown in this app is sourced from publicly available map data and is provided for discovery purposes only.

            We do not guarantee the accuracy, completeness, or availability of any hospital, service, or specialty listed.

            Always verify details directly with the hospital before relying on this information, especially in emergencies.

            This app does not provide medical advice or diagnosis.
        """.trimIndent(),
        onBack = onBack
    )
}

@Composable
fun ContactScreen(onBack: () -> Unit) {
    InfoScreen(
        title = "Contact",
        content = """
            For questions, feedback, or concerns about Pincode Hospital Finder, please reach out to us at:

            support@pincodehospitalfinder.app
        """.trimIndent(),
        onBack = onBack
    )
}
