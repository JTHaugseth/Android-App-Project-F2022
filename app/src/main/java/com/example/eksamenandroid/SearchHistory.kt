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

        // Sends allData to SearchHistoryAdapter to populate the Recycler View.
        val rv = findViewById<RecyclerView>(R.id.SearchHistoryRV)
        val allData = getSearchHistory()
        val allDataValidated = validateAllData(allData)
        rv.adapter = SearchHistoryAdapter(this@SearchHistory, allDataValidated)
    }

    // Inner class to create SearchHistoryItems-object we can use in the adapter. 
    inner class SearchHistoryItems {
        var searchInput: String? = null
        var searchURL: String? = null
    }

    // Reads the History-database and creates objects to send to the adapter. 
    private fun getSearchHistory(): ArrayList<SearchHistoryItems> {
        val historyItems = ArrayList<SearchHistoryItems>()
        val recipesDB = RecipesDB(this)
        val db = recipesDB.readableDatabase
        val cursor = db.query("History", null, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val dataItem = SearchHistoryItems()
            dataItem.searchInput = cursor.getString(cursor.getColumnIndexOrThrow("searchInput"))
            dataItem.searchURL = cursor.getString(cursor.getColumnIndexOrThrow("searchUrl"))
            historyItems.add(dataItem)
        }
        db.close()
        cursor.close()
        return historyItems
    }

    // Reads the Settings to get maxHistoryItems allowed. 
    // It will then reverse allData to get the newest searched recipes on top.
    // If the amount of searched recipes (search history) is higher than the maxHistoryItems allowed, it will remove all recipes over the limit. 
    private fun validateAllData(allData: ArrayList<SearchHistoryItems>): ArrayList<SearchHistoryItems> {
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
        cursor.close()
        db.close()
        return allData
    }

    // Returns the user back to the Main menu 
    fun returnToMainMenu(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}