package com.example.eksamenandroid

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RecipesDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    
    // Creates the database, tables and rows we need to store information in our app.
    companion object {
        private const val DATABASE_NAME = "RecipesDB"
        private const val DATABASE_VERSION = 1
    }
    
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL (
            "CREATE TABLE Settings ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "calories INTEGER, " +
                    "history_items INTEGER, " +
                    "diet TEXT, " +
                    "cuisine TEXT, " +
                    "mealtype TEXT " +
                    ")"
        )

        db.execSQL (
            "CREATE TABLE TodaysMeals ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT, " +
                    "image TEXT, " +
                    "calories INTEGER, " +
                    "dietLabel TEXT, " +
                    "healthLabel TEXT, " +
                    "cautions TEXT " +
                    ")"
        )
        db.execSQL (
            "CREATE TABLE History ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "searchInput TEXT, " +
                    "searchUrl TEXT" +
                    ")"
        )

        db.execSQL (
            "CREATE TABLE Favorites ( " +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT, " +
                    "image TEXT, " +
                    "calories INTEGER, " +
                    "dietLabel TEXT, " +
                    "healthLabel TEXT, " +
                    "cautions TEXT, " +
                    "url TEXT" +
                    ")"
        )
    }
    
    // If changes has been applied to the database, it will delete all tables and re-create them for the current updated version. 
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Settings")
        db.execSQL("DROP TABLE IF EXISTS TodaysMeals")
        db.execSQL("DROP TABLE IF EXISTS History")
        db.execSQL("DROP TABLE IF EXISTS Favorites")
        onCreate(db)
    }
}

