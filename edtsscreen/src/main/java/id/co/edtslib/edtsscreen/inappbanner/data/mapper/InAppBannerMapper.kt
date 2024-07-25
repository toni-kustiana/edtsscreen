package id.co.edtslib.edtsscreen.inappbanner.data.mapper

import id.co.edtslib.edtsscreen.inappbanner.data.source.local.entity.InAppBannerEntity
import id.co.edtslib.edtsscreen.inappbanner.data.source.remote.response.InAppBannerContentResponse
import id.co.edtslib.edtsscreen.inappbanner.data.source.remote.response.InAppBannerResponse
import id.co.edtslib.edtsscreen.inappbanner.domain.model.InAppBannerData
import org.mapstruct.Mapper
import org.mapstruct.Mappings

@Mapper
interface InAppBannerMapper {

    @Mappings
    fun inAppBannerResponseToEntity(input: List<InAppBannerResponse>?): List<InAppBannerEntity>?

    @Mappings
    fun inAppBannerEntityToModel(input: List<InAppBannerEntity>?): List<InAppBannerData>?

}