package id.co.edtslib.edtsscreen.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.co.edtslib.edtsscreen.R

object PermissionUtils {
    fun checkPermission(
        fragmentActivity: FragmentActivity, permission: String,
        resultLauncher: ActivityResultLauncher<String>,
        prepareAskPermissionHandler: (() -> Unit)?, grantCallback: () -> Unit
    ) {
        if (userRunTimePermission()) {
            when {
                ContextCompat.checkSelfPermission(
                    fragmentActivity,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    grantCallback()
                }
                fragmentActivity.shouldShowRequestPermissionRationale(
                    permission
                ) -> {
                    if (prepareAskPermissionHandler == null) {
                        askPermission(resultLauncher, permission)
                    } else {
                        prepareAskPermissionHandler()
                    }
                }
                else -> {
                    if (prepareAskPermissionHandler == null) {
                        askPermission(resultLauncher, permission)
                    } else {
                        prepareAskPermissionHandler()
                    }
                }
            }
        } else {
            grantCallback()
        }
    }

    private fun isPermissionAllowed(context: Context, permission: String): Boolean {
        return if (userRunTimePermission()) {
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun isPermissionLocationAllowed(context: Context) = isPermissionAllowed(context,
        Manifest.permission.ACCESS_FINE_LOCATION)

    private fun askPermission(resultLauncher: ActivityResultLauncher<String>, permission: String) {
        resultLauncher.launch(permission)
    }

    fun askLocationPermission(resultLauncher: ActivityResultLauncher<String>) {
        resultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun askContactPermission(resultLauncher: ActivityResultLauncher<String>) {
        resultLauncher.launch(Manifest.permission.READ_CONTACTS)
    }

    fun askCameraPermission(resultLauncher: ActivityResultLauncher<Array<String>>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            resultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            )
        } else {
            resultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    fun userRunTimePermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun Activity.goToAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun Fragment.goToAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireActivity().packageName, null)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun Activity.showRationaleDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.permission_title)
            .setMessage(R.string.permission_setting)
            .setPositiveButton(R.string.permission_btn_setting) { dialog, _ ->
                dialog.cancel()
                this.goToAppSettings()
            }
            .setNegativeButton(R.string.permission_btn_back) { dialog, _ -> dialog.cancel() }
            .show()
    }

    fun Fragment.showRationaleDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.permission_title)
            .setMessage(R.string.permission_setting)
            .setPositiveButton(R.string.permission_btn_setting) { dialog, _ ->
                dialog.cancel()
                this.goToAppSettings()
            }
            .setNegativeButton(R.string.permission_btn_back) { dialog, _ -> dialog.cancel() }
            .show()
    }

    fun FragmentActivity.requestPermission(
        permission: String,
        action: () -> Unit
    ) {
        if (userRunTimePermission()) {
            val shouldProvideRationale = this.shouldShowRequestPermissionRationale(
                permission
            )
            if (shouldProvideRationale) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.permission_title)
                    .setMessage(R.string.permission_rationale)
                    .setPositiveButton(android.R.string.ok) { dialog, _ ->
                        dialog.cancel()
                        action()
                    }
                    .show()
            } else action()
        }
    }
}