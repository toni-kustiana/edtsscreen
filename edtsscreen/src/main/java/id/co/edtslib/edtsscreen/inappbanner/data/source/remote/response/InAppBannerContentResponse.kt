package id.co.edtslib.edtsscreen.inappbanner.data.source.remote.response

import com.google.gson.annotations.SerializedName
import id.co.edtslib.edtsscreen.inappbanner.domain.model.InAppBannerData

data class InAppBannerContentResponse(
    @field:SerializedName("content")
    val content: List<InAppBannerResponse>? = null
)