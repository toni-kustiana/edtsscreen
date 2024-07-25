package id.co.edtsscreen

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import id.co.edtslib.edtsscreen.nfc.NfcData
import id.co.edtslib.edtsscreen.nfc.NfcDelegate
import id.co.edtslib.edtsscreen.nfc.NfcFragment
import id.co.edtslib.edtsscreen.nfc.NfcManager
import id.co.edtslib.edtsscreen.nfc.Utils
import id.co.edtslib.edtsscreen.nfc.record.ParsedNdefRecord

class MainActivity : AppCompatActivity() {

    private lateinit var nfcManager: NfcManager
    private lateinit var tvText: AppCompatTextView
    private lateinit var tvText2: AppCompatTextView
    private lateinit var tvText3: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvText = findViewById(R.id.tvText)
        tvText2 = findViewById(R.id.tvText2)
        tvText3 = findViewById(R.id.tvText3)

        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
        fragment.keepTrayAfterScan = false
        fragment.delegate = object : NfcDelegate {
            override fun onNfcReceived(records: List<ParsedNdefRecord>) {
                if (records.isNotEmpty()) {
                    val nfcDataList =
                        records.filter { record: ParsedNdefRecord ->
                            val nfcData = NfcData.fromJson(record.str())
                            nfcData?.id != null
                        }.joinToString(", ") { record: ParsedNdefRecord ->
                            val nfcData = NfcData.fromJson(record.str())
                            nfcData.toString()
                        }
                    if (nfcDataList.isNotEmpty()) {
                        tvText.text = String.format("nfcData=%s\n", nfcDataList)
                        Toast.makeText(
                            this@MainActivity,
                            nfcDataList,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onNfcReceived(txBytes: ByteArray, rxBytes: ByteArray) {
                if (rxBytes.size >= 4) {
                    val balance = Utils.toInt32(rxBytes, 0)
                    tvText2.text = String.format(
                        "txBytes=%s\nrxBytes=%s\nbalance=$balance",
                        txBytes.contentToString(),
                        rxBytes.contentToString(),
                        balance
                    )
                } else {
                    tvText2.text = String.format("apdu command bytes length less than 4")
                }

                /*nfcManager.sendCommand(Utils.hexToByteArray("00B500000A"), { command, response ->
                    tvText3.text = String.format(
                        "command=${command.contentToString()}\nresponse=${response.contentToString()}"
                    )
                }, { err, message ->
                    tvText3.text = String.format("error=%s", err?.toString() ?: message)
                })*/
            }

            override fun onClosePopup() {
                // do something
            }

            override fun onCommandError(err: Exception?, message: String?) {
                val tvText3 = findViewById<AppCompatTextView>(R.id.tvText3)
                tvText3.text = String.format("error=%s", err?.toString() ?: message)
            }

        }
        setupListener()
    }

    private fun setupListener() {
        val btnScan = findViewById<Button>(R.id.btnScan)
        btnScan.setOnClickListener {
            val fragment =
                supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
            fragment.showTray()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (intent != null) {
            val fragment =
                supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
            nfcManager = fragment.nfcManager
            fragment.process(intent, Utils.hexToByteArray("00B500000A"))
        }

    }

}