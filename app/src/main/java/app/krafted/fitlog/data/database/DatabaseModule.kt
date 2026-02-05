package app.krafted.fitlog.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import app.krafted.fitlog.data.database.dao.BodyweightDao
import app.krafted.fitlog.data.database.dao.ExerciseDao
import app.krafted.fitlog.data.database.dao.PersonalRecordDao
import app.krafted.fitlog.data.database.dao.RepRangeRecordDao
import app.krafted.fitlog.data.database.dao.RoutineDao
import app.krafted.fitlog.data.database.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            // .fallbackToDestructiveMigration() // Removed to enforce migrations
            .build()
    }

    internal val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Change personal_records index to UNIQUE
            // Since we can't easily modify an index, we drop the old one and create the new unique one
            db.execSQL("DROP INDEX IF EXISTS `index_personal_records_exerciseId`")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_personal_records_exerciseId` ON `personal_records` (`exerciseId`)")
        }
    }

    internal val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create rep_range_records table
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `rep_range_records` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                    `exerciseId` INTEGER NOT NULL, 
                    `repRange` TEXT NOT NULL, 
                    `bestWeight` REAL NOT NULL, 
                    `repsAtBestWeight` INTEGER NOT NULL, 
                    `estimated1RM` REAL NOT NULL, 
                    `achievedDate` INTEGER NOT NULL, 
                    FOREIGN KEY(`exerciseId`) REFERENCES `exercises`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            // Create indices for the new table
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_rep_range_records_exerciseId_repRange` ON `rep_range_records` (`exerciseId`, `repRange`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_rep_range_records_exerciseId` ON `rep_range_records` (`exerciseId`)")
        }
    }


    @Provides
    @Singleton
    fun provideExerciseDao(database: AppDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    @Singleton
    fun provideRoutineDao(database: AppDatabase): RoutineDao {
        return database.routineDao()
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(database: AppDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    @Singleton
    fun provideBodyweightDao(database: AppDatabase): BodyweightDao {
        return database.bodyweightDao()
    }

    @Provides
    @Singleton
    fun providePersonalRecordDao(database: AppDatabase): PersonalRecordDao {
        return database.personalRecordDao()
    }

    @Provides
    @Singleton
    fun provideRepRangeRecordDao(database: AppDatabase): RepRangeRecordDao {
        return database.repRangeRecordDao()
    }
}


