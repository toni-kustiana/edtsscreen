package id.co.edtslib.edtsscreen.inappbanner.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class InAppBannerResponse(
    @field:SerializedName("id")
    val id: String?,

    @field:SerializedName("event")
    val event: String?,

    @field:SerializedName("name")
    val name: String?,

    @field:SerializedName("image")
    val image: String?,

    @field:SerializedName("startDate")
    val startDate: String?,

    @field:SerializedName("endDate")
    val endDate:  String?,

    @field:SerializedName("duration")
    val duration: Int?,

    @field:SerializedName("record")
    val record: Int?,

    @field:SerializedName("deepLinkValue")
    val deepLinkValue: String?,

    @field:SerializedName("interval")
    val interval: Int?,

    @field:SerializedName("position")
    val position: String?,
)