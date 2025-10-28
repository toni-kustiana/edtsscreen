package id.co.edtsscreen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.co.edtsscreen.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCoachmark.setOnClickListener {
            CoachmarkActivity.open(this)
        }

        binding.btnNFC.setOnClickListener {
            NfcActivity.open(this)
        }

    }

}