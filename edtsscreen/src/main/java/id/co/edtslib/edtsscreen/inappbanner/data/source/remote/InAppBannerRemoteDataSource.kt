package id.co.edtslib.edtsscreen.inappbanner.data.source.remote

import id.co.edtslib.data.BaseDataSource
import id.co.edtslib.edtsscreen.inappbanner.data.source.remote.network.InAppBannerApiService
import id.co.edtslib.edtsscreen.inappbanner.data.source.remote.request.InAppBannerRequest

class InAppBannerRemoteDataSource(
    private val service: InAppBannerApiService
) : BaseDataSource() {

    suspend fun get(path: String,
                    deviceId: String?,
                    client: String?,
                    platform: String?
    ) =
        getResult {
            service.get(
                path = path,
                request = InAppBannerRequest(
                    deviceID = deviceId,
                    client = client,
                    platform = platform
                ))
        }
}