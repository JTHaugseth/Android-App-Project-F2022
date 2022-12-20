package com.example.eksamenandroid

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inserts default settings
        insertDefaultSettings()

        val rv = findViewById<RecyclerView>(R.id.MainRV)
        val searchButton = findViewById<Button>(R.id.SearchButton)

        // Retrieves recipes based on time of day when the user opens the app
        GlobalScope.launch(Dispatchers.Main){
            val timeOfDay = "https://api.edamam.com/api/recipes/v2?app_key=89289943ee654421a0a4925ef267f71f&app_id=fd84bb48&type=public&mealType=${getRecipesTimeOfDay()}"
            val searchQuery = ""
            val allData = getRecipes(timeOfDay, searchQuery)
            previousAllData = allData

            // Checks if user has scrolled to the bottom of Recycler view
            checkUserScroll()

            rv.adapter = RecipeAdapter(this@MainActivity, allData)
        }

        // Retrieves recipes based on user-search-input
        searchButton.setOnClickListener {
            val timeOfDay = "https://api.edamam.com/api/recipes/v2?app_key=89289943ee654421a0a4925ef267f71f&app_id=fd84bb48&type=public&mealType=${getRecipesTimeOfDay()}"
            val searchQuery = filterRecipesAndSave()
            GlobalScope.launch(Dispatchers.Main){
                val allData = getRecipes(timeOfDay, searchQuery)
                previousAllData = allData

                // Checks if user has scrolled to the bottom of Recycler view
                checkUserScroll()

                rv.adapter = RecipeAdapter(this@MainActivity, allData)
            }
        }
    }

    // Variables to store next page url and previous all data array. Used in checkUserScroll() and getRecipes()
    private lateinit var previousAllData: ArrayList<RecipeItems>
    private var nextPageURL: String? = null

    // Inserts default settings to app when it is launched for the first time
    private fun insertDefaultSettings() {
            val recipesDB = RecipesDB(this)
            val db = recipesDB.writableDatabase
            val cursor = db.rawQuery("SELECT COUNT(*) FROM Settings", null)

            if(cursor.moveToFirst()) {
                val count = cursor.getInt(0)
                if(count == 0) {
                    val values = ContentValues()
                    values.put("id", 1)
                    values.put("calories", 2000)
                    values.put("history_items", 10)
                    values.put("diet", "Not specified")
                    values.put("cuisine", "All")
                    values.put("mealtype", "All")
                    db.insert("Settings", null, values)
                }
            }
            cursor.close()
            db.close()
        }

    // Returns a string based on what time of the day it is. Used in getRecipes()
    private fun getRecipesTimeOfDay(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return if (hour in 6..10) {
            "Breakfast"
        } else if (hour in 11..13) {
            "Lunch"
        } else if (hour in 14..20) {
            "Dinner"
        } else if (hour in 21..5) {
            "Snack"
        } else {
            "Teatime"
        }
    }

    // Returns a url that has been filtered to the user-settings, and saves user-search-input to the Search History activity.
    private fun filterRecipesAndSave(): String{
        val searchInput = findViewById<EditText>(R.id.InputText)
        val searchQueryString = searchInput.text.toString()
        var updatedSearchString = searchQueryString
        if(searchQueryString.contains(' ')){
            updatedSearchString = searchQueryString.replace(' ', '-')
        }
        var url = "https://api.edamam.com/api/recipes/v2?app_key=89289943ee654421a0a4925ef267f71f&app_id=fd84bb48&type=public&q=$updatedSearchString"
        val getSettings = getSettingsFromDB()

        val dietString = getSettings.diet.toString()
        val cuisineString = getSettings.cuisine.toString()
        val mealtypeString = getSettings.mealtype.toString()

        val diet = getSettings.diet
        val cuisine = getSettings.cuisine
        val mealtype = getSettings.mealtype

        if(mealtypeString!="All"){
            url = "$url&mealType=$mealtype"
        }
        if(dietString != "Not specified"){
            url = "$url&Diet=$diet"
        }
        if(cuisineString!="All"){
            url = "$url&cuisineType=$cuisine"
        }

        if (updatedSearchString.isNotEmpty()) {
            val recipesDB = RecipesDB(this)
            val db = recipesDB.writableDatabase
            val values = ContentValues()
            values.put("searchInput", updatedSearchString)
            values.put("searchUrl", url)
            db.insert("History", null, values)
            db.close()
        }

        return url
    }

    // This function has three ways of working ->
    // Each way is calling the API with a given url
    // 1. When the user starts the app, it will base the url on time of day.
    // 2. When the user searches something, it will base the url on the search.
    // 3. When the user goes to the bottom of the Recycler View, it will base the url on the next-page-url if it exists.

    suspend fun getRecipes(timeOfDay: String, searchQuery: String): ArrayList<RecipeItems>{
        val allData = ArrayList<RecipeItems>()
        var timeOfDayURL = timeOfDay
        var searchURL = searchQuery

        GlobalScope.async {
            try {
                val importedData: String

                if (searchURL.isNotEmpty()) {
                    importedData = URL(searchURL).readText().toString()
                } else {
                    importedData = URL(timeOfDayURL).readText().toString()
                }
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
                e.message?.let { Log.e("getRecipes MainAct", it) }
                allData.clear()
            }
        }.await()
        return allData
    }

    // Reference to Report: Page 2-3 Code Structure
    // This class can be used to access different values from the settings. Used in filterRecipesAndSave() and getSettingsFromDB()
    data class MySettings(val id: Int, val calories: String, val history_items: String, val diet: String, val cuisine: String, val mealtype: String)

    // Reads the settings from database and populates MySettings class.
    private fun getSettingsFromDB(): MySettings {
        val recipesDB = RecipesDB(this)
        val db = recipesDB.readableDatabase
        val cursor = db.query("Settings", null, null, null, null, null, null, null)

        cursor.moveToFirst()
        val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        val calories = cursor.getString(cursor.getColumnIndexOrThrow("calories"))
        val history_items = cursor.getString(cursor.getColumnIndexOrThrow("history_items"))
        val diet = cursor.getString(cursor.getColumnIndexOrThrow("diet"))
        val cuisine = cursor.getString(cursor.getColumnIndexOrThrow("cuisine"))
        val mealtype = cursor.getString(cursor.getColumnIndexOrThrow("mealtype"))

        cursor.close()
        db.close()

        return MySettings(id, calories, history_items, diet, cuisine, mealtype)
    }

    // If the user reaches the bottom of the Recycler view, it re-runs getRecipes() with the next page url.
    // It will store the old data, add the new data, and recreate the recycler view with the combined data.
    // Finally it repositions the user to the position they were in before triggering the re-render.
    private suspend fun checkUserScroll() {
        val recyclerView = findViewById<RecyclerView>(R.id.MainRV)
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
                        }else {
                            val newAllData = getRecipes("", nextPageURL.toString())
                            previousAllData.addAll(newAllData)
                            recyclerView.adapter = RecipeAdapter(this@MainActivity, previousAllData)
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

    // Functions below is onClicks defined in activity_main elements. Sends the user to different activities.
    fun openSearchHistory(view: View) {
        val intent = Intent(this, SearchHistory::class.java)
        startActivity(intent)
    }
    fun openFavorites(view: View) {
        val intent = Intent(this, FavoritesActivity::class.java)
        startActivity(intent)
    }
    fun openTodaysMeals(view: View) {
        val intent = Intent(this, TodaysMeals::class.java)
        startActivity(intent)
    }
    fun openSettings(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }
}