package id.co.edtslib.edtsscreen.inappbanner.data.source.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.co.edtslib.data.source.local.LocalDataSource
import id.co.edtslib.edtsscreen.inappbanner.data.source.local.entity.InAppBannerEntity

class InAppBannerListLocalData(sharedPreference: SharedPreferences) :
    LocalDataSource<List<InAppBannerEntity>?>(sharedPreference) {
    override fun getKeyName(): String = "InAppBannerList"
    override fun getValue(json: String): List<InAppBannerEntity>? =
        Gson().fromJson(json, object : TypeToken<List<InAppBannerEntity>?>() {}.type)
}