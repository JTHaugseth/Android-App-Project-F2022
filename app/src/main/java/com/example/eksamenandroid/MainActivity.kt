package com.example.eksamenandroid

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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

        insertDefaultSettings()

        val rv = findViewById<RecyclerView>(R.id.MainRV)
        val searchButton = findViewById<Button>(R.id.SearchButton)

        GlobalScope.launch(Dispatchers.Main){
            val timeOfDay = "https://api.edamam.com/api/recipes/v2?app_key=89289943ee654421a0a4925ef267f71f&app_id=fd84bb48&type=public&mealType=${getRecipesTimeOfDay()}"
            val searchQuery = ""
            val allData = getRecipes(timeOfDay, searchQuery)

            rv.adapter = RecipeAdapter(this@MainActivity, allData)
        }

        searchButton.setOnClickListener {
            val timeOfDay = "https://api.edamam.com/api/recipes/v2?app_key=89289943ee654421a0a4925ef267f71f&app_id=fd84bb48&type=public&mealType=${getRecipesTimeOfDay()}"
            val searchQuery = filterRecipesAndSave()
            Log.i("testy", searchQuery)
            GlobalScope.launch(Dispatchers.Main){
                val allData = getRecipes(timeOfDay, searchQuery)
                rv.adapter = RecipeAdapter(this@MainActivity, allData)
            }
        }
    }

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

    fun getRecipesTimeOfDay(): String {
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

    fun filterRecipesAndSave(): String{
        val searchInput = findViewById<EditText>(R.id.InputText)
        val searchQueryString = searchInput.text.toString()
        var updatedSearchString = searchQueryString
        if(searchQueryString.contains(' ')){
            updatedSearchString = searchQueryString.replace(' ', '-')
        }
        Log.i("randomstuff2", updatedSearchString)
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

        val recipesDB = RecipesDB(this)
        val db = recipesDB.writableDatabase
        val values = ContentValues()
        values.put("searchInput", updatedSearchString)
        values.put("searchUrl", url)
        db.insert("History", null, values)
        db.close()
        
        return url
    }

    suspend fun getRecipes(timeOfDay: String, searchQuery: String): ArrayList<RecipeItems>{

        val allData = ArrayList<RecipeItems>()
        var timeOfDayURL = timeOfDay
        var searchURL = searchQuery

        GlobalScope.async {
            val importedData: String

            if (searchURL.isNotEmpty()) {
                importedData = URL(searchURL).readText().toString()
            } else {
                importedData = URL(timeOfDayURL).readText().toString()
            }
            Log.i("testing", importedData)
            val dataArray = (JSONObject(importedData).get("hits") as JSONArray)
            (0 until dataArray.length()).forEach{itemnr ->
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

    data class MySettings(val id: Int, val calories: String, val history_items: String, val diet: String, val cuisine: String, val mealtype: String)

    fun getSettingsFromDB(): MySettings {
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

    fun openSearchHistory(view: View) {
        val intent = Intent(this, SearchHistory::class.java)
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