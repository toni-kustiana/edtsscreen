package id.co.edtslib.edtsscreen.inappbanner.data.source.local.entity

data class InAppBannerEntity(
    val id: String?,
    val event: String?,
    val name: String?,
    val image: String?,
    val startDate: String?,
    val endDate:  String?,
    val duration: Int?,
    val record: Int?,
    val deepLinkValue: String?,
    val interval: Int?,
    val position: String?
)