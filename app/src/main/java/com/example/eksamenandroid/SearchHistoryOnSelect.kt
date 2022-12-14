package com.example.eksamenandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import kotlin.math.round

class SearchHistoryOnSelect : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_history_onselect)

        val rv = findViewById<RecyclerView>(R.id.SearchHistoryOnSelectRV)
        val url = intent.getStringExtra("URL")

        GlobalScope.launch(Dispatchers.Main){
            val allData = getRecipes(url)
            rv.adapter = RecipeAdapter(this@SearchHistoryOnSelect, allData)
        }
    }

    private suspend fun getRecipes(url: String?): ArrayList<RecipeItems> {
        val allData = ArrayList<RecipeItems>()

        GlobalScope.async {
            val importedData: String
            importedData = URL(url).readText().toString()
            val dataArray = (JSONObject(importedData).get("hits") as JSONArray)
            (0 until dataArray.length()).forEach { itemnr ->
                val dataItem = RecipeItems()
                val assetItem = dataArray.get(itemnr)

                dataItem.image = (assetItem as JSONObject).getJSONObject("recipe").getString("image")
                dataItem.title = (assetItem as JSONObject).getJSONObject("recipe").getString("label")
                val yieldFloat = (assetItem as JSONObject).getJSONObject("recipe").getInt("yield")
                val caloriesFloat = (assetItem as JSONObject).getJSONObject("recipe").getInt("calories")
                dataItem.calories = round(caloriesFloat.toDouble()).toInt() / round(yieldFloat.toDouble()).toInt()
                dataItem.url = (assetItem as JSONObject).getJSONObject("recipe").getString("url")

                if ((assetItem as JSONObject).getJSONObject("recipe").getJSONArray("dietLabels").length() != 0) {
                    dataItem.dietLabel = (assetItem as JSONObject).getJSONObject("recipe").getJSONArray("dietLabels").getString(0)
                } else {dataItem.dietLabel = ""}
                if ((assetItem as JSONObject).getJSONObject("recipe").getJSONArray("healthLabels").length() != 0) {
                    dataItem.healthLabel = (assetItem as JSONObject).getJSONObject("recipe").getJSONArray("healthLabels").getString(0)
                } else {dataItem.healthLabel = ""}
                if ((assetItem as JSONObject).getJSONObject("recipe").getJSONArray("cautions").length() != 0) {
                    dataItem.cautions = (assetItem as JSONObject).getJSONObject("recipe").getJSONArray("cautions").getString(0)
                } else {dataItem.cautions = ""}

                allData.add(dataItem)
            }
        }.await()
        return allData
    }

    fun backToSearchHistory(view: View) {
        val intent = Intent(this, SearchHistory::class.java)
        startActivity(intent)
    }
}