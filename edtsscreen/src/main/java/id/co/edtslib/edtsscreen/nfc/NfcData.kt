package id.co.edtslib.edtsscreen.nfc

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class NfcData (
    val hex: String?,
    val reversedHex: String?,
    val id: Long?,
    val reversedId: Long?,
    val techList: List<String>
) {
    companion object {
        fun fromJson(json: String) =
            Gson().fromJson<NfcData?>(json, object : TypeToken<NfcData?>() {}.type)
     }
}