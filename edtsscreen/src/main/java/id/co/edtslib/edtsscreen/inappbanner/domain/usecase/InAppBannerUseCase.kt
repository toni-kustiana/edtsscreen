package id.co.edtslib.edtsscreen.inappbanner.domain.usecase

import id.co.edtslib.data.Result
import id.co.edtslib.edtsscreen.inappbanner.domain.model.InAppBannerData
import kotlinx.coroutines.flow.Flow

interface InAppBannerUseCase {
    fun get(path: String, client: String?, platform: String?): Flow<Result<InAppBannerData?>>
    fun show(banner: InAppBannerData)
}