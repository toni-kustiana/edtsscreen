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