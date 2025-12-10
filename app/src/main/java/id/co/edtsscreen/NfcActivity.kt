package id.co.edtsscreen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import id.co.edtslib.edtsscreen.nfc.NfcData
import id.co.edtslib.edtsscreen.nfc.NfcDelegate
import id.co.edtslib.edtsscreen.nfc.NfcFragment
import id.co.edtslib.edtsscreen.nfc.NfcMode
import id.co.edtslib.edtsscreen.nfc.Utils
import id.co.edtslib.edtsscreen.nfc.record.ParsedNdefRecord
import id.co.edtsscreen.databinding.ActivityNfcBinding

class NfcActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNfcBinding
    private val nfcFragment: NfcFragment?
        get() = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as? NfcFragment

    private var nfcMode: NfcMode = NfcMode.READ

    companion object {
        private const val READ_BALANCE_COMMAND = "00B500000A"
        fun open(activity: AppCompatActivity) {
            activity.startActivity(
                Intent(
                    activity,
                    NfcActivity::class.java
                )
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNfcFragment()
        updateMode(nfcMode)
        setupListener()
    }

    fun setupNfcFragment() {
        nfcFragment?.let { fragment ->
            fragment.keepTrayAfterScan = false
            fragment.delegate = object : NfcDelegate {
                override fun onNfcReceived(records: List<ParsedNdefRecord>) {
                    if (records.isNotEmpty()) {
                        val nfcDataList = getParsedNfcData(records)
                        val nfcNonParsed = records.joinToString(", ") { it.str() }
                        val finalData = nfcDataList.ifEmpty { nfcNonParsed }

                        if (finalData.isNotEmpty()) {
                            binding.tvNfcData.text = String.format(
                                "nfcData=%s\n", finalData
                            )
                            val snackBar =
                                Snackbar.make(binding.root, finalData, Snackbar.LENGTH_SHORT)
                            snackBar.show()
                        }
                    }
                }

                override fun onNfcReceived(txBytes: ByteArray, rxBytes: ByteArray) {
                    if (rxBytes.size >= 4) {
                        val balance = Utils.toInt32(rxBytes, 0)
                        binding.tvNfcBytes.text =
                            getString(
                                R.string.txbytes_rxbytes_balance,
                                txBytes.contentToString(),
                                rxBytes.contentToString(),
                                balance
                            )

                    } else {
                        binding.tvNfcBytes.text =
                            String.format("apdu command bytes length less than 4")
                    }
                }

                override fun onClosePopup() {
                    // do something
                }

                override fun onCommandError(err: Exception?, message: String?) {
                    binding.tvNfcError.text = String.format("error=%s", err?.toString() ?: message)
                }

                override fun onLoading(isLoading: Boolean) {
                    // if the process is too fast (small data), we can use dummy progress if needed
                    //                binding.progressBar.apply {
                    //                    if (isLoading){
                    //                        isVisible = true
                    //                        postDelayed({
                    //                            isVisible = false
                    //                        }, 500)
                    //                    }
                    //                }

                    binding.progressBar.isVisible = isLoading
                }

            }
        }
    }

    private fun setupListener() {
        binding.btnRead.setOnClickListener {
            updateMode(NfcMode.READ)
            showTray()
        }
        binding.btnWrite.setOnClickListener {
            updateMode(NfcMode.WRITE)
            showTray()
        }
    }

    private fun updateMode(nfcMode: NfcMode) {
        this.nfcMode = nfcMode
        binding.tvMode.text = getString(R.string.mode, nfcMode.name)
    }

    private fun showTray() {
        binding.fragmentContainerView.isVisible = true
        nfcFragment?.showTray()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        nfcFragment?.process(
            intent,
            Utils.hexToByteArray(READ_BALANCE_COMMAND),
            nfcMode,
            binding.etValue.text.toString()
        )
    }

    private fun getParsedNfcData(records: List<ParsedNdefRecord>): String =
        records
            .mapNotNull { record -> NfcData.fromJson(record.str()) }
            .filter { nfcData -> nfcData.id != null }
            .joinToString(", ") { nfcData -> nfcData.toString() }

}