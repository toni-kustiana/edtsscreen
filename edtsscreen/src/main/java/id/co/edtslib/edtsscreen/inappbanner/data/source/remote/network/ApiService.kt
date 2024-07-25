package id.co.edtslib.edtsscreen.inappbanner.data.source.remote.network

import id.co.edtslib.data.source.remote.response.ApiResponse
import id.co.edtslib.edtsscreen.inappbanner.data.source.remote.request.InAppBannerRequest
import id.co.edtslib.edtsscreen.inappbanner.data.source.remote.response.InAppBannerContentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface InAppBannerApiService {
    @POST("{path}")
    suspend fun get(@Path("path") path:String,
        @Body request: InAppBannerRequest
    ): Response<ApiResponse<InAppBannerContentResponse?>>

}