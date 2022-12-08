package com.example.eksamenandroid

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.eksamenandroid.Database.Companion.COLUMN_CALORIES
import com.example.eksamenandroid.Database.Companion.COLUMN_DATA
import com.example.eksamenandroid.Database.Companion.COLUMN_HISTORY_ITEMS
import com.example.eksamenandroid.Database.Companion.COLUMN_ID
import com.example.eksamenandroid.Database.Companion.TABLE_SETTINGS


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        getAndLog()

    }

    /*val caloriesPerDayView = findViewById<EditText>(R.id.CaloriesInput)
    val maxHistoryItemsView = findViewById<EditText>(R.id.MaxHistoryItemsInput)
    val desiredDietView = findViewById<Spinner>(R.id.DietDropDown)
    val cuisineTypeView = findViewById<Spinner>(R.id.CuisineDropDown)
    val mealTypeView = findViewById<Spinner>(R.id.MealTypeDropDown)*/


    val database = Database(this)



    fun put() {
        val values = ContentValues()
        values.put(COLUMN_ID, 1)
        values.put(COLUMN_DATA, "heihei")
        values.put(COLUMN_CALORIES, 1000)
        values.put(COLUMN_HISTORY_ITEMS, 10)
        // Use the insert() method to insert the new row into the Settings table
        val db = database.writableDatabase
        db.insert(TABLE_SETTINGS, null, values)
        getAndLog()
    }

    fun getAndLog() {
        val db = database.readableDatabase
        // Define a projection that specifies the columns to be retrieved
        val projection = arrayOf(COLUMN_ID, COLUMN_DATA, COLUMN_CALORIES, COLUMN_HISTORY_ITEMS)
        // Define a selection and selectionArgs that specify the rows to be retrieved
        val selection = "$COLUMN_ID = ?"
        val selectionArgs = arrayOf("1")
        // Use the query() method to retrieve the rows from the Settings table
        val cursor = db.query(
            TABLE_SETTINGS, projection, selection, selectionArgs, null, null, null
        )
        // Iterate over the rows in the cursor and log the values of the columns
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val data = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATA))
            val calories = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CALORIES))
            val historyItems = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HISTORY_ITEMS))
            Log.i("Settings", "ID: $id, Data: $data, Calories: $calories, History items: $historyItems")
        }
    }






    fun saveAndReturn(view: View) {



        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}