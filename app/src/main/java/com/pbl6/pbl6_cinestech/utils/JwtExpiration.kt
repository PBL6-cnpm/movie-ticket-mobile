package com.pbl6.pbl6_cinestech.utils

import android.os.Build
import androidx.annotation.RequiresApi
import org.json.JSONObject
import java.util.Base64

object JwtExpiration {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getJwtExpiration(token: String): Long? {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return null
            val payload = String(Base64.getUrlDecoder().decode(parts[1]))
            val json = JSONObject(payload)
            json.optLong("exp", -1L)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isTokenExpired(token: String): Boolean {
        val exp = getJwtExpiration(token) ?: return true
        val now = System.currentTimeMillis() / 1000
        return exp -now < 300
    }
}