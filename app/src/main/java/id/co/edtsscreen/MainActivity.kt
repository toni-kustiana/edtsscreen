package id.co.edtsscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import id.co.edtslib.edtsscreen.nfc.NfcData
import id.co.edtslib.edtsscreen.nfc.NfcDelegate
import id.co.edtslib.edtsscreen.nfc.NfcFragment
import id.co.edtslib.edtsscreen.nfc.record.ParsedNdefRecord

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
        fragment.delegate = object : NfcDelegate {
            override fun onNfcReceived(records: List<ParsedNdefRecord>) {
                records.forEach {record ->
                    val nfcData = NfcData.fromJson(record.str())
                    if (nfcData?.id != null) {
                        Toast.makeText(this@MainActivity, nfcData.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun keepTrayAfterScan() = false
        }
        setupListener()
    }

    private fun setupListener() {
        val btnScan = findViewById<Button>(R.id.btnScan)
        btnScan.setOnClickListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
            fragment.showTray()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
        fragment.process(intent)

    }
}