package id.co.edtslib.edtsscreen.inappbanner.data.source.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.co.edtslib.data.source.local.LocalDataSource
import id.co.edtslib.edtsscreen.inappbanner.data.source.local.entity.InAppBannerEntity

class InAppBannerShownLocalData(sharedPreference: SharedPreferences) :
    LocalDataSource<List<String>?>(sharedPreference) {
    override fun getKeyName(): String = "InAppBannerShown"
    override fun getValue(json: String): List<String>? =
        Gson().fromJson(json, object : TypeToken<List<String>?>() {}.type)
}