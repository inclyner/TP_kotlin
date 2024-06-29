package pt.isec.amov2022202320191482112019129635

import android.content.Context

class MySharedPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    // Put methods for managing SharedPreferences here, such as:
    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue).toString()
    }

    fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }
}
