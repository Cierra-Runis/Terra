package pers.cierra_runis.terra.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import pers.cierra_runis.terra.ui.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage() {
    val navController = LocalNavController.current

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(text = "设置")
            }, navigationIcon = {
                if (navController.previousBackStackEntry != null) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                    }
                }
            }
        )
    }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
        }
    }
}

@Preview
@Composable
fun PreviewSettingsPage() {
    SettingsPage()
}