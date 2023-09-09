package id.co.edtslib.edtsscreen.nfc

import id.co.edtslib.edtsscreen.nfc.record.ParsedNdefRecord

interface NfcDelegate {
    fun onNfcReceived(records: List<ParsedNdefRecord>)
    fun onNfcReceived(bytes: ByteArray, hex: String)
    fun keepTrayAfterScan(): Boolean
}