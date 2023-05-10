package dongdong.duan.chat.utility

import android.content.Context
import android.content.SharedPreferences
class PreferenceManager(context: Context?) {
    private val sharedPreferences: SharedPreferences =
        context?.getSharedPreferences("SharePrfeChat", Context.MODE_PRIVATE)!!

    fun edit(block: SharedPreferences.Editor.() -> Unit) {
        with(sharedPreferences.edit()) {
            block()
            apply()
        }
    }


    fun GetBoolean(key: String, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    fun PutBoolean(key: String, value: Boolean) {
        edit {
            putBoolean(key, value)
        }
    }

    fun GetString(key: String, default: String? = null): String? {
        return sharedPreferences.getString(key, default)
    }

    fun PutString(key: String, value: String) {
        edit {
            putString(key, value)
        }
    }

    fun clear() {
        edit {
            clear()
        }
    }
}
