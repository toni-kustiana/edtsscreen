package id.co.edtslib.edtsscreen.inappbanner.data.source.remote.request

import com.google.gson.annotations.SerializedName

data class InAppBannerRequest(
    @field:SerializedName("deviceID")
    val deviceID: String? = null,
    @field:SerializedName("client")
    val client: String? = null,
    @field:SerializedName("platform")
    val platform: String? = null,
)