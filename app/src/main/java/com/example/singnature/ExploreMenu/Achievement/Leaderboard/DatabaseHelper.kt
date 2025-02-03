package com.example.singnature.ExploreMenu.Achievement.Leaderboard

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Constants for database name and version
    companion object {
        private const val DATABASE_NAME = "leaderboard.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create the leaderboard table
        val createTableQuery = "CREATE TABLE leaderboard (username TEXT, badges INTEGER)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed
    }

    fun getReadableDatabaseInstance(): SQLiteDatabase {
        return readableDatabase
    }
}
