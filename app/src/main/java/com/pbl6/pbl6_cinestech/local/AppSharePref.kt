package com.pbl6.pbl6_cinestech.local

import android.content.Context
import androidx.core.content.edit

class AppSharePref(private val context: Context) {
    companion object {
        private const val PREF_LOCATION = "location"
        private const val PREF_FAVOURITE = "favourite"
    }

    private val sharePref by lazy {
        context.getSharedPreferences("TrackingSharePref", Context.MODE_PRIVATE)
    }

    var listFavourite: MutableList<String>
        get() {
            val saved = sharePref.getString(PREF_FAVOURITE, "") ?: ""
            return if (saved.isEmpty()) {
                mutableListOf()
            } else {
                saved.split(",").toMutableList()
            }
        }
        set(value) {
            val join = value.joinToString(",")
            sharePref.edit { putString(PREF_FAVOURITE, join) }
        }

    var location: String
        get() = sharePref.getString(PREF_LOCATION, "Da Nang") ?: ""
        set(value) {
            sharePref.edit { putString(PREF_LOCATION, value) }
        }
}