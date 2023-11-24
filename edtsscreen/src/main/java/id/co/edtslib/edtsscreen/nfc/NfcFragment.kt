package id.co.edtslib.edtsscreen.nfc

import android.content.Intent
import android.nfc.NdefMessage
import android.provider.Settings
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import id.co.edtslib.edtsds.popup.Popup
import id.co.edtslib.edtsscreen.databinding.EdtsScreenFragmentNfcBinding
import id.co.edtslib.edtsscreen.nfc.parser.NdefMessageParser
import id.co.edtslib.uibase.BaseFragment

open class NfcFragment: BaseFragment<EdtsScreenFragmentNfcBinding>() {
    lateinit var nfcManager: NfcManager

    var delegate: NfcDelegate? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> EdtsScreenFragmentNfcBinding
        get() = EdtsScreenFragmentNfcBinding::inflate

    override fun getTrackerPageName(): String? = null

    override fun setup() {
        binding.bottomLayout.titleDivider = false

        nfcManager = NfcManager(requireActivity(), Intent(requireActivity(), requireActivity().javaClass))
        nfcManager.checkNfcFeature {
            binding.root.isVisible = true
        }
        nfcManager.delegate = object : NfcManager.NfcManagerDelegate {
            override fun onRead(messages: Array<NdefMessage?>) {
                binding.root.isVisible = true
                binding.root.postDelayed({
                    binding.root.isVisible = delegate?.keepTrayAfterScan() == true
                    messages.forEach {
                        val records = NdefMessageParser.parse(it)
                        delegate?.onNfcReceived(records)
                    }
                }, 1500L)
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
        }
    }

    fun process(intent: Intent?, bytes: ByteArray? = null) {
        if (intent != null) {
            nfcManager.processIntent(intent, bytes)
        }
    }

    override fun onResume() {
        super.onResume()
        nfcManager.dispatch()
    }

    fun showTray() {
        binding.root.isVisible = true
    }

}