package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mods")
data class ModEntity(
    @PrimaryKey val id: String,
    val title: String,
    val author: String,
    val category: String,
    val minecraftVersion: String,
    val latestVersion: String,
    val description: String,
    val fileSize: String,
    val fileType: String,
    val downloadsCount: Int,
    val averageRating: Float,
    val ratingCount: Int,
    val changelog: String,
    val features: String, // comma separated
    val isUserUploaded: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloaded_mods")
data class DownloadedModEntity(
    @PrimaryKey val modId: String,
    val installedVersion: String,
    val downloadedAt: Long = System.currentTimeMillis(),
    val localFileName: String,
    val fileSize: String,
    val isInstalled: Boolean = true
)

@Entity(tableName = "mod_reviews")
data class ModReviewEntity(
    @PrimaryKey val id: String,
    val modId: String,
    val userName: String,
    val rating: Int, // 1 to 5
    val comment: String,
    val tag: String = "Mantap",
    val timestamp: Long = System.currentTimeMillis(),
    val isVerifiedDownloader: Boolean = true
)

@Entity(tableName = "mod_favorites")
data class ModFavoriteEntity(
    @PrimaryKey val modId: String,
    val savedAt: Long = System.currentTimeMillis()
)
