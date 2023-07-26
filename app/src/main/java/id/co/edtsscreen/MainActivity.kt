package id.co.edtsscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import id.co.edtslib.edtsscreen.scancode.ScanCodeDelegate
import id.co.edtslib.edtsscreen.scancode.ScanCodeFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as ScanCodeFragment
        fragment.delegate = object : ScanCodeDelegate {
            override fun onScanned(code: String) {
                Toast.makeText(this@MainActivity, code, Toast.LENGTH_LONG).show()
            }

            override fun onBack() {
                finish()
            }
        }
        fragment.title = "Title"
        fragment.helper = "Scan Code by edts"

    }
}