package org.example.app.ui

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

// PUBLIC_INTERFACE
/**
 * Utilities for persisting and applying theme (light/dark) selection.
 */
object ThemeUtils {
    private const val PREFS = "ui_prefs"
    private const val KEY_MODE = "night_mode" // values: "light" or "dark"

    /** PUBLIC_INTERFACE Apply saved theme at app startup. */
    fun applySavedTheme(context: Context) {
        when (getSavedTheme(context)) {
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    /** PUBLIC_INTERFACE Toggle and persist the theme. Returns new mode string. */
    fun toggleTheme(context: Context): String {
        val current = getSavedTheme(context)
        val next = if (current == "dark") "light" else "dark"
        saveTheme(context, next)
        applySavedTheme(context)
        return next
    }

    /** PUBLIC_INTERFACE Read saved theme value. */
    fun getSavedTheme(context: Context): String {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_MODE, "light") ?: "light"
    }

    private fun saveTheme(context: Context, mode: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_MODE, mode)
            .apply()
    }
}
