package id.co.edtslib.edtsscreen.inappbanner.data.source.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.co.edtslib.data.source.local.LocalDataSource
import id.co.edtslib.edtsscreen.inappbanner.data.source.local.entity.InAppBannerShownEntity

class InAppBannerShownLocalData(sharedPreference: SharedPreferences) :
    LocalDataSource<List<InAppBannerShownEntity>?>(sharedPreference) {
    override fun getKeyName(): String = "InAppBannerLatestShownList"
    override fun getValue(json: String): List<InAppBannerShownEntity>? =
        Gson().fromJson(json, object : TypeToken<List<InAppBannerShownEntity>?>() {}.type)
}