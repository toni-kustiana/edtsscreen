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
import id.co.edtslib.edtsscreen.nfc.Utils
import id.co.edtslib.edtsscreen.nfc.record.ParsedNdefRecord

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
        fragment.delegate = object : NfcDelegate {
            override fun onNfcReceived(records: List<ParsedNdefRecord>) {
                records.forEach { record ->
                    val nfcData = NfcData.fromJson(record.str())
                    if (nfcData?.id != null) {
                        val tvText = findViewById<AppCompatTextView>(R.id.tvText)
//                        tvText.text = nfcData.toString()
                        Toast.makeText(this@MainActivity, nfcData.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onNfcReceived(txBytes: ByteArray, rxBytes: ByteArray) {
                val tvText = findViewById<AppCompatTextView>(R.id.tvText)
                val balance = Utils.toInt32(rxBytes, 0)
                tvText.text = String.format(
                    "bytes=%s\nhex=%s\nbalance=$balance",
                    rxBytes.contentToString(),
                    rxBytes,
                    balance
                )
            }

            override fun keepTrayAfterScan() = false
            override fun onClosePopup() {
                // do something
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

        val fragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
        fragment.process(intent, Utils.hexToByteArray("00B500000A"))

    }
}