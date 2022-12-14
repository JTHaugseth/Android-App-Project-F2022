package com.example.eksamenandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class SearchHistoryOnSelect : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_history_onselect)

        val url = intent.getStringExtra("URL")
        if (url != null) {
            Log.i("THIS IS THE CORRECT URL", url)
        }
    }
}