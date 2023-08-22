# edtsscreen
android edts screen

## Setup
### Gradle

Add this to your project level `build.gradle`:
```groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
Add this to your app `build.gradle`:
```groovy
dependencies {
    implementation 'com.github.edtslib:edtsscreen:latest'
}
```

# NfcFragment

![NfcFragment](https://i.ibb.co/rmB0DHv/Screenshot-2023-08-22-at-16-23-44.png)

# Usage

Here's a basic implementation.

```xml
    <androidx.fragment.app.FragmentContainerView
        android:background="@color/colorOpacity"
        android:fitsSystemWindows="true"
        android:id="@+id/fragment_container_view"
        android:name="id.co.edtslib.edtsscreen.nfc.NfcFragment"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
### Prerequisite

Add permission tag on your manifest

```xml
    <uses-permission android:name="android.permission.NFC" />
    <uses-feature android:name="android.hardware.nfc" />
```

### Implementation

On Activity Create

```kotlin
         val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
        fragment.delegate = object : NfcDelegate {
            override fun onNfcReceived(records: List<ParsedNdefRecord>) {
                records.forEach {record ->
                    val nfcData = NfcData.fromJson(record.str())
                    if (nfcData?.id != null) {
                        Toast.makeText(this@MainActivity, nfcData.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

```

on Activity New Intent

```kotlin
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NfcFragment
        fragment.process(intent)

    }
```

# ScanCodeFragment

![ScanCodeFragment](https://i.ibb.co/Y80SNJt/2023-07-26-10-44-46.jpg)

# Usage

Here's a basic implementation.

```xml
    <androidx.fragment.app.FragmentContainerView
    android:fitsSystemWindows="true"
    android:id="@+id/fragment_container_view"
    android:name="id.co.edtslib.edtsscreen.scancode.ScanCodeFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
### Prerequisite

Add permission tag on your manifest

```xml
    <uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
tools:ignore="ScopedStorage" />
```

### Implementation

```kotlin
        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as ScanCodeFragment
fragment.delegate = object : ScanCodeDelegate {
    override fun onScanned(code: String) {
        Toast.makeText(this@MainActivity, code, Toast.LENGTH_LONG).show()
    }

    override fun onBack() {
        finish()
    }
}
fragment.title = "Title"
fragment.helper = "Scan Code by edts"

```