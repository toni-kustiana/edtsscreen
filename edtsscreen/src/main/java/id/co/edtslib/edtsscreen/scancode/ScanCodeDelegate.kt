package id.co.edtslib.edtsscreen.scancode

interface ScanCodeDelegate {
    fun onScanned(code: String)
    fun onBack()
}