package app.krafted.fitlog.data.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1).apply {
            // Fill with some data if needed, or close to prepare for migration
            close()
        }

        // Run migration 1 -> 2
        helper.runMigrationsAndValidate(
            TEST_DB,
            2,
            true, // validate schema
            DatabaseModule.MIGRATION_1_2
        )
    }

    @Test
    @Throws(IOException::class)
    fun migrate2To3() {
        var db = helper.createDatabase(TEST_DB, 2).apply {
            // Fill with some data (e.g., exercises for FK constraint check)
            execSQL("INSERT INTO exercises (id, name, primaryMuscle, secondaryMuscles, category, equipment, thumbnailRes) VALUES (1, 'Test Exercise', 'Chest', '', 'Strength', 'Barbell', null)")
            close()
        }

        // Run migration 2 -> 3
        helper.runMigrationsAndValidate(
            TEST_DB,
            3,
            true, // validate schema
            DatabaseModule.MIGRATION_2_3
        )
    }

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create earliest version
        helper.createDatabase(TEST_DB, 1).apply { 
            close() 
        }

        // Run all migrations to latest
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            TEST_DB
        )
            .addMigrations(DatabaseModule.MIGRATION_1_2, DatabaseModule.MIGRATION_2_3)
            .build()
            .apply {
                openHelper.writableDatabase.close()
            }
    }
}
