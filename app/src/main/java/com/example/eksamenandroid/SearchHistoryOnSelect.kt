package com.example.eksamenandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import kotlin.math.round

class SearchHistoryOnSelect : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_history_onselect)

        // Retrieves title and url from intent in SearchHistoryAdapter
        val title = intent.getStringExtra("TITLE")
        val searchHistoryTitle = findViewById<TextView>(R.id.SearchHistoryOnSelectTitle)
        searchHistoryTitle.text = title

        val rv = findViewById<RecyclerView>(R.id.SearchHistoryOnSelectRV)
        val url = intent.getStringExtra("URL")

        // Sends allData to RecipeAdapter to populate the Recycler View.
        GlobalScope.launch(Dispatchers.Main){
            val allData = getRecipes(url)
            previousAllData = allData

            // Checks if user has scrolled to the bottom of Recycler view
            checkUserScroll()
            
            rv.adapter = RecipeAdapter(this@SearchHistoryOnSelect, allData)
        }
    }

    // Variables to store next page url and previous all data array. Used in checkUserScroll() and getRecipes()
    private lateinit var previousAllData: ArrayList<RecipeItems>
    private var nextPageURL: String? = null


    // This function has two ways of working ->
    // Each way is calling the API with a given url
    // 1. When the user clicks on search in Search History, it will base the url on the earlier searched recipe, gotten from intent in SearchHistoryAdapter.
    // 2. When the user goes to the bottom of the Recycler View, it will base the url on the next-page-url if it exists.
    private suspend fun getRecipes(url: String?): ArrayList<RecipeItems> {
        val allData = ArrayList<RecipeItems>()

        GlobalScope.async {
            try {
                val importedData: String
                importedData = URL(url).readText().toString()
                if ((JSONObject(importedData).get("_links") as JSONObject).length() == 0) {
                    nextPageURL = ""
                } else {
                    nextPageURL =
                        (JSONObject(importedData).get("_links") as JSONObject).getJSONObject("next").getString("href").toString()
                }
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
                    } else {
                        dataItem.dietLabel = ""
                    }
                    if ((assetItem as JSONObject).getJSONObject("recipe").getJSONArray("healthLabels").length() != 0) {
                        dataItem.healthLabel = (assetItem as JSONObject).getJSONObject("recipe").getJSONArray("healthLabels").getString(0)
                    } else {
                        dataItem.healthLabel = ""
                    }
                    if ((assetItem as JSONObject).getJSONObject("recipe").getJSONArray("cautions").length() != 0) {
                        dataItem.cautions = (assetItem as JSONObject).getJSONObject("recipe").getJSONArray("cautions").getString(0)
                    } else {
                        dataItem.cautions = ""
                    }

                    allData.add(dataItem)
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("getRecipes SearchHist", it) }
                allData.clear()
            }
        }.await()
        return allData
    }

    // If the user reaches the bottom of the Recycler view, it re-runs getRecipes() with the next page url.
    // It will store the old data, add the new data, and recreate the recycler view with the combined data.
    // Finally it repositions the user to the position they were in before triggering the re-render.
    suspend fun checkUserScroll() {
        val recyclerView = findViewById<RecyclerView>(R.id.SearchHistoryOnSelectRV)
        var listenerTrigger = false

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()


                val totalItemCount = layoutManager.itemCount
                GlobalScope.launch(Dispatchers.Main) {
                    if (!listenerTrigger && lastVisibleItemPosition == totalItemCount - 1) {
                        listenerTrigger = true
                        if(nextPageURL == "") {
                            delay(2000)
                            listenerTrigger = false
                        }else{
                            val newAllData = getRecipes(nextPageURL.toString())
                            previousAllData.addAll(newAllData)
                            recyclerView.adapter = RecipeAdapter(this@SearchHistoryOnSelect, previousAllData)
                            recyclerView.scrollToPosition(previousAllData.size - 23)
                            delay(2000)
                            listenerTrigger = false
                        }
                    }
                }
            }
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }

    // Returns the user back to SearchHistory
    fun backToSearchHistory(view: View) {
        val intent = Intent(this, SearchHistory::class.java)
        startActivity(intent)
    }
}