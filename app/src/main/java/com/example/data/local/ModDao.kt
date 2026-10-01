package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ModDao {
    @Query("SELECT * FROM mods ORDER BY createdAt DESC")
    fun getAllMods(): Flow<List<ModEntity>>

    @Query("SELECT * FROM mods WHERE id = :id")
    fun getModById(id: String): Flow<ModEntity?>

    @Query("SELECT * FROM mods WHERE id = :id")
    suspend fun getModByIdSync(id: String): ModEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMods(mods: List<ModEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMod(mod: ModEntity)

    @Update
    suspend fun updateMod(mod: ModEntity)

    @Query("DELETE FROM mods WHERE id = :id")
    suspend fun deleteMod(id: String)

    @Query("SELECT COUNT(*) FROM mods")
    suspend fun getModCount(): Int

    // Downloaded mods
    @Query("SELECT * FROM downloaded_mods ORDER BY downloadedAt DESC")
    fun getAllDownloadedMods(): Flow<List<DownloadedModEntity>>

    @Query("SELECT * FROM downloaded_mods WHERE modId = :modId")
    fun getDownloadedMod(modId: String): Flow<DownloadedModEntity?>

    @Query("SELECT * FROM downloaded_mods WHERE modId = :modId")
    suspend fun getDownloadedModSync(modId: String): DownloadedModEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadedMod(downloaded: DownloadedModEntity)

    @Query("DELETE FROM downloaded_mods WHERE modId = :modId")
    suspend fun deleteDownloadedMod(modId: String)

    // Reviews
    @Query("SELECT * FROM mod_reviews WHERE modId = :modId ORDER BY timestamp DESC")
    fun getReviewsForMod(modId: String): Flow<List<ModReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ModReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ModReviewEntity>)

    // Favorites
    @Query("SELECT * FROM mod_favorites ORDER BY savedAt DESC")
    fun getAllFavorites(): Flow<List<ModFavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(fav: ModFavoriteEntity)

    @Query("DELETE FROM mod_favorites WHERE modId = :modId")
    suspend fun deleteFavorite(modId: String)
}
