package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ModReviewEntity
import com.example.data.model.MinecraftVersions
import com.example.data.model.ModCategories
import com.example.data.model.ModItem
import com.example.data.model.SortOption
import com.example.data.repository.ModRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ModViewModel(application: Application) : AndroidViewModel(application) {
    val repository = ModRepository(application)

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ModCategories.ALL)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedVersion = MutableStateFlow(MinecraftVersions.ALL)
    val selectedVersion = _selectedVersion.asStateFlow()

    private val _selectedSort = MutableStateFlow(SortOption.POPULAR)
    val selectedSort = _selectedSort.asStateFlow()

    // Download & Update Progress Simulation State
    private val _downloadingModIds = MutableStateFlow<Map<String, Float>>(emptyMap())
    val downloadingModIds = _downloadingModIds.asStateFlow()

    private val _updatingModIds = MutableStateFlow<Set<String>>(emptySet())
    val updatingModIds = _updatingModIds.asStateFlow()

    private val _isCheckingUpdates = MutableStateFlow(false)
    val isCheckingUpdates = _isCheckingUpdates.asStateFlow()

    // Snack / Notification Events
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    // Selected Mod for Detail View
    private val _selectedModId = MutableStateFlow<String?>(null)
    val selectedModId = _selectedModId.asStateFlow()

    // Filter in Mod Manager: "Semua", "Ada Pembaruan", "Addon", "Texture"
    private val _managerFilter = MutableStateFlow("Semua")
    val managerFilter = _managerFilter.asStateFlow()

    // Base flows from repository
    val allMods = repository.allMods.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val downloadedMods = repository.downloadedMods.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val favoriteMods = repository.favoriteMods.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Filtered and Sorted Mods for Explore Screen
    val filteredMods: StateFlow<List<ModItem>> = combine(
        allMods,
        _searchQuery,
        _selectedCategory,
        _selectedVersion,
        _selectedSort
    ) { mods, query, category, version, sort ->
        var result = mods

        // Search text matching name, description, author, or category
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            result = result.filter { item ->
                item.title.lowercase().contains(q) ||
                item.description.lowercase().contains(q) ||
                item.author.lowercase().contains(q) ||
                item.category.lowercase().contains(q) ||
                item.featuresList.any { it.lowercase().contains(q) }
            }
        }

        // Category filter
        if (category != ModCategories.ALL) {
            result = result.filter { it.category.equals(category, ignoreCase = true) }
        }

        // Minecraft Version filter
        if (version != MinecraftVersions.ALL) {
            result = result.filter { it.minecraftVersion.equals(version, ignoreCase = true) }
        }

        // Sorting
        when (sort) {
            SortOption.POPULAR -> result.sortedByDescending { it.downloadsCount }
            SortOption.TOP_RATED -> result.sortedByDescending { it.averageRating }
            SortOption.NEWEST -> result.sortedByDescending { it.createdAt }
            SortOption.FILE_SIZE -> result.sortedBy { parseSizeToMb(it.fileSize) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Downloaded Mods for Mod Manager Screen
    val filteredDownloadedMods: StateFlow<List<ModItem>> = combine(
        downloadedMods,
        _managerFilter
    ) { mods, filter ->
        when (filter) {
            "Ada Pembaruan" -> mods.filter { it.hasUpdateAvailable }
            "Petualangan" -> mods.filter { it.category == ModCategories.ADVENTURE }
            "Bangunan" -> mods.filter { it.category == ModCategories.BUILDING }
            "Mekanikal" -> mods.filter { it.category == ModCategories.MECHANICAL }
            "Shader & Tekstur" -> mods.filter { it.category == ModCategories.SHADERS }
            else -> mods
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedModDetail: StateFlow<ModItem?> = combine(
        allMods,
        _selectedModId
    ) { mods, id ->
        if (id == null) null else mods.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun getReviews(modId: String): StateFlow<List<ModReviewEntity>> {
        return repository.getReviews(modId).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSelectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun onSelectVersion(version: String) {
        _selectedVersion.value = version
    }

    fun onSelectSort(sort: SortOption) {
        _selectedSort.value = sort
    }

    fun setManagerFilter(filter: String) {
        _managerFilter.value = filter
    }

    fun selectMod(modId: String?) {
        _selectedModId.value = modId
    }

    fun downloadMod(mod: ModItem) {
        if (_downloadingModIds.value.containsKey(mod.id)) return

        viewModelScope.launch {
            // Animate download progress from 0% to 100%
            val currentMap = _downloadingModIds.value.toMutableMap()
            currentMap[mod.id] = 0.1f
            _downloadingModIds.value = currentMap

            for (step in 2..10) {
                delay(120)
                val progress = step / 10f
                _downloadingModIds.value = _downloadingModIds.value.toMutableMap().apply {
                    put(mod.id, progress)
                }
            }

            // Save in DB and storage
            val result = repository.downloadMod(mod)
            _downloadingModIds.value = _downloadingModIds.value.toMutableMap().apply {
                remove(mod.id)
            }

            if (result.isSuccess) {
                _snackbarEvent.emit("Berhasil mengunduh '${mod.title}'! Mod siap dipasang ke Minecraft.")
            } else {
                _snackbarEvent.emit("Gagal mengunduh: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun updateMod(mod: ModItem) {
        if (_updatingModIds.value.contains(mod.id)) return

        viewModelScope.launch {
            _updatingModIds.value = _updatingModIds.value + mod.id
            delay(1200) // simulation of downloading the delta patch
            val res = repository.updateModVersion(mod.id)
            _updatingModIds.value = _updatingModIds.value - mod.id

            if (res.isSuccess) {
                _snackbarEvent.emit("Mod '${mod.title}' berhasil diperbarui ke ${mod.latestVersion}!")
            } else {
                _snackbarEvent.emit("Gagal memperbarui: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun deleteDownloadedMod(mod: ModItem) {
        viewModelScope.launch {
            val res = repository.deleteDownloadedMod(mod.id)
            if (res.isSuccess) {
                _snackbarEvent.emit("Mod '${mod.title}' berhasil dihapus dari penyimpanan.")
            } else {
                _snackbarEvent.emit("Gagal menghapus: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            _isCheckingUpdates.value = true
            delay(1500)
            val installed = downloadedMods.value
            val updateCount = installed.count { it.hasUpdateAvailable }
            _isCheckingUpdates.value = false

            if (updateCount > 0) {
                _snackbarEvent.emit("Ditemukan $updateCount pembaruan mod baru yang siap diunduh!")
            } else {
                _snackbarEvent.emit("Semua ${installed.size} mod terinstal Anda sudah versi terbaru!")
            }
        }
    }

    fun submitReview(
        modId: String,
        userName: String,
        rating: Int,
        comment: String,
        tag: String
    ) {
        viewModelScope.launch {
            val res = repository.addReview(
                modId = modId,
                userName = userName,
                rating = rating,
                comment = comment,
                tag = tag
            )
            if (res.isSuccess) {
                _snackbarEvent.emit("Terima kasih! Ulasan & rating bintang $rating Anda telah disimpan.")
            } else {
                _snackbarEvent.emit("Gagal menyimpan ulasan.")
            }
        }
    }

    fun uploadMod(
        title: String,
        author: String,
        category: String,
        minecraftVersion: String,
        modVersion: String,
        description: String,
        fileSize: String,
        fileType: String,
        changelog: String,
        features: String
    ) {
        viewModelScope.launch {
            val res = repository.uploadMod(
                title = title,
                author = author,
                category = category,
                minecraftVersion = minecraftVersion,
                modVersion = modVersion,
                description = description,
                fileSize = fileSize,
                fileType = fileType,
                changelog = changelog,
                features = features
            )
            if (res.isSuccess) {
                _snackbarEvent.emit("Mod '$title' berhasil diunggah dan ditambahkan ke katalog!")
            } else {
                _snackbarEvent.emit("Gagal mengunggah mod.")
            }
        }
    }

    fun toggleFavorite(mod: ModItem) {
        viewModelScope.launch {
            if (mod.isFavorite) {
                repository.removeFavorite(mod.id)
                _snackbarEvent.emit("Dihapus dari favorit.")
            } else {
                repository.addFavorite(mod.id)
                _snackbarEvent.emit("Ditambahkan ke favorit!")
            }
        }
    }

    private fun parseSizeToMb(sizeStr: String): Float {
        return try {
            sizeStr.replace("MB", "", ignoreCase = true).trim().toFloat()
        } catch (e: Exception) {
            0f
        }
    }
}
