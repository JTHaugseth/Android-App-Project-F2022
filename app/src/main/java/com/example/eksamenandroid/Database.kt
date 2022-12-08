package com.example.eksamenandroid

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "RecipesDB"
        private const val DATABASE_VERSION = 1

        const val TABLE_HISTORY = "History"
        const val TABLE_FAVORITES = "Favorites"
        const val TABLE_SETTINGS = "Settings"
        const val COLUMN_ID = "id"
        const val COLUMN_DATA = "data"
        const val COLUMN_CALORIES = "calories"
        const val COLUMN_HISTORY_ITEMS = "history_items"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createHistoryTableSql = "CREATE TABLE $TABLE_HISTORY($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_DATA TEXT)"
        db?.execSQL(createHistoryTableSql)

        val createFavoritesTableSql = "CREATE TABLE $TABLE_FAVORITES($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_DATA TEXT)"
        db?.execSQL(createFavoritesTableSql)

        val createSettingsTableSql = "CREATE TABLE $TABLE_SETTINGS($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_DATA TEXT, $COLUMN_CALORIES INTEGER, $COLUMN_HISTORY_ITEMS INTEGER)"
        db?.execSQL(createSettingsTableSql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropHistoryTableSql = "DROP TABLE IF EXISTS $TABLE_HISTORY"
        db?.execSQL(dropHistoryTableSql)
        val dropFavoritesTableSql = "DROP TABLE IF EXISTS $TABLE_FAVORITES"
        db?.execSQL(dropFavoritesTableSql)
        val dropSettingsTableSql = "DROP TABLE IF EXISTS $TABLE_SETTINGS"
        db?.execSQL(dropSettingsTableSql)
        onCreate(db)
    }
}