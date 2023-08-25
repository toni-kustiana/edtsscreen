package id.co.edtslib.edtsscreen.nfc

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson

class NfcManager(private val activity: FragmentActivity, intent: Intent) {
    interface NfcManagerDelegate {
        fun onRead(messages: Array<NdefMessage?>)
    }

    private var nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)
    private var pendingIntent: PendingIntent = PendingIntent.getActivity(
        activity, 0, intent
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE
    )

    var delegate: NfcManagerDelegate? = null

    fun dispatch() {
        //if (!nfcAdapter.isEnabled) showWirelessSettings()
        nfcAdapter?.enableForegroundDispatch(activity,
            pendingIntent,
            null,
            null)
    }

    fun processIntent(intent: Intent?) {
        if (intent != null) {
            activity.intent = intent
            resolveIntent(intent)
        }
    }

    private fun resolveIntent(intent: Intent) {
        val action = intent.action

        if (NfcAdapter.ACTION_TAG_DISCOVERED == action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == action) {
            val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val msgs: Array<NdefMessage?>
            if (rawMsgs != null) {
                msgs = arrayOfNulls(rawMsgs.size)
                for (i in rawMsgs.indices) {
                    msgs[i] = rawMsgs[i] as NdefMessage
                }

                delegate?.onRead(msgs)
            } else {
                val empty = ByteArray(0)
                val id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)
                val tag = intent.getParcelableExtra<Parcelable>(NfcAdapter.EXTRA_TAG) as Tag?
                if (tag != null) {
                    val nfcData = dumpTagData(tag)

                    val record = NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, Gson().toJson(nfcData).toByteArray())
                    val msg = NdefMessage(arrayOf(record))
                    msgs = arrayOf(msg)

                    delegate?.onRead(msgs)
                }
            }
        }
    }

    private fun dumpTagData(tag: Tag): NfcData {
        val hex = Utils.toHex(tag.id)
        val reversedHex = Utils.toReversedHex(tag.id)
        val id = Utils.toDec(tag.id)
        val reversedId = Utils.toReversedDec(tag.id)

        return NfcData(hex = hex,
            reversedHex = reversedHex,
            id = id,
            reversedId = reversedId,
            techList = tag.techList.toList())
        /*
        //tag.techList

        //sb.delete(sb.length - 2, sb.length)
        for (tech in tag.techList) {
            if (tech == MifareClassic::class.java.name) {
                sb.append('\n')
                var type = "Unknown"
                try {
                    val mifareTag = MifareClassic.get(tag)
                    when (mifareTag.type) {
                        MifareClassic.TYPE_CLASSIC -> type = "Classic"
                        MifareClassic.TYPE_PLUS -> type = "Plus"
                        MifareClassic.TYPE_PRO -> type = "Pro"
                    }
                    sb.append("Mifare Classic type: ")
                    sb.append(type)
                    sb.append('\n')
                    sb.append("Mifare size: ")
                    sb.append(mifareTag.size.toString() + " bytes")
                    sb.append('\n')
                    sb.append("Mifare sectors: ")
                    sb.append(mifareTag.sectorCount)
                    sb.append('\n')
                    sb.append("Mifare blocks: ")
                    sb.append(mifareTag.blockCount)
                } catch (e: Exception) {
                    sb.append("Mifare classic error: " + e.message)
                }
            }
            if (tech == MifareUltralight::class.java.name) {
                sb.append('\n')
                val mifareUlTag = MifareUltralight.get(tag)
                var type = "Unknown"
                when (mifareUlTag.type) {
                    MifareUltralight.TYPE_ULTRALIGHT -> type = "Ultralight"
                    MifareUltralight.TYPE_ULTRALIGHT_C -> type = "Ultralight C"
                }
                sb.append("Mifare Ultralight type: ")
                sb.append(type)
            }
        }
        return sb.toString()*/
    }

}