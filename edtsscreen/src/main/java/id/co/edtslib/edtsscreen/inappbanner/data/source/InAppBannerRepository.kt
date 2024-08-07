package id.co.edtslib.edtsscreen.inappbanner.data.source

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import id.co.edtslib.data.NetworkBoundGetResource
import id.co.edtslib.data.Result
import id.co.edtslib.data.source.local.HttpHeaderLocalSource
import id.co.edtslib.data.source.remote.SessionRemoteDataSource
import id.co.edtslib.data.source.remote.response.ApiResponse
import id.co.edtslib.edtsscreen.inappbanner.data.mapper.InAppBannerMapper
import id.co.edtslib.edtsscreen.inappbanner.data.source.local.InAppBannerListLocalData
import id.co.edtslib.edtsscreen.inappbanner.data.source.local.InAppBannerShownLocalData
import id.co.edtslib.edtsscreen.inappbanner.data.source.local.entity.InAppBannerShown
import id.co.edtslib.edtsscreen.inappbanner.data.source.remote.InAppBannerRemoteDataSource
import id.co.edtslib.edtsscreen.inappbanner.data.source.remote.response.InAppBannerContentResponse
import id.co.edtslib.edtsscreen.inappbanner.domain.model.InAppBannerData
import id.co.edtslib.edtsscreen.inappbanner.domain.repository.IInAppBannerRepository
import id.co.edtslib.util.DateTimeUtil
import kotlinx.coroutines.flow.flow
import org.mapstruct.factory.Mappers
import java.text.ParseException
import java.util.Date

class InAppBannerRepository(private val localDataSource: HttpHeaderLocalSource,
                            private val sessionRemoteDataSource: SessionRemoteDataSource,
                            private val inAppBannerRemote: InAppBannerRemoteDataSource,
                            private val inAppBannerListLocal: InAppBannerListLocalData,
                            private val inAppBannerShownLocal: InAppBannerShownLocalData,
                            private val context: Context):
    IInAppBannerRepository {

    override fun get(
        path: String,
        client: String?,
        platform: String?
    ) =
        object : NetworkBoundGetResource<InAppBannerData?, ApiResponse<InAppBannerContentResponse?>>(
            localDataSource, sessionRemoteDataSource
        ) {
            @SuppressLint("HardwareIds")
            override suspend fun createCall(): Result<ApiResponse<InAppBannerContentResponse?>> {
                val deviceID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

                return inAppBannerRemote.get(
                    path = path,
                    client = client,
                    platform = platform,
                    deviceId = deviceID)
            }

            override fun getCached() = flow {
                val data = Mappers.getMapper(InAppBannerMapper::class.java)
                    .inAppBannerEntityToModel(inAppBannerListLocal.getCached())

                val bannerList = data?.filter {
                    val isExist = it.startDate?.isNotEmpty() == true && it.endDate?.isNotEmpty() == true
                    if (isExist) {
                        try {
                            val startDate = DateTimeUtil.getUTCDate(it.startDate!!)
                            val endDate = DateTimeUtil.getUTCDate(it.endDate!!)
                            if (startDate != null && endDate != null) {
                                val time = Date().time

                                time >= startDate.time && time <= endDate.time
                            }
                            else {
                                false
                            }

                        }
                        catch (e: ParseException) {
                            false
                        }
                    }
                    else {
                        true
                    }
                }?.sortedBy { it.endDate }

                val banner = if (bannerList?.isNotEmpty() == true) bannerList[0] else null
                if (banner == null) {
                    emit(null)
                }
                else {
                    val latestShown = inAppBannerShownLocal.getCached()
                    if (latestShown == null || latestShown.id != banner.id) {
                        emit(banner)
                    }
                    else {
                        val now = Date().time
                        val nextShow = latestShown.time + (banner.interval ?: 0)*60*1000

                        if (now >= nextShow) {
                            emit(banner)
                        }
                        else (
                            emit(null)
                        )
                    }
                }


            }

            override fun shouldFetch(data: InAppBannerData?) = true

            override suspend fun saveCallResult(data: ApiResponse<InAppBannerContentResponse?>) {
                inAppBannerListLocal.save(Mappers.getMapper(InAppBannerMapper::class.java)
                    .inAppBannerResponseToEntity(data.data?.content))
            }

        }.asFlow()

    override fun show(banner: InAppBannerData) {
        if (banner.id != null) {
            inAppBannerShownLocal.save(InAppBannerShown(
                id = banner.id,
                time = Date().time
            ))
        }
    }
}