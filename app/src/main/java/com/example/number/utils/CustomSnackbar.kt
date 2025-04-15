package com.example.number.utils

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.number.R
import com.google.android.material.snackbar.Snackbar

object CustomSnackbar {

    fun show(activity: Activity, message: String, iconResId: Int = R.drawable.voda_logo) {
        val snackbar = Snackbar.make(
            activity.findViewById(android.R.id.content),
            "", Snackbar.LENGTH_SHORT
        )

        val snackbarLayout = snackbar.view as ViewGroup

        // ✅ 기본 배경 제거
        snackbar.view.setBackgroundColor(Color.TRANSPARENT)

        snackbarLayout.setPadding(0, 0, 0, 0)
        snackbarLayout.removeAllViews()

        val customView = LayoutInflater.from(activity).inflate(R.layout.custom_snackbar, null)

        val text = customView.findViewById<TextView>(R.id.snackbar_text)
        val icon = customView.findViewById<ImageView>(R.id.snackbar_icon)
        text.text = message
        icon.setImageResource(iconResId)

        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
        layoutParams.bottomMargin = 100

        snackbarLayout.addView(customView, layoutParams)
        snackbar.show()
    }
}
