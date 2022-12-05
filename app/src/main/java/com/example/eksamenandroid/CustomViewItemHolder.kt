package com.example.eksamenandroid

import android.content.Context
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class CustomViewItemHolder: LinearLayout {

    var imageData: ImageView? = null
    var titleData: TextView? = null
    var dietLabelData: TextView? = null
    var healthLabelData: TextView? = null
    var cautionsData: TextView? = null

    constructor(context: Context): super(context) {
        imageData = ImageView(context)
        titleData = TextView(context)
        dietLabelData = TextView(context)
        healthLabelData = TextView(context)
        cautionsData = TextView(context)

        addView(imageData)
        addView(titleData)
        addView(dietLabelData)
        addView(healthLabelData)
        addView(cautionsData)
    }

    fun setTitle(title: String?) {
        titleData?.setText(title)
    }

    fun setDietLabel(dietLabel: String?) {
        dietLabelData?.setText(dietLabel)
    }

    fun setHealthLabel(healthLabel: String?) {
        healthLabelData?.setText(healthLabel)
    }

    fun setCaution(cautions: String?) {
        cautionsData?.setText(cautions)
    }



}