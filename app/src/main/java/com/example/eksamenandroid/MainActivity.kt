package com.example.eksamenandroid

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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.MainRV)
        val searchInput = findViewById<EditText>(R.id.InputText)
        val searchButton = findViewById<Button>(R.id.SearchButton)

        GlobalScope.launch(Dispatchers.Main){
            val timeOfDay = "https://api.edamam.com/api/recipes/v2?app_key=89289943ee654421a0a4925ef267f71f&app_id=fd84bb48&type=public&mealType=${getRecipesTimeOfDay()}"
            val searchQuery = ""
            val allData = getRecipes(timeOfDay, searchQuery)

            rv.adapter = RecipeAdapter(allData)
        }

        searchButton.setOnClickListener {
            val timeOfDay = "https://api.edamam.com/api/recipes/v2?app_key=89289943ee654421a0a4925ef267f71f&app_id=fd84bb48&type=public&mealType=${getRecipesTimeOfDay()}"
            val searchQueryString = searchInput.text.toString()
            val searchQuery = "https://api.edamam.com/api/recipes/v2?app_key=89289943ee654421a0a4925ef267f71f&app_id=fd84bb48&type=public&q=$searchQueryString"

            GlobalScope.launch(Dispatchers.Main){
                val allData = getRecipes(timeOfDay, searchQuery)

                rv.adapter = RecipeAdapter(allData)
            }
            filterRecipes()
        }
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




    fun filterRecipes(){

        var url = "https://api.edamam.com/api/recipes/v2?app_key=89289943ee654421a0a4925ef267f71f&app_id=fd84bb48&type=public&"
        val getSettings = getSettingsFromDB()

        val diet = getSettings.diet
        val cuisine = getSettings.cuisine
        val mealtype = getSettings.mealtype

        Log.i("diet", diet)
        Log.i("cuisine", cuisine)
        Log.i("mealtype", mealtype)

        if(diet != "not specified"){
            url += "$url&dietLabels=$diet"
        }
        if(cuisine!="all"){
            url += "$url&Cuisine=$cuisine"
        }
        if(mealtype!="all"){
            url += "$url&mealtype=$cuisine"
        }
        Log.i("url", url)

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

        Log.i("yes", diet)

        cursor.close()
        db.close()

        return MySettings(id, calories, history_items, diet, cuisine, mealtype)
    }
}