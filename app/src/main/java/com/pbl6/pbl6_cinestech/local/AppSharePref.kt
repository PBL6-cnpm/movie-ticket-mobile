package com.pbl6.pbl6_cinestech.local

import android.content.Context
import androidx.core.content.edit

class AppSharePref(private val context: Context) {
    companion object {
        private const val PREF_LOCATION = "location"
    }

    private val sharePref by lazy {
        context.getSharedPreferences("TrackingSharePref", Context.MODE_PRIVATE)
    }

    var location: String
        get() = sharePref.getString(PREF_LOCATION, "Da Nang") ?: ""
        set(value) {
            sharePref.edit { putString(PREF_LOCATION, value) }
        }
}