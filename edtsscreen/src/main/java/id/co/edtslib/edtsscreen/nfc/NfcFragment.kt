package id.co.edtslib.edtsscreen.nfc

import android.content.Intent
import android.nfc.NdefMessage
import android.view.LayoutInflater
import android.view.ViewGroup
import id.co.edtslib.edtsscreen.databinding.FragmentNfcBinding
import id.co.edtslib.edtsscreen.nfc.parser.NdefMessageParser
import id.co.edtslib.uibase.BaseFragment

class NfcFragment: BaseFragment<FragmentNfcBinding>() {
    lateinit var nfcManager: NfcManager

    var delegate: NfcDelegate? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentNfcBinding
        get() = FragmentNfcBinding::inflate

    override fun getTrackerPageName(): String? = null

    override fun setup() {
        binding.bottomLayout.titleDivider = false

        nfcManager = NfcManager(requireActivity(), Intent(requireActivity(), requireActivity().javaClass))
        nfcManager.delegate = object : NfcManager.NfcManagerDelegate {
            override fun onRead(messages: Array<NdefMessage?>) {
                messages.forEach {
                    val records = NdefMessageParser.parse(it)
                    delegate?.onNfcReceived(records)
                }
            }
        }
    }

    fun process(intent: Intent?) {
        if (intent != null) {
            nfcManager.processIntent(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        nfcManager.dispatch()
    }
}