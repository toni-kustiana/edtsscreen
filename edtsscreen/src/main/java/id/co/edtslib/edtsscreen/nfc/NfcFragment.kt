package id.co.edtslib.edtsscreen.nfc

import android.content.Intent
import android.nfc.NdefMessage
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import id.co.edtslib.edtsds.popup.Popup
import id.co.edtslib.edtsscreen.databinding.EdtsScreenFragmentNfcBinding
import id.co.edtslib.edtsscreen.nfc.parser.NdefMessageParser
import id.co.edtslib.uibase.BaseFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class NfcFragment : BaseFragment<EdtsScreenFragmentNfcBinding>() {

    lateinit var nfcManager: NfcManager
    var delegate: NfcDelegate? = null
    var keepTrayAfterScan = false

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> EdtsScreenFragmentNfcBinding
        get() = EdtsScreenFragmentNfcBinding::inflate

    override fun getTrackerPageName(): String? = null

    override fun setup() {
        binding.bottomLayout.titleDivider = false

        val intent = Intent(requireActivity(), requireActivity()::class.java)
        nfcManager = NfcManager(requireActivity(), intent)
        nfcManager.checkNfcFeature {
            binding.root.isVisible = true
        }
        nfcManager.delegate = object : NfcManager.NfcManagerDelegate {
            override fun onRead(messages: Array<NdefMessage?>) {
                // This prevents crashes if the fragment is destroyed during the delay
                lifecycleScope.launch {
                    binding.root.isVisible = true

                    delay(200L) // Suspend instead of blocking/callback

                    if (!isAdded) return@launch

                    binding.root.isVisible = keepTrayAfterScan

                    val allRecords = messages
                        .filterNotNull()
                        .flatMap { NdefMessageParser.parse(it) }

                    delegate?.onNfcReceived(allRecords)
                }
            }

            override fun onCommandReceived(txBytes: ByteArray, rxBytes: ByteArray) {
                delegate?.onNfcReceived(txBytes, rxBytes)
            }

            override fun openSetting(popup: Popup) {
                /** Open NFC Setting on Android phone */
                val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                startActivity(intent)
                popup.dismiss()
            }

            override fun onClosePopup() {
                delegate?.onClosePopup()
            }

            override fun onCommandError(err: Exception?, message: String?) {
                delegate?.onCommandError(err, message)
            }

            override fun onLoading(isLoading: Boolean) {
                if (isAdded) {
                    delegate?.onLoading(isLoading)
                }
            }
        }
    }

    fun process(
        intent: Intent,
        command: ByteArray,
        nfcMode: NfcMode = NfcMode.READ,
        valueToWrite: String? = null
    ) {
        if (::nfcManager.isInitialized) {
            nfcManager.processIntent(intent, command, nfcMode, valueToWrite)
        }
    }

    override fun onResume() {
        super.onResume()
        if (::nfcManager.isInitialized) {
            nfcManager.dispatch()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::nfcManager.isInitialized) {
            nfcManager.disableForegroundDispatch()
        }
    }

    fun showTray() {
        if (::nfcManager.isInitialized) {
            binding.root.isVisible = true
        }
    }

}