package id.co.edtslib.edtsscreen.inappbanner.data.source.local

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.co.edtslib.data.source.local.LocalDataSource
import id.co.edtslib.edtsscreen.inappbanner.data.source.local.entity.InAppBannerEntity
import id.co.edtslib.edtsscreen.inappbanner.data.source.local.entity.InAppBannerShown

class InAppBannerShownLocalData(sharedPreference: SharedPreferences) :
    LocalDataSource<InAppBannerShown?>(sharedPreference) {
    override fun getKeyName(): String = "InAppBannerLatestShown"
    override fun getValue(json: String): InAppBannerShown? =
        Gson().fromJson(json, object : TypeToken<InAppBannerShown?>() {}.type)
}