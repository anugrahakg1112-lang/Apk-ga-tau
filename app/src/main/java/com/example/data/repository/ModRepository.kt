package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.DownloadedModEntity
import com.example.data.local.ModEntity
import com.example.data.local.ModFavoriteEntity
import com.example.data.local.ModReviewEntity
import com.example.data.model.ModItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class ModRepository(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val dao = db.modDao()

    val allMods: Flow<List<ModItem>> = combine(
        dao.getAllMods(),
        dao.getAllDownloadedMods(),
        dao.getAllFavorites()
    ) { mods, downloadedList, favorites ->
        val downloadedMap = downloadedList.associateBy { it.modId }
        val favoriteSet = favorites.map { it.modId }.toSet()

        mods.map { entity ->
            val downloaded = downloadedMap[entity.id]
            val isDownloaded = downloaded != null
            val installedVer = downloaded?.installedVersion
            val hasUpdate = isDownloaded && installedVer != null && installedVer != entity.latestVersion

            ModItem(
                id = entity.id,
                title = entity.title,
                author = entity.author,
                category = entity.category,
                minecraftVersion = entity.minecraftVersion,
                latestVersion = entity.latestVersion,
                description = entity.description,
                fileSize = entity.fileSize,
                fileType = entity.fileType,
                downloadsCount = entity.downloadsCount,
                averageRating = entity.averageRating,
                ratingCount = entity.ratingCount,
                changelog = entity.changelog,
                featuresList = entity.features.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                isUserUploaded = entity.isUserUploaded,
                createdAt = entity.createdAt,
                isDownloaded = isDownloaded,
                installedVersion = installedVer,
                hasUpdateAvailable = hasUpdate,
                downloadedAt = downloaded?.downloadedAt,
                isFavorite = favoriteSet.contains(entity.id)
            )
        }
    }.flowOn(Dispatchers.IO)

    val downloadedMods: Flow<List<ModItem>> = combine(
        allMods,
        dao.getAllDownloadedMods()
    ) { all, downloadedList ->
        val downloadedIds = downloadedList.map { it.modId }.toSet()
        all.filter { it.id in downloadedIds }
    }.flowOn(Dispatchers.IO)

    val favoriteMods: Flow<List<ModItem>> = allMods.combine(dao.getAllFavorites()) { mods, favs ->
        val favIds = favs.map { it.modId }.toSet()
        mods.filter { it.id in favIds }
    }.flowOn(Dispatchers.IO)

    fun getReviews(modId: String): Flow<List<ModReviewEntity>> {
        return dao.getReviewsForMod(modId).flowOn(Dispatchers.IO)
    }

    suspend fun downloadMod(mod: ModItem): Result<File> = withContext(Dispatchers.IO) {
        try {
            // Generate realistic local mod file in app external files directory
            val modsDir = File(context.getExternalFilesDir(null), "minecraft_mods")
            if (!modsDir.exists()) modsDir.mkdirs()

            val fileName = "${mod.id}_${mod.latestVersion.replace(".", "_")}${mod.fileType}"
            val modFile = File(modsDir, fileName)
            if (!modFile.exists()) {
                modFile.writeText("CraftMod Minecraft Package Content: ${mod.title} - Version ${mod.latestVersion}\nAuthor: ${mod.author}\nTarget: ${mod.minecraftVersion}")
            }

            // Record in downloaded table
            dao.insertDownloadedMod(
                DownloadedModEntity(
                    modId = mod.id,
                    installedVersion = mod.latestVersion,
                    downloadedAt = System.currentTimeMillis(),
                    localFileName = fileName,
                    fileSize = mod.fileSize,
                    isInstalled = true
                )
            )

            // Increment download count in mod entity
            val current = dao.getModByIdSync(mod.id)
            if (current != null) {
                dao.updateMod(current.copy(downloadsCount = current.downloadsCount + 1))
            }

            Result.success(modFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateModVersion(modId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val mod = dao.getModByIdSync(modId) ?: return@withContext Result.failure(Exception("Mod tidak ditemukan"))
            val downloaded = dao.getDownloadedModSync(modId) ?: return@withContext Result.failure(Exception("Mod belum diunduh"))

            val modsDir = File(context.getExternalFilesDir(null), "minecraft_mods")
            val fileName = "${mod.id}_${mod.latestVersion.replace(".", "_")}${mod.fileType}"
            val modFile = File(modsDir, fileName)
            modFile.writeText("CraftMod Updated Package: ${mod.title} - Version ${mod.latestVersion}")

            dao.insertDownloadedMod(
                downloaded.copy(
                    installedVersion = mod.latestVersion,
                    downloadedAt = System.currentTimeMillis(),
                    localFileName = fileName,
                    fileSize = mod.fileSize
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteDownloadedMod(modId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val downloaded = dao.getDownloadedModSync(modId)
            if (downloaded != null) {
                val modsDir = File(context.getExternalFilesDir(null), "minecraft_mods")
                val modFile = File(modsDir, downloaded.localFileName)
                if (modFile.exists()) modFile.delete()
                dao.deleteDownloadedMod(modId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadMod(
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
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val newId = "user-mod-${UUID.randomUUID().toString().take(8)}"
            val entity = ModEntity(
                id = newId,
                title = title,
                author = author.ifBlank { "Minecraft Creator" },
                category = category,
                minecraftVersion = minecraftVersion,
                latestVersion = modVersion.ifBlank { "v1.0.0" },
                description = description,
                fileSize = fileSize.ifBlank { "12.5 MB" },
                fileType = fileType,
                downloadsCount = 1,
                averageRating = 5.0f,
                ratingCount = 1,
                changelog = changelog.ifBlank { "Rilis pertama mod" },
                features = features.ifBlank { "Fitur baru kustom,Kompatibel dengan Minecraft Bedrock" },
                isUserUploaded = true,
                createdAt = System.currentTimeMillis()
            )
            dao.insertMod(entity)

            // Auto-install creator's own uploaded mod
            val modsDir = File(context.getExternalFilesDir(null), "minecraft_mods")
            if (!modsDir.exists()) modsDir.mkdirs()
            val fileName = "${newId}_${entity.latestVersion.replace(".", "_")}${entity.fileType}"
            val modFile = File(modsDir, fileName)
            modFile.writeText("CraftMod User Uploaded: $title\nCreated by: $author")

            dao.insertDownloadedMod(
                DownloadedModEntity(
                    modId = newId,
                    installedVersion = entity.latestVersion,
                    downloadedAt = System.currentTimeMillis(),
                    localFileName = fileName,
                    fileSize = entity.fileSize,
                    isInstalled = true
                )
            )

            // Add creator's initial note review
            dao.insertReview(
                ModReviewEntity(
                    id = "rev-${UUID.randomUUID()}",
                    modId = newId,
                    userName = author.ifBlank { "Kreator" },
                    rating = 5,
                    comment = "Selamat menikmati mod buatan saya! Jangan ragu beri ulasan jika ada saran pembaruan.",
                    tag = "Kreator Mod",
                    isVerifiedDownloader = true
                )
            )

            Result.success(newId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addReview(
        modId: String,
        userName: String,
        rating: Int,
        comment: String,
        tag: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val currentMod = dao.getModByIdSync(modId) ?: return@withContext Result.failure(Exception("Mod tidak ditemukan"))
            val isDownloaded = dao.getDownloadedModSync(modId) != null

            val review = ModReviewEntity(
                id = "rev-${UUID.randomUUID()}",
                modId = modId,
                userName = userName.ifBlank { "Pengguna CraftMod" },
                rating = rating.coerceIn(1, 5),
                comment = comment,
                tag = tag,
                timestamp = System.currentTimeMillis(),
                isVerifiedDownloader = isDownloaded
            )
            dao.insertReview(review)

            // Recalculate average rating
            val newRatingCount = currentMod.ratingCount + 1
            val newAvg = ((currentMod.averageRating * currentMod.ratingCount) + rating) / newRatingCount
            val roundedAvg = (Math.round(newAvg * 10.0) / 10.0).toFloat()

            dao.updateMod(
                currentMod.copy(
                    averageRating = roundedAvg,
                    ratingCount = newRatingCount
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(modId: String): Unit = withContext(Dispatchers.IO) {
        val favs = dao.getAllFavorites()
        // Check if exists
        val dbFav = AppDatabase.getDatabase(context).modDao()
        val allFav = withContext(Dispatchers.IO) {
            // We can directly toggle
            try {
                dbFav.insertFavorite(ModFavoriteEntity(modId = modId))
            } catch (e: Exception) {
                dbFav.deleteFavorite(modId)
            }
        }
    }

    suspend fun removeFavorite(modId: String): Unit = withContext(Dispatchers.IO) {
        dao.deleteFavorite(modId)
    }

    suspend fun addFavorite(modId: String): Unit = withContext(Dispatchers.IO) {
        dao.insertFavorite(ModFavoriteEntity(modId = modId))
    }

    suspend fun deleteUploadedMod(modId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            deleteDownloadedMod(modId)
            dao.deleteMod(modId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
