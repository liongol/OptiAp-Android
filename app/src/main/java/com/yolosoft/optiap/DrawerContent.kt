package com.yolosoft.optiap

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(modifier: Modifier) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentPage by remember { mutableStateOf("home") }

    ModalNavigationDrawer(
        gesturesEnabled = true,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    icon = { Text("🏠") },
                    label = { Text("Home") },
                    onClick = {
                        currentPage = "home"
                        scope.launch { drawerState.close() }
                    },
                    selected = currentPage == "home"
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = { Text("📁") },
                    label = { Text("Files") },
                    onClick = {
                        currentPage = "files"
                        scope.launch { drawerState.close() }
                    },
                    selected = currentPage == "files"
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    icon = { Text("🔧") },
                    label = { Text("Settings") },
                    onClick = {
                        currentPage = "settings"
                        scope.launch { drawerState.close() }
                    },
                    selected = currentPage == "settings"
                )
            }
        },
        content = {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                if (currentPage == "home") "OptiAP"
                                else currentPage.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                                }
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                },
                content = {
                    when (currentPage) {
                        "home" -> HomePage(modifier = Modifier.padding(it))
                        "files" -> FilesPage(modifier = Modifier.padding(it))
                        "settings" -> SettingsPage(modifier = Modifier.padding(it))
                    }
                }
            )
        }
    )
}