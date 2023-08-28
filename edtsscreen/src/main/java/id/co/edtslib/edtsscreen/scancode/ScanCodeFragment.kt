package id.co.edtslib.edtsscreen.scancode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import id.co.edtslib.edtsds.R
import id.co.edtslib.edtsscreen.databinding.EdtsScreenFragmentScanCodeBinding
import id.co.edtslib.edtsscreen.utils.PermissionUtils
import id.co.edtslib.edtsscreen.utils.PermissionUtils.showRationaleDialog
import id.co.edtslib.uibase.BaseFragment

open class ScanCodeFragment: BaseFragment<EdtsScreenFragmentScanCodeBinding>() {
    lateinit var codeScanner: CodeScanner

    var title: String? = null
        set(value) {
            field = value

            var bundle = arguments
            if (bundle == null) {
               bundle = Bundle()
            }
            bundle.putString("title", value)
            arguments = bundle

            doSetTitle(value)
        }

    var helper: String? = null
        set(value) {
            field = value

            var bundle = arguments
            if (bundle == null) {
                bundle = Bundle()
            }
            bundle.putString("helper", value)
            arguments = bundle

            doHelper(value)
        }

    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var permission = false
            permissions.entries.forEach {
                permission = it.value
            }
            if (permission) {
                codeScanner.startPreview()
            } else {
                showRationaleDialog()
            }
        }

    var delegate: ScanCodeDelegate? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> EdtsScreenFragmentScanCodeBinding
        get() = EdtsScreenFragmentScanCodeBinding::inflate

    override fun getTrackerPageName(): String?  = null

    override fun setup() {
        codeScanner = CodeScanner(requireContext(), binding.scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            binding.scannerView.post {
                delegate?.onScanned(it.toString())
            }
            stop()
        }

        binding.ivBack.setOnClickListener {
            delegate?.onBack()
        }

        binding.scannerView.isAutoFocusButtonVisible = false

        doSetTitle(arguments?.getString("title"))
        binding.scannerView.postDelayed({
            doHelper(arguments?.getString("helper"))
        }, 100)
    }

    fun start() {
        PermissionUtils.askCameraPermission(cameraPermissionResult)
    }

    fun stop() {
        codeScanner.releaseResources()
    }

    override fun onStart() {
        super.onStart()
        start()
    }

    override fun onStop() {
        stop()
        super.onStop()
    }

    private fun doSetTitle(title: String?) {
        if (isAdded && isNotNullBinding()) {
            binding.llToolbar.isVisible = title?.isNotEmpty() == true
            binding.tvTitle.text = title
        }
    }

    private fun doHelper(helper: String?) {
        if (isAdded && isNotNullBinding()) {
            val layoutParams = binding.tvHelper.layoutParams as FrameLayout.LayoutParams
            layoutParams.topMargin = binding.clContent.height / 2 +
                    (binding.scannerView.frameSize * binding.clContent.width).toInt() / 2 +
                    resources.getDimensionPixelSize(R.dimen.dimen_24dp)

            binding.tvHelper.text = helper
        }
    }

}