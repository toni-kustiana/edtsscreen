package id.co.edtslib.edtsscreen.nfc

enum class NfcMode {
    READ,
    WRITE;

    fun isRead(): Boolean {
        return this == READ
    }

    fun isWrite(): Boolean {
        return this == WRITE
    }

    companion object {
        fun fromString(mode: String?, default: NfcMode = READ): NfcMode {
            return when (mode) {
                "READ" -> READ
                "WRITE" -> WRITE
                else -> default
            }
        }
    }
}