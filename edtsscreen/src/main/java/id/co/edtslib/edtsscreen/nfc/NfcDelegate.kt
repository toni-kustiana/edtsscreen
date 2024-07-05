package id.co.edtslib.edtsscreen.nfc

import id.co.edtslib.edtsscreen.nfc.record.ParsedNdefRecord

interface NfcDelegate {
    fun onNfcReceived(records: List<ParsedNdefRecord>)
    /**
     * txBytes: Byte Array of transmitter command
     * rxBytes: Byte Array of receiver result
     * */
    fun onNfcReceived(txBytes: ByteArray, rxBytes: ByteArray)
    fun keepTrayAfterScan(): Boolean
    fun onClosePopup()
    fun onCommandError(err: Exception?, message: String?)
}