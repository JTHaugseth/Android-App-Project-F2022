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
        rv.adapter = SearchHistoryAdapter(this@SearchHistory, allData)
    }

    fun getSearchHistory(): ArrayList<String> {
        val historyItems = ArrayList<String>()
        val recipesDB = RecipesDB(this)
        val db = recipesDB.readableDatabase
        val cursor = db.query("History", null, null, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val searchInput = cursor.getString(cursor.getColumnIndexOrThrow("searchInput"))
            historyItems.add(searchInput)
        }
        return historyItems
    }

    fun returnToMainMenu(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}