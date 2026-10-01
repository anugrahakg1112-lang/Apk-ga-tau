package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FolderZip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ModItem
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RedstoneAccent
import com.example.ui.viewmodel.ModViewModel
import kotlinx.coroutines.flow.collectLatest

enum class NavigationTab(val label: String) {
    EXPLORE("Jelajah"),
    MANAGER("Manajer Mod"),
    UPLOAD("Unggah"),
    FAVORITES("Favorit")
}

@Composable
fun MainScreen(viewModel: ModViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val selectedMod by viewModel.selectedModDetail.collectAsStateWithLifecycle()
    val downloadedMods by viewModel.downloadedMods.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val updatesCount = downloadedMods.count { it.hasUpdateAvailable }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (selectedMod != null) {
        ModDetailScreen(
            mod = selectedMod!!,
            viewModel = viewModel,
            onBack = { viewModel.selectMod(null) }
        )
    } else {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    // Tab 0: Explore
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Default.Explore else Icons.Outlined.Explore,
                                contentDescription = "Jelajah"
                            )
                        },
                        label = { Text("Jelajah") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            indicatorColor = EmeraldPrimary
                        ),
                        modifier = Modifier.testTag("nav_explore")
                    )

                    // Tab 1: Mod Manager
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (updatesCount > 0) {
                                        Badge(containerColor = EmeraldPrimary, contentColor = Color.Black) {
                                            Text("$updatesCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedTab == 1) Icons.Default.FolderZip else Icons.Outlined.FolderZip,
                                    contentDescription = "Manajer Mod"
                                )
                            }
                        },
                        label = { Text("Manajer Mod") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            indicatorColor = EmeraldPrimary
                        ),
                        modifier = Modifier.testTag("nav_manager")
                    )

                    // Tab 2: Upload Mod
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Default.CloudUpload else Icons.Outlined.CloudUpload,
                                contentDescription = "Unggah Mod"
                            )
                        },
                        label = { Text("Unggah") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            indicatorColor = EmeraldPrimary
                        ),
                        modifier = Modifier.testTag("nav_upload")
                    )

                    // Tab 3: Favorites
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 3) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorit"
                            )
                        },
                        label = { Text("Favorit") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            indicatorColor = EmeraldPrimary
                        ),
                        modifier = Modifier.testTag("nav_favorites")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTab) {
                    0 -> ExploreScreen(
                        viewModel = viewModel,
                        onModClick = { mod -> viewModel.selectMod(mod.id) }
                    )
                    1 -> ModManagerScreen(
                        viewModel = viewModel,
                        onModClick = { mod -> viewModel.selectMod(mod.id) }
                    )
                    2 -> UploadModScreen(
                        viewModel = viewModel,
                        onModUploaded = { mod ->
                            viewModel.selectMod(mod.id)
                        }
                    )
                    3 -> FavoritesScreen(
                        viewModel = viewModel,
                        onModClick = { mod -> viewModel.selectMod(mod.id) }
                    )
                }
            }
        }
    }
}
