package id.co.edtslib.edtsscreen.inappbanner.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import id.co.edtslib.data.ProcessResult
import id.co.edtslib.data.ProcessResultDelegate
import id.co.edtslib.data.Result
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
                             private val flowData: Flow<Result<InAppBannerData?>>?,
                             private val dismissible: Boolean = false): KoinComponent {
    private val inAppBannerUseCase: InAppBannerUseCase by inject()

    private var popup: Popup? = null
    private val binding = DialogInAppBannerBinding.inflate(LayoutInflater.from(fragmentActivity))

    var delegate: InAppBannerDelegate? = null

    private fun show() {
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
                            dialog = null
                            delegate?.onError(
                                code = code,
                                message = message,
                                data = data
                            )
                        }

                        override fun errorConnection() {
                            dialog = null
                            delegate?.onError(
                                code = "404",
                                message = null,
                                data = null
                            )
                        }

                        override fun errorSystem() {
                            dialog = null
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
                            else {
                                dialog = null
                            }
                        }

                        override fun unAuthorize(message: String?) {
                            dialog = null
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
    }

    @SuppressLint("CheckResult")
    fun showBanner(banner: InAppBannerData) {
        if (popup == null) {
            popup = Popup.showFullScreen(
                view = binding.root,
                dismissible = dismissible)
            popup?.setOnDismissListener {
                inAppBannerUseCase.show(banner)
                popup = null
                dialog = null
            }
        }

        binding.clDialog.setOnClickListener {
            if (dismissible) {
                close(banner)
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
            val lpShimmer = binding.shimmerFrameLayout.layoutParams as FrameLayout.LayoutParams
            lpShimmer.height = (binding.shimmerFrameLayout.width * 1.14).toInt()
            binding.shimmerFrameLayout.layoutParams = lpShimmer

            if (! fragmentActivity.isDestroyed) {
                try {
                    Glide.
                    with(fragmentActivity).
                    load(banner.image).
                    listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            close()
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.imageView.post {
                                if (resource != null) {
                                    val w = resource.intrinsicWidth
                                    val h = resource.intrinsicHeight

                                    val lp = binding.imageView.layoutParams as FrameLayout.LayoutParams
                                    lp.height = binding.imageView.width * h / w

                                    binding.imageView.setImageDrawable(resource)
                                    binding.shimmerFrameLayout.isVisible = false

                                    binding.imageView.post {
                                        if (binding.imageView.drawable is BitmapDrawable) {
                                            val rect = Rect()
                                            binding.ivClose.getLocalVisibleRect(rect)

                                            val bitmapDrawable = binding.imageView.drawable as BitmapDrawable
                                            val pixel = bitmapDrawable.bitmap.getPixel(rect.left, rect.top)

                                            val r = Color.red(pixel)
                                            val g = Color.green(pixel)
                                            val b = Color.blue(pixel)

                                            binding.ivClose.isActivated = r > 0xAA && g > 0xAA && b > 0xAA
                                        }
                                    }
                                }
                                else {
                                    close()
                                }
                            }

                            return true
                        }

                    }).submit()
                }
                catch (ignore: Exception) {

                }
            }
        }
    }

    fun close() {
        popup?.cancel()
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
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                fragmentActivity.startActivity(intent)
                popup?.cancel()
            }
            catch (e: Exception) {
                Toast.makeText(fragmentActivity, "Unknown Intent $url", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private var dialog: InAppBannerDialog? = null
        fun show(fragmentActivity: FragmentActivity,
                 flowData: Flow<Result<InAppBannerData?>>,
                 dismissible: Boolean = false) {
            if (dialog == null) {
                dialog = InAppBannerDialog(
                    fragmentActivity = fragmentActivity,
                    flowData = flowData,
                    dismissible = dismissible
                )
                dialog?.show()
            }
        }

        fun show(fragmentActivity: FragmentActivity, url: String?) {
            if (dialog == null) {
                dialog = InAppBannerDialog(
                    fragmentActivity = fragmentActivity,
                    flowData = null
                )
                dialog?.showBanner(InAppBannerData.create(url))
            }
        }
    }

}