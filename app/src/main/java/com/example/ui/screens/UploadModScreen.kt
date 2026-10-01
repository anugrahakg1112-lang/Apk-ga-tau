package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MinecraftVersions
import com.example.data.model.ModCategories
import com.example.data.model.ModItem
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.DiamondCyan
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.viewmodel.ModViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UploadModScreen(
    viewModel: ModViewModel,
    onModUploaded: (ModItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ModCategories.ADVENTURE) }
    var selectedVersion by remember { mutableStateOf(MinecraftVersions.V1_21) }
    var modVersion by remember { mutableStateOf("v1.0.0") }
    var fileType by remember { mutableStateOf(".mcaddon") }
    var fileSize by remember { mutableStateOf("14.2 MB") }
    var description by remember { mutableStateOf("") }
    var features by remember { mutableStateOf("") }
    var changelog by remember { mutableStateOf("Rilis versi perdana") }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var isSuccessNotification by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "custom_minecraft_mod.mcaddon"
            selectedFileName = fileName
            if (fileName.endsWith(".mcpack", ignoreCase = true)) {
                fileType = ".mcpack"
            } else if (fileName.endsWith(".zip", ignoreCase = true)) {
                fileType = ".zip"
            } else {
                fileType = ".mcaddon"
            }
        }
    }

    val availableCategories = ModCategories.list.filter { it != ModCategories.ALL }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Unggah Mod Minecraft Baru",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary
                    )
                )
                Text(
                    text = "Publikasikan kreasi addon, tekstur, atau mod mekanikal Anda ke komunitas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Success Card Banner
            AnimatedVisibility(visible = isSuccessNotification) {
                Surface(
                    color = EmeraldPrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(EmeraldPrimary)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Mod berhasil diunggah dan terbit di tab Jelajah!",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = EmeraldPrimary
                        )
                    }
                }
            }

            // Section 1: File Mod Attachment
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(ObsidianCardBorder)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Berkas Mod / Addon",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Pilih berkas format .mcaddon, .mcpack, atau .zip",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                filePickerLauncher.launch("*/*")
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderZip,
                                contentDescription = null,
                                tint = DiamondCyan,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedFileName ?: "Pilih berkas mod dari perangkat...",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (selectedFileName != null) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (selectedFileName != null) "Berkas siap dipaketkan ($fileType)" else "Atau gunakan berkas generator bawaan",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = "Pilih Berkas",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick File Format Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(".mcaddon", ".mcpack", ".zip").forEach { format ->
                            val isSelected = fileType == format
                            FilterChip(
                                selected = isSelected,
                                onClick = { fileType = format },
                                label = { Text(format) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = DiamondCyan,
                                    selectedLabelColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // Section 2: Info & Details
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(ObsidianCardBorder)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Identitas & Informasi Mod",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Judul Mod *") },
                        placeholder = { Text("Contoh: Mecha Titan Robot Addon") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_title_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = author,
                            onValueChange = { author = it },
                            label = { Text("Nama Pembuat *") },
                            placeholder = { Text("SteveMaker_ID") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("upload_author_input")
                        )

                        OutlinedTextField(
                            value = modVersion,
                            onValueChange = { modVersion = it },
                            label = { Text("Versi Mod") },
                            placeholder = { Text("v1.0.0") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(0.7f)
                                .testTag("upload_mod_version_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Selection
                    Text(
                        text = "Pilih Kategori Mod:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableCategories.forEach { cat ->
                            val isSelected = cat == selectedCategory
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = getCategoryIcon(cat),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("upload_category_$cat")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Minecraft Version Selection
                    Text(
                        text = "Kompatibilitas Minecraft:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MinecraftVersions.list.filter { it != MinecraftVersions.ALL }.forEach { ver ->
                            val isSelected = ver == selectedVersion
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedVersion = ver },
                                label = { Text(ver) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldPrimary,
                                    selectedLabelColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Deskripsi Lengkap Mod *") },
                        placeholder = { Text("Jelaskan apa yang baru dalam mod ini, cara kerja item, resep crafting, dan keseruan bermain...") },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("upload_description_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = features,
                        onValueChange = { features = it },
                        label = { Text("Fitur Unggulan (pisahkan dengan koma)") },
                        placeholder = { Text("Robot dapat dikendarai, Meriam laser plasma, Animasi kokpit") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = changelog,
                        onValueChange = { changelog = it },
                        label = { Text("Catatan Pembaruan (Changelog)") },
                        placeholder = { Text("v1.0.0: Rilis awal addon") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Submit Button
            val isFormValid = title.isNotBlank() && description.isNotBlank()

            Button(
                onClick = {
                    if (isFormValid) {
                        val computedFeatures = if (features.isNotBlank()) {
                            features
                        } else {
                            "Dukungan Minecraft ${selectedVersion},Tekstur & Model Kustom,Stabil Tanpa Bug"
                        }

                        viewModel.uploadMod(
                            title = title.trim(),
                            author = author.trim().ifBlank { "Kreator CraftMod" },
                            category = selectedCategory,
                            minecraftVersion = selectedVersion,
                            modVersion = modVersion.trim().ifBlank { "v1.0.0" },
                            description = description.trim(),
                            fileSize = fileSize,
                            fileType = fileType,
                            changelog = changelog.trim(),
                            features = computedFeatures
                        )

                        isSuccessNotification = true
                        title = ""
                        description = ""
                        features = ""
                        selectedFileName = null
                    }
                },
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_upload_mod_button")
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Unggah & Terbitkan Mod",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
