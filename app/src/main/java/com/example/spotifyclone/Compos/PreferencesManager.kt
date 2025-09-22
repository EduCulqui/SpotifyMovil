package com.example.spotifyclone.Compos

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun setLoggedIn(uid: String) {
        prefs.edit().apply {
            putBoolean("is_logged_in", true)
            putString("uid", uid)
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean("is_logged_in", false)
    }

    fun getUid(): String? {
        return prefs.getString("uid", null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}