package id.co.edtslib.edtsscreen.inappbanner.domain.usecase

import id.co.edtslib.edtsscreen.inappbanner.domain.model.InAppBannerData
import id.co.edtslib.edtsscreen.inappbanner.domain.repository.IInAppBannerRepository

class InAppBannerInteractor(private val repository: IInAppBannerRepository) :
    InAppBannerUseCase {
    override fun get(path: String, client: String?, platform: String?) =
        repository.get(path, client, platform)

    override fun show(banner: InAppBannerData) =
        repository.show(banner)
}