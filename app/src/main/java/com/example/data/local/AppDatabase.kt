package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ModEntity::class,
        DownloadedModEntity::class,
        ModReviewEntity::class,
        ModFavoriteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun modDao(): ModDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "craftmod_database"
                )
                .addCallback(DatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    seedDatabase(database.modDao())
                }
            }
        }

        private suspend fun seedDatabase(dao: ModDao) {
            val initialMods = listOf(
                ModEntity(
                    id = "dragon-mounts-reborn",
                    title = "Dragon Mounts: Legacy Reborn",
                    author = "BarracudaATA & ModTeam",
                    category = "Petualangan",
                    minecraftVersion = "1.21.x Tricky Trials",
                    latestVersion = "v2.1.0",
                    description = "Jinakkan dan terbangkan 8 jenis naga legenda! Termasuk Naga Ender, Naga Api, Naga Es, Naga Hutan, dan Naga Hantu. Dilengkapi armor naga berlian, pelana kustom, dan serangan nafas naga unik. Jelajahi dunia Minecraft dari angkasa dengan mulus.",
                    fileSize = "24.5 MB",
                    fileType = ".mcaddon",
                    downloadsCount = 42800,
                    averageRating = 4.8f,
                    ratingCount = 312,
                    changelog = "v2.1.0: Kompatibel dengan Tricky Trials 1.21, animasi terbang diperhalus, penambahan efek partikel nafas es baru, perbaikan bug crash saat mendarat.",
                    features = "8 Jenis Naga Jinak,Armor & Pelana Naga Kustom,Sistem Telur Naga Menetas,Nafas Elemen Spesial,Dukungan Multiplayer Bedrock",
                    isUserUploaded = false
                ),
                ModEntity(
                    id = "create-mechanical-pocket",
                    title = "Create: Mechanical Automation PE",
                    author = "Simi & Team Create",
                    category = "Mekanikal",
                    minecraftVersion = "1.21.x Tricky Trials",
                    latestVersion = "v1.5.0",
                    description = "Hadirkan keajaiban mekanika gir berputar, sabuk konveyor, kincir angin, bor penambang otomatis, dan mesin uap kinetik. Bangun pabrik otomatis tercanggih dan jalur kereta api otomatis di Minecraft Bedrock!",
                    fileSize = "32.1 MB",
                    fileType = ".mcaddon",
                    downloadsCount = 67500,
                    averageRating = 4.9f,
                    ratingCount = 520,
                    changelog = "v1.5.0: Piston rotasi generasi baru, pengurangan beban render tick hingga 40%, penambahan pipa cairan tembaga, dan blok pengontrol kecepatan kinetik.",
                    features = "Sistem Energi Kinetik & Gir,Sabuk Konveyor & Pemilah Otomatis,Bor Tambang Raksasa,Kereta Api Kustom Bergerak,Pabrik Otomasi Mandiri",
                    isUserUploaded = false
                ),
                ModEntity(
                    id = "decocraft-modern-interior",
                    title = "DecoCraft: Perabotan & Interior Modern",
                    author = "RazzleberryFox Studio",
                    category = "Bangunan",
                    minecraftVersion = "1.20.x Trails & Tales",
                    latestVersion = "v3.2.0",
                    description = "Lebih dari 300+ blok dekorasi fungsional: sofa lembut yang bisa diduduki, kulkas penyimpanan makanan, meja kopi marmer, lampu gantung neon, rak buku modern, dan set komputer gaming RGB lengkap!",
                    fileSize = "16.8 MB",
                    fileType = ".mcpack",
                    downloadsCount = 51200,
                    averageRating = 4.7f,
                    ratingCount = 289,
                    changelog = "v3.2.0: Penambahan 45 furnitur ruang keluarga modern, perbaikan collision box kursi, suara sakelar lampu interaktif.",
                    features = "300+ Model 3D Furnitur,Interaksi Duduk & Buka Pintu,Set Dapur & Ruang Tamu Mewah,Pencahayaan Lampu Dinamis,Ringan Tanpa Lag",
                    isUserUploaded = false
                ),
                ModEntity(
                    id = "luminous-rtx-shaders",
                    title = "Luminous Ultra Shaders HD",
                    author = "ShaderMaster Lab",
                    category = "Shader & Tekstur",
                    minecraftVersion = "1.21.x Tricky Trials",
                    latestVersion = "v4.2.0",
                    description = "Visual menakjubkan kelas konsol untuk Minecraft Android. Bayangan lembut dinamis, pantulan air berkilauan dengan gelombang jernih, lambaian dedaunan pohon dan rumput ditiup angin, serta langit senja hiper-realistis.",
                    fileSize = "45.2 MB",
                    fileType = ".mcpack",
                    downloadsCount = 89400,
                    averageRating = 4.9f,
                    ratingCount = 840,
                    changelog = "v4.2.0: Dukungan Render Dragon engine terbaru, optimasi performa stabil 60 FPS pada perangkat RAM 4GB+, kabut atmosfer pagi hari yang diperbarui.",
                    features = "Pantulan Air Realistis & Gelombang,Bayangan Lembut Sinar Matahari,Animasi Lambaian Daun & Rumput,Langit Malam Bintang 4K,Optimasi Anti-Drop FPS",
                    isUserUploaded = false
                ),
                ModEntity(
                    id = "dungeon-crawl-bosses",
                    title = "Dungeon Crawl & Ancient Bosses",
                    author = "MythicWorlds RPG",
                    category = "Petualangan",
                    minecraftVersion = "1.21.x Tricky Trials",
                    latestVersion = "v2.0.4",
                    description = "Eksplorasi kastil bawah tanah dan labirin berlantai 5 yang dihasilkan secara prosedural. Hadapi jebakan mematikan, kumpulkan peti artefak berkekuatan magis, dan kalahkan bos kuno seperti Void Lich dan Nether Knight.",
                    fileSize = "19.3 MB",
                    fileType = ".mcaddon",
                    downloadsCount = 38900,
                    averageRating = 4.6f,
                    ratingCount = 205,
                    changelog = "v2.0.4: Bos baru Void Golem di lantai terdalam, drop item relik senjata magis baru, penyesuaian tingkat kesulitan monster.",
                    features = "Labirin Prosedural Bawah Tanah,5 Bos Kustom dengan Fase Serangan Unik,Peti Harta Karun Berhadiah Epik,Pedang & Jubah Magis Eksklusif,Musik Suasana Menantang",
                    isUserUploaded = false
                ),
                ModEntity(
                    id = "redstone-power-circuits",
                    title = "Redstone Power & Industrial Circuit",
                    author = "TeknikCraft ID",
                    category = "Mekanikal",
                    minecraftVersion = "1.20.x Trails & Tales",
                    latestVersion = "v1.4.0",
                    description = "Paket sirkuit redstone mini yang menghemat ruang hingga 75%. Dilengkapi baterai redstone, kabel terisolasi yang bisa dipasang di dinding atau langit-langit, sensor nirkabel, dan gerbang logika terpadu (AND/OR/XOR).",
                    fileSize = "11.4 MB",
                    fileType = ".mcaddon",
                    downloadsCount = 29400,
                    averageRating = 4.7f,
                    ratingCount = 178,
                    changelog = "v1.4.0: Pemancar nirkabel jarak jauh hingga 128 blok, perbaikan glitch timing repeater, dukungan kabel multiwarna.",
                    features = "Kabel Redstone Fleksibel Dinding,Sensor Redstone Nirkabel Jarak Jauh,Baterai Penyimpan Sinyal,Panel Tenaga Surya Efisien,Lift Otomatis Satu Tombol",
                    isUserUploaded = false
                ),
                ModEntity(
                    id = "epic-medieval-armors",
                    title = "Epic Medieval Weapons & Armors",
                    author = "Blacksmith Forge",
                    category = "Senjata & Zirah",
                    minecraftVersion = "1.21.x Tricky Trials",
                    latestVersion = "v1.9.0",
                    description = "Persenjatai diri Anda dengan senjata abad pertengahan legendaris! Menambahkan katana berlapis netherite, tombak panjang, palu perang berdaya hantam ganda, busur silang berat, dan 14 set zirah ksatria dengan bonus pasif pertahanan.",
                    fileSize = "14.7 MB",
                    fileType = ".mcaddon",
                    downloadsCount = 44100,
                    averageRating = 4.8f,
                    ratingCount = 310,
                    changelog = "v1.9.0: Senjata Mace compatibilities, kombo tebasan katana baru, efek knockback palu perang yang disesuaikan.",
                    features = "14 Zirah Ksatria Kerajaan,Katana, Halberd & Warhammer,Sistem Kombo & Parrying Serangan,Enchantment Kustom Khusus Senjata Abad Pertengahan,Model 3D HD Detil",
                    isUserUploaded = false
                ),
                ModEntity(
                    id = "mutant-creatures-beasts",
                    title = "Mutant Creatures & Epic Beasts",
                    author = "Chudov Labs",
                    category = "Makhluk & Mobs",
                    minecraftVersion = "1.21.x Tricky Trials",
                    latestVersion = "v2.5.0",
                    description = "Ubah mob biasa menjadi mutan raksasa berotot yang menakutkan! Mutant Zombie yang membanting tanah, Mutant Creeper berkepala lima yang dapat melompat, Mutant Enderman yang melempar bongkahan batu, dan mutant wolf peliharaan yang setia.",
                    fileSize = "21.0 MB",
                    fileType = ".mcaddon",
                    downloadsCount = 58200,
                    averageRating = 4.8f,
                    ratingCount = 460,
                    changelog = "v2.5.0: Mutant Breeze baru dengan tornado angin dahsyat, animasi mati mutan yang lebih sinematik, penurunan HP Mutant Skeleton agar lebih seimbang.",
                    features = "10+ Mob Mutan Raksasa,Sistem Ramuan Kimia X untuk Mutasi,Mob Serigala Mutan Jinak,Item Spesial Palu Tulang Creeper,Suara Kustom Menyeramkan",
                    isUserUploaded = false
                ),
                ModEntity(
                    id = "chisel-micro-blocks",
                    title = "Chisel & Bits: Ukiran Blok Mikro",
                    author = "BlockMaster Studio",
                    category = "Bangunan",
                    minecraftVersion = "1.20.x Trails & Tales",
                    latestVersion = "v1.3.5",
                    description = "Alat pahat revolusioner untuk memotong blok kubus standar menjadi 4096 bit kecil! Buat lengkungan sempurna, patung pahatan detail, jembatan berukir indah, dan furnitur mini sesuai imajinasi arsitek Anda.",
                    fileSize = "8.9 MB",
                    fileType = ".mcpack",
                    downloadsCount = 22700,
                    averageRating = 4.5f,
                    ratingCount = 142,
                    changelog = "v1.3.5: Tambahan alat pahat berlian yang lebih presisi, fitur copy-paste rancangan ukiran, optimasi memori untuk struktur besar.",
                    features = "Pahat Blok ke Skala Mikro 1/16,Kombinasikan Berbagai Tekstur Blok,Simpan Desain Blueprints,Bebas Bangun Bentuk Apapun,Sangat Stabil di Handphone",
                    isUserUploaded = false
                )
            )
            dao.insertMods(initialMods)

            // Seed sample downloaded mod with an older version to demonstrate the "Update Available" notification!
            val sampleDownloaded = DownloadedModEntity(
                modId = "dragon-mounts-reborn",
                installedVersion = "v1.8.0", // older than v2.1.0 -> Shows "Pembaruan Tersedia!"
                downloadedAt = System.currentTimeMillis() - 86400000L * 3,
                localFileName = "dragon_mounts_reborn.mcaddon",
                fileSize = "22.1 MB",
                isInstalled = true
            )
            dao.insertDownloadedMod(sampleDownloaded)

            val sampleDownloaded2 = DownloadedModEntity(
                modId = "decocraft-modern-interior",
                installedVersion = "v3.2.0", // up to date!
                downloadedAt = System.currentTimeMillis() - 86400000L,
                localFileName = "decocraft_furniture.mcpack",
                fileSize = "16.8 MB",
                isInstalled = true
            )
            dao.insertDownloadedMod(sampleDownloaded2)

            // Seed initial reviews
            val sampleReviews = listOf(
                ModReviewEntity(
                    id = "rev-1",
                    modId = "dragon-mounts-reborn",
                    userName = "Rian_Craft99",
                    rating = 5,
                    comment = "Keren banget naganya bisa terbang kencang dan nafas api berfungsi normal di Minecraft PE 1.21! Sangat direkomendasikan.",
                    tag = "Wajib Dicoba",
                    timestamp = System.currentTimeMillis() - 86400000L * 2,
                    isVerifiedDownloader = true
                ),
                ModReviewEntity(
                    id = "rev-2",
                    modId = "dragon-mounts-reborn",
                    userName = "Dimas_Miner",
                    rating = 5,
                    comment = "Instalasi gampang tinggal klik buka langsung terimpor ke Minecraft. Naganya tidak ada bug sama sekali.",
                    tag = "Tanpa Bug",
                    timestamp = System.currentTimeMillis() - 86400000L * 4,
                    isVerifiedDownloader = true
                ),
                ModReviewEntity(
                    id = "rev-3",
                    modId = "decocraft-modern-interior",
                    userName = "Siti_Builder",
                    rating = 5,
                    comment = "Suka sekali dengan set dapurnya! Sangat cocok untuk dekorasi rumah modern di survival mode.",
                    tag = "Sangat Keren",
                    timestamp = System.currentTimeMillis() - 86400000L,
                    isVerifiedDownloader = true
                ),
                ModReviewEntity(
                    id = "rev-4",
                    modId = "luminous-rtx-shaders",
                    userName = "Aldi_Gamer",
                    rating = 5,
                    comment = "Grafik air dan pantulan cahayanya memanjakan mata, dan yang paling penting HP tidak panas.",
                    tag = "Ringan",
                    timestamp = System.currentTimeMillis() - 86400000L * 5,
                    isVerifiedDownloader = true
                )
            )
            dao.insertReviews(sampleReviews)
        }
    }
}
