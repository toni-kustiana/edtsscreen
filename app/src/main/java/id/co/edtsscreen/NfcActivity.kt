package id.co.edtsscreen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import id.co.edtslib.edtsscreen.nfc.NfcData
import id.co.edtslib.edtsscreen.nfc.NfcDelegate
import id.co.edtslib.edtsscreen.nfc.NfcFragment
import id.co.edtslib.edtsscreen.nfc.NfcManager
import id.co.edtslib.edtsscreen.nfc.NfcMode
import id.co.edtslib.edtsscreen.nfc.Utils
import id.co.edtslib.edtsscreen.nfc.record.ParsedNdefRecord
import id.co.edtsscreen.databinding.ActivityNfcBinding

class NfcActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNfcBinding
    private lateinit var nfcManager: NfcManager
    private var nfcMode: NfcMode = NfcMode.READ

    companion object{
        fun open(activity: AppCompatActivity){
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

        updateMode(nfcMode)
        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
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
                        Toast.makeText(
                            this@NfcActivity,
                            finalData,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onNfcReceived(txBytes: ByteArray, rxBytes: ByteArray) {
                if (rxBytes.size >= 4) {
                    val balance = Utils.toInt32(rxBytes, 0)
                    binding.tvNfcBytes.text = String.format(
                        "txBytes=%s\nrxBytes=%s\nbalance=$balance",
                        txBytes.contentToString(),
                        rxBytes.contentToString(),
                        balance
                    )
                } else {
                    binding.tvNfcBytes.text = String.format("apdu command bytes length less than 4")
                }
            }

            override fun onClosePopup() {
                // do something
            }

            override fun onCommandError(err: Exception?, message: String?) {
                binding.tvNfcError.text = String.format("error=%s", err?.toString() ?: message)
            }

        }
        setupListener()
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

    private fun updateMode(nfcMode: NfcMode){
        this.nfcMode = nfcMode
        binding.tvMode.text = "Mode : ${nfcMode.name}"
    }

    private fun showTray() {
        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
        binding.fragmentContainerView.isVisible = true
        fragment.showTray()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
        nfcManager = fragment.nfcManager
        fragment.process(
            intent,
            Utils.hexToByteArray("00B500000A"),
            nfcMode,
            binding.etValue.text.toString()
        )
    }

    private fun getParsedNfcData(records: List<ParsedNdefRecord>): String =
        records.filter { record: ParsedNdefRecord ->
            val nfcData = NfcData.fromJson(record.str())
            nfcData?.id != null
        }.joinToString(", ") { record: ParsedNdefRecord ->
            val nfcData = NfcData.fromJson(record.str())
            nfcData?.toString() ?: "nfcData null"
        }
}