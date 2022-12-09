package com.example.eksamenandroid

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RecipesDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "RecipesDB"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE Settings ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "calories INTEGER, " +
                    "history_items INTEGER, " +
                    "diet TEXT, " +
                    "cuisine TEXT, " +
                    "mealtype TEXT, " +
                    "url TEXT" +
                    ")"
        )

        db.execSQL(
            "CREATE TABLE History ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "data TEXT" +
                    ")"
        )

        db.execSQL(
            "CREATE TABLE Favorites ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "data TEXT" +
                    ")"
        )
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Settings")
        db.execSQL("DROP TABLE IF EXISTS History")
        db.execSQL("DROP TABLE IF EXISTS Favorites")
        onCreate(db)
    }
}

