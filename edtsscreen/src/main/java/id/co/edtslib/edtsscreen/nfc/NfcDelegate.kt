package id.co.edtslib.edtsscreen.nfc

import id.co.edtslib.edtsscreen.nfc.record.ParsedNdefRecord

interface NfcDelegate {
    fun onNfcReceived(records: List<ParsedNdefRecord>)

    /**
     * txBytes: Byte Array of transmitter command
     * rxBytes: Byte Array of receiver response
     * */
    fun onNfcReceived(txBytes: ByteArray, rxBytes: ByteArray)
    fun onClosePopup()
    fun onCommandError(err: Exception?, message: String?)
}