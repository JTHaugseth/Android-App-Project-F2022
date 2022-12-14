package com.example.eksamenandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SearchHistory : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_history)

        val rv = findViewById<RecyclerView>(R.id.SearchHistoryRV)
        val allData = getSearchHistory()
        val allDataValidated = validateAllData(allData)
        rv.adapter = SearchHistoryAdapter(this@SearchHistory, allDataValidated)
    }

    private fun getSearchHistory(): ArrayList<String> {
        val historyItems = ArrayList<String>()
        val recipesDB = RecipesDB(this)
        val db = recipesDB.readableDatabase
        val cursor = db.query("History", null, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val searchInput = cursor.getString(cursor.getColumnIndexOrThrow("searchInput"))
            historyItems.add(searchInput)
        }
        db.close()
        cursor.close()
        return historyItems
    }

    private fun validateAllData(allData: ArrayList<String>): ArrayList<String> {
        val recipesDB = RecipesDB(this)
        val db = recipesDB.readableDatabase
        val cursor = db.query("Settings", null, null, null, null, null, null, null)
        cursor.moveToFirst()
        val maxHistoryItems = cursor.getString(cursor.getColumnIndexOrThrow("history_items")).toInt()
        allData.reverse()

        if (allData.size > maxHistoryItems) {
            for (i in allData.size - 1 downTo maxHistoryItems) {
                allData.removeAt(i)
            }
        }

        return allData
    }

    fun returnToMainMenu(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}