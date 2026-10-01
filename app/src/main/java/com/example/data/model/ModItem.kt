package com.example.data.model

data class ModItem(
    val id: String,
    val title: String,
    val author: String,
    val category: String, // 'Petualangan', 'Bangunan', 'Mekanikal', 'Shader & Tekstur', 'Senjata & Zirah', 'Makhluk & Mobs'
    val minecraftVersion: String,
    val latestVersion: String, // e.g. "v2.1.0"
    val description: String,
    val fileSize: String, // e.g. "24.5 MB"
    val fileType: String, // ".mcaddon", ".mcpack", ".zip"
    val downloadsCount: Int,
    val averageRating: Float,
    val ratingCount: Int,
    val changelog: String,
    val featuresList: List<String>,
    val isUserUploaded: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    
    // Status relative to current device
    val isDownloaded: Boolean = false,
    val installedVersion: String? = null,
    val hasUpdateAvailable: Boolean = false,
    val downloadedAt: Long? = null,
    val isFavorite: Boolean = false
)

object ModCategories {
    const val ALL = "Semua"
    const val ADVENTURE = "Petualangan"
    const val BUILDING = "Bangunan"
    const val MECHANICAL = "Mekanikal"
    const val SHADERS = "Shader & Tekstur"
    const val WEAPONS = "Senjata & Zirah"
    const val MOBS = "Makhluk & Mobs"

    val list = listOf(ALL, ADVENTURE, BUILDING, MECHANICAL, SHADERS, WEAPONS, MOBS)
}

object MinecraftVersions {
    const val ALL = "Semua Versi"
    const val V1_21 = "1.21.x Tricky Trials"
    const val V1_20 = "1.20.x Trails & Tales"
    const val V1_19 = "1.19.x The Wild"
    const val JAVA = "Java Edition 1.20+"

    val list = listOf(ALL, V1_21, V1_20, V1_19, JAVA)
}

enum class SortOption(val displayName: String) {
    POPULAR("Terpopuler"),
    TOP_RATED("Rating Tertinggi"),
    NEWEST("Terbaru"),
    FILE_SIZE("Ukuran File")
}
