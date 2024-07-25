package id.co.edtslib.edtsscreen.inappbanner.di

import id.co.edtslib.edtsscreen.inappbanner.data.source.InAppBannerRepository
import id.co.edtslib.edtsscreen.inappbanner.data.source.local.InAppBannerListLocalData
import id.co.edtslib.edtsscreen.inappbanner.data.source.local.InAppBannerShownLocalData
import id.co.edtslib.edtsscreen.inappbanner.data.source.remote.InAppBannerRemoteDataSource
import id.co.edtslib.edtsscreen.inappbanner.data.source.remote.network.InAppBannerApiService
import id.co.edtslib.edtsscreen.inappbanner.domain.repository.IInAppBannerRepository
import id.co.edtslib.edtsscreen.inappbanner.domain.usecase.InAppBannerInteractor
import id.co.edtslib.edtsscreen.inappbanner.domain.usecase.InAppBannerUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val inAppBannerModule = module {
    single { provideInAppBannerApiService(get(named("api"))) }

    single { InAppBannerListLocalData(get()) }
    single { InAppBannerShownLocalData(get()) }
    single { InAppBannerRemoteDataSource(get()) }

    single<IInAppBannerRepository> {
        InAppBannerRepository(
            get(),
            get(),
            get(),
            get(),
            get(),
            androidContext()
        )
    }

    factory<InAppBannerUseCase> { InAppBannerInteractor(get()) }
}

private fun provideInAppBannerApiService(retrofit: Retrofit) =
    retrofit.create(InAppBannerApiService::class.java)