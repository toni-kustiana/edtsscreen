package id.co.edtslib.edtsscreen.inappbanner.ui

import id.co.edtslib.edtsscreen.inappbanner.domain.model.InAppBannerData

interface InAppBannerDelegate {
    fun onError(code: String?, message: String?, data: InAppBannerData?)
    fun close(data: InAppBannerData)
}