package com.instriker.wcre.repositories

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.util.Log

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

import com.instriker.wcre.R
import com.instriker.wcre.config.Constants

//https://gist.github.com/1271795

class GameContentOpenHelper(private val context: Context) {

    @Throws(IOException::class)
    fun openDatabase(): SQLiteDatabase {
        val dbFile = context.getDatabasePath(DB_NAME)

        if (!_databaseInited) {
            Log.i("GameContentOpenHelper", "Initializing the database")
            try {
                this.initialiseDatabaseForCurrentVersion()
            } finally {
                _databaseInited = true
            }
        }

        return openReadonlyDatabase(dbFile)
    }

    @Throws(IOException::class)
    private fun initialiseDatabaseForCurrentVersion() {
        val dbFile = context.getDatabasePath(DB_NAME)

        var db: SQLiteDatabase?
        try {
            db = openReadonlyDatabaseForCurrentVersion(dbFile)
        } catch (ex: Exception) {
            db = null
        }

        // OK found, just continue
        if (db != null) {
            Log.i("GameContentOpenHelper", "Database already exists, skipping initialization")
            db.close()
            return
        }

        // Could not find the db, initialize it
        this.copyDatabase(dbFile)
        db = openReadonlyDatabaseForCurrentVersion(dbFile)

        if (db == null) {
            throw IOException("Could not find the database for the current application version.")
        } else {
            db.close()
        }
    }

    @Throws(IOException::class)
    private fun openReadonlyDatabaseForCurrentVersion(dbFile: File): SQLiteDatabase? {
        var db: SQLiteDatabase?
        try {
            db = openReadonlyDatabase(dbFile)
        } catch (ex: SQLiteException) {
            return null
        }

        val version: Int
        try {
            version = db.version
            Log.i("GameContentOpenHelper", "Current database is version $version")
        } catch (ex: SQLiteException) {
            db.close()
            return null
        }

        val expectedVersion = this.context.resources.getInteger(R.integer.expectedDatabaseVersion)
        if (version != expectedVersion) {
            Log.e("GameContentOpenHelper", "Invalid version, was expecting $expectedVersion")
            db.close()
            db = null
        }
        return db
    }

    @Throws(IOException::class, SQLiteException::class)
    private fun openReadonlyDatabase(dbFile: File): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READONLY or SQLiteDatabase.NO_LOCALIZED_COLLATORS)
    }

    @Throws(IOException::class)
    private fun copyDatabase(dbFile: File) {
        Log.i("GameContentOpenHelper", "Creating new Database file")
        if (createNewDatabase(dbFile)) {
            copyDatabaseContent(dbFile)
        } else {
            throw IOException("Could not initialize database file.")
        }
    }

    @Throws(IOException::class)
    private fun createNewDatabase(dbFile: File): Boolean {
        try {
            var success = true
            // Create file if it does not exist
            val path = dbFile.parentFile
            if (!path.exists()) {
                success = path.mkdirs()
            }

            if (dbFile.exists()) {
                success = success && dbFile.delete()
            }
            success = success && dbFile.createNewFile()
            return success
        } catch (e: IOException) {
            if (Constants.DEBUG) {
                e.printStackTrace()
                Log.e("GameContentOpenHelper", "Unexpected error occurs while coping game content database", e)
            }
            throw e
        }

    }

    @Throws(IOException::class)
    private fun copyDatabaseContent(dbFile: File) {
        Log.i("GameContentOpenHelper", "Filling Database data")

        var inputStream: InputStream? = null
        try {
            inputStream = context.assets.open(DB_NAME)

            FileOutputStream(dbFile).use { os ->
                inputStream.copyTo(os)

                Log.i("GameContentOpenHelper", "Database ready")
                os.flush()
            }
        } catch (e: IOException) {
            if (Constants.DEBUG) {
                e.printStackTrace()
                Log.e("GameContentOpenHelper", "Unexpected error occurs while coping game content database", e)
            }
            throw e
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                if (Constants.DEBUG) {
                    e.printStackTrace()
                    Log.e("GameContentOpenHelper", "Unexpected error occurs while coping game content database", e)
                }
                throw e
            }

        }
    }

    companion object {
        private val DB_NAME = "RedbgcContent.sqlite"
        private var _databaseInited = false
    }
}