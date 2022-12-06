package com.example.eksamenandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rv = findViewById<RecyclerView>(R.id.MainRV)

        GlobalScope.launch(Dispatchers.Main){
            val allData = getRecipes()

            rv.adapter = RecipeAdapter(allData)
        }
    }

    suspend fun getRecipes(): ArrayList<RecipeItems>{

        val allData = ArrayList<RecipeItems>()

        GlobalScope.async {
            val importedData = URL("https://api.edamam.com/api/recipes/v2?app_key=89289943ee654421a0a4925ef267f71f&app_id=fd84bb48&type=public&mealType=Breakfast").readText().toString()
            Log.i("testing", importedData)
            val dataArray = (JSONObject(importedData).get("hits") as JSONArray)
            (0 until dataArray.length()).forEach{itemnr ->
                val dataItem = RecipeItems()
                val assetItem = dataArray.get(itemnr)
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
}