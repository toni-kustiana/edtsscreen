package id.co.edtslib.edtsscreen.inappbanner.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import id.co.edtslib.data.ProcessResult
import id.co.edtslib.data.ProcessResultDelegate
import id.co.edtslib.data.Result
import id.co.edtslib.edtsds.R
import id.co.edtslib.edtsds.popup.Popup
import id.co.edtslib.edtsscreen.databinding.DialogInAppBannerBinding
import id.co.edtslib.edtsscreen.inappbanner.domain.model.InAppBannerData
import id.co.edtslib.edtsscreen.inappbanner.domain.usecase.InAppBannerUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class InAppBannerDialog(private val fragmentActivity: FragmentActivity,
                             private val flowData: Flow<Result<InAppBannerData?>>?): KoinComponent {
    private val inAppBannerUseCase: InAppBannerUseCase by inject()

    private var popup: Popup? = null
    private val binding = DialogInAppBannerBinding.inflate(LayoutInflater.from(fragmentActivity))

    var delegate: InAppBannerDelegate? = null

    private fun show(): InAppBannerDialog {
        fragmentActivity.lifecycleScope.launch {
            flowData?.collectLatest {
                ProcessResult(
                    result = it,
                    delegate = object : ProcessResultDelegate<InAppBannerData?> {
                        override fun error(
                            code: String?,
                            message: String?,
                            data: InAppBannerData?
                        ) {
                            delegate?.onError(
                                code = code,
                                message = message,
                                data = data
                            )
                        }

                        override fun errorConnection() {
                            delegate?.onError(
                                code = "404",
                                message = null,
                                data = null
                            )
                        }

                        override fun errorSystem() {
                            delegate?.onError(
                                code = "503",
                                message = null,
                                data = null
                            )
                        }

                        override fun loading() {

                        }

                        override fun success(data: InAppBannerData?) {
                            if (data?.image?.isNotEmpty() == true) {
                                showBanner(data)
                            }
                        }

                        override fun unAuthorize(message: String?) {
                            delegate?.onError(
                                code = "401",
                                message = null,
                                data = null
                            )
                        }
                    }

                    )
            }
        }

        return this
    }

    @SuppressLint("CheckResult")
    fun showBanner(banner: InAppBannerData) {
        if (popup == null) {
            popup = Popup.showFullScreen(
                view = binding.root)
            popup?.setOnDismissListener {
                inAppBannerUseCase.show(banner)
                popup = null
            }
        }

        binding.ivClose.setOnClickListener {
            close(banner)
        }

        binding.imageView.setOnClickListener {
            click(banner)
        }

        popup?.show()

        binding.root.post {
            Glide.
                with(fragmentActivity).
                load(banner.image).
                placeholder(R.drawable.ic_broken_product).
                error(R.drawable.ic_broken_product).
                into(binding.imageView)
        }
    }

    protected open fun close(banner: InAppBannerData) {
        delegate?.close(banner)
        popup?.cancel()
    }

    protected open fun click(banner: InAppBannerData?) {
        if (banner?.deepLinkValue?.isNotEmpty() == true) {
            val uri = Uri.parse(banner.deepLinkValue)
            val url = if (uri.scheme?.isNotEmpty() == true) {
                banner.deepLinkValue
            }
            else {
                "https://${banner.deepLinkValue}"
            }
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            fragmentActivity.startActivity(intent)
            popup?.cancel()
        }
    }

    companion object {
        fun show(fragmentActivity: FragmentActivity,
                 flowData: Flow<Result<InAppBannerData?>>) =
            InAppBannerDialog(
                fragmentActivity = fragmentActivity,
                flowData = flowData).show()

        fun show(fragmentActivity: FragmentActivity, url: String?) =
            InAppBannerDialog(
                fragmentActivity = fragmentActivity,
                flowData = null).showBanner(InAppBannerData.create(url))
    }

}