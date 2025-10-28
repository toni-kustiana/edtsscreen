package id.co.edtslib.edtsscreen.nfc

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Build
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import id.co.edtslib.edtsds.popup.Popup
import id.co.edtslib.edtsds.popup.PopupDelegate

class NfcManager(private val activity: FragmentActivity, intent: Intent) {
    interface NfcManagerDelegate {
        fun onRead(messages: Array<NdefMessage?>)

        /**
         * txBytes: Byte Array of transmitter command
         * rxBytes: Byte Array of receiver result
         * */
        fun onCommandReceived(txBytes: ByteArray, rxBytes: ByteArray)
        fun openSetting(popup: Popup)
        fun onClosePopup()
        fun onCommandError(err: Exception?, message: String?)
    }

    private var nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(activity)
    private var pendingIntent: PendingIntent = PendingIntent.getActivity(
        activity, 0, intent
            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE
    )
    var isoDep: IsoDep? = null

    var delegate: NfcManagerDelegate? = null
    var timeout = 5000

    fun checkNfcFeature(callback: () -> Unit) {
        Utils.checkNfcStatus(nfcAdapter, {
            Popup.show(
                activity = activity,
                title = "Fitur NFC",
                message = "Ponsel anda tidak mendukung fitur NFC.",
                positiveButton = "Tutup",
                positiveClickListener = object : PopupDelegate {
                    override fun onClick(popup: Popup, view: View) {
                        popup.dismiss()
                        delegate?.onClosePopup()
                    }
                }
            )
        }, {
            callback.invoke()
        }, {
            Popup.show(
                activity = activity,
                title = "Fitur NFC",
                message = "Aktifkan fitur NFC pada ponsel anda.",
                positiveButton = "Pengaturan",
                negativeButton = "Batal",
                positiveClickListener = object : PopupDelegate {
                    override fun onClick(popup: Popup, view: View) {
                        /** Open NFC Setting on Android phone */
                        val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                        activity.startActivity(intent)
                        popup.dismiss()
                    }
                },
                negativeClickListener = null
            )
        })
    }

    fun dispatch() {
        //if (!nfcAdapter.isEnabled) showWirelessSettings()
        nfcAdapter?.enableForegroundDispatch(
            activity,
            pendingIntent,
            null,
            null
        )
    }

    fun processIntent(
        intent: Intent,
        command: ByteArray,
        isRead: Boolean = true,
        valueToWrite: String? = null
    ) {
        if (isRead){
            activity.intent = intent
            resolveIntent(intent, command)
        } else {
            writeToTag(intent, valueToWrite)
        }
    }

    @Suppress("DEPRECATION")
    private fun resolveIntent(intent: Intent, command: ByteArray) {
        val action = intent.action

        if (NfcAdapter.ACTION_TAG_DISCOVERED == action ||
            NfcAdapter.ACTION_TECH_DISCOVERED == action ||
            NfcAdapter.ACTION_NDEF_DISCOVERED == action
        ) {
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

                    val record = NdefRecord(
                        NdefRecord.TNF_UNKNOWN,
                        empty,
                        id,
                        Gson().toJson(nfcData).toByteArray()
                    )
                    val msg = NdefMessage(arrayOf(record))
                    msgs = arrayOf(msg)

                    delegate?.onRead(msgs)

                    processIsoDep(tag, command)

                }
            }
        }
    }

    private fun connectToTag(isoDep: IsoDep): Boolean {
        if (!isoDep.isConnected) {
            try {
                isoDep.connect()
                isoDep.timeout = timeout  // 5 sec time out
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("NfcManager", "Could not connect to tag")
                delegate?.onCommandError(e, "TRANSMISSION_ERROR")
                return false
            }

        }
        return true
    }

    private fun processIsoDep(tag: Tag, command: ByteArray) {
        isoDep = IsoDep.get(tag)
        isoDep?.let {
            val isConnected = connectToTag(it)
            if (isConnected) {
                sendCommand(command)
            } else {
                Log.e("NfcManager", "NFC not connected")
                delegate?.onCommandError(null, "NFC not connected")
            }
        }
    }

    fun closeConnection() {
        isoDep?.close()
    }

    fun sendCommand(command: ByteArray) {
        if (isoDep != null) {
            try {
                val apduResponse = isoDep!!.transceive(command)
                delegate?.onCommandReceived(command, apduResponse)
            } catch (err: Exception) {
                err.printStackTrace()
                Log.e("NfcManager", "error=${err.message}")
                delegate?.onCommandError(err, err.message)
            }
        } else {
            Log.e("NfcManager", "isoDep is null")
            delegate?.onCommandError(null, "isoDep is null")
        }
    }

    fun sendCommand(
        command: ByteArray,
        onSuccess: (command: ByteArray, response: ByteArray) -> Unit,
        onError: (error: Exception?, message: String?) -> Unit
    ) {
        if (isoDep != null) {
            try {
                val apduResponse = isoDep!!.transceive(command)
                onSuccess.invoke(command, apduResponse)
            } catch (err: Exception) {
                err.printStackTrace()
                Log.e("NfcManager", "error=${err.message}")
                onError.invoke(err, err.message)
            }
        } else {
            Log.e("NfcManager", "isoDep is null")
            onError.invoke(null, "isoDep is null")
        }
    }

    private fun dumpTagData(tag: Tag): NfcData {
        val hex = Utils.toHex(tag.id)
        val reversedHex = Utils.toReversedHex(tag.id)
        val id = Utils.toDec(tag.id)
        val reversedId = Utils.toReversedDec(tag.id)

        return NfcData(
            hex = hex,
            reversedHex = reversedHex,
            id = id,
            reversedId = reversedId,
            techList = tag.techList.toList()
        )
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

    fun writeToTag(intent: Intent, text: String?) {
        val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }
        if (tag == null) {
            Log.e("NfcManager", "No NFC tag found in intent")
            delegate?.onCommandError(null, "No NFC tag found")
            return
        }

        try {
            val ndef = android.nfc.tech.Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (!ndef.isWritable) {
                    delegate?.onCommandError(null, "Tag is read-only")
                    ndef.close()
                    return
                }
                if (text.isNullOrEmpty()){
                    delegate?.onCommandError(null, "Value is not valid")
                    ndef.close()
                    return
                }

                // Create a simple text record in NDEF format
                val languageCode = "en"
                val textBytes = text.toByteArray(Charsets.UTF_8)
                val langBytes = languageCode.toByteArray(Charsets.US_ASCII)
                val payload = ByteArray(1 + langBytes.size + textBytes.size)
                payload[0] = langBytes.size.toByte()
                System.arraycopy(langBytes, 0, payload, 1, langBytes.size)
                System.arraycopy(textBytes, 0, payload, 1 + langBytes.size, textBytes.size)

                val record = NdefRecord(
                    NdefRecord.TNF_WELL_KNOWN,
                    NdefRecord.RTD_TEXT,
                    ByteArray(0),
                    payload
                )
                val message = NdefMessage(arrayOf(record))

                // Check tag capacity
                if (ndef.maxSize < message.toByteArray().size) {
                    delegate?.onCommandError(null, "Tag capacity too small")
                    ndef.close()
                    return
                }

                // Write message
                ndef.writeNdefMessage(message)
                ndef.close()

                Log.d("NfcManager", "Write successful: $text")
                delegate?.onRead(arrayOf(message)) // optionally notify success
            } else {
                // Handle non-NDEF formatted tag
                val formattable = android.nfc.tech.NdefFormatable.get(tag)
                if (formattable != null) {
                    formattable.connect()
                    val message = NdefMessage(
                        arrayOf(
                            NdefRecord.createTextRecord("en", text)
                        )
                    )
                    formattable.format(message)
                    formattable.close()
                    Log.d("NfcManager", "Tag formatted and written successfully")
                    delegate?.onRead(arrayOf(message))
                } else {
                    delegate?.onCommandError(null, "Tag is not NDEF compatible")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            delegate?.onCommandError(e, e.message)
        }
    }


}