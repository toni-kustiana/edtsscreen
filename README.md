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

Add config on app build gradle

```groovy
    configurations {
    all*.exclude group: 'com.google.guava', module: 'listenablefuture'
}
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
# InAppBannerDialog

![InAppBannerDialog](https://i.ibb.co.com/NY3N3TB/2024-07-26-08-16-20.jpg)

### Implementation
Add in app banner module to Koin Module Configuration
```kotlin
object GroceryApp {
    val modules = listOf(
        inAppBannerModule
    )
}
```

Show In App Dialog
```kotlin
            InAppBannerDialog.show(
    fragmentActivity = this@HomeV2Activity,
    flowData = viewModel.getInAppBanner()
)
```
Define your view model
```kotlin
    fun getInAppBanner() = inAppBannerUseCase.get(
        path = "notification/api/mobile/push-notification/getinapp",
        client = "klikindomaret",
        platform = "android"
    )
```
# CoachMarkView
![CoachMarkView](https://i.ibb.co.com/2qQvC65/Screenshot-2024-07-27-at-9-09-10-PM.png)

# Usage

Here's a basic implementation.

```xml
    <id.co.edtslib.edtsscreen.coachmark.CoachMarkView
        android:focusableInTouchMode="true"
        android:focusable="true"
        android:clickable="true"
        android:id="@+id/coachMarkView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
#### Model CoachData
    rect: rectangle of coach mark view
    imageResId: icon of coach mark view
    title: title of coach mark view
    sort: sort  of coach mark view
    positiveText: text of button
    trianglePosition: null if pointer center of coach mark view rectangle
    shape: shape of coach mark view

### Implementation

Add coach mark item

```kotlin
        val rect = Rect()
        binding.homeToolbar.tabLayout.getGlobalVisibleRect(rect)

        binding.coachMarkView.add(
            CoachData(
                rect = rect,
                imageResId = edts.klikidm.android.feature_home_v2.R.drawable.ic_coach_mark_3,
                title = getString(edts.klikidm.android.feature_home_v2.R.string.coach_mark_3_title),
                description = getString(edts.klikidm.android.feature_home_v2.R.string.coach_mark_3_description),
                sort = 2,
                alignInfo = CoachAlign.Bottom,
                positiveText = getString(edts.klikidm.android.feature_home_v2.R.string.coach_mark_next),
                trianglePosition = null,
                shape = CoachShape.createRectangle(
                    rad = resources.getDimensionPixelSize(edts.klikidm.android.core_resource.R.dimen.dimen_16dp)
                )
            )
        )
```
Delegate of coach mark
```kotlin
binding.coachMarkView.delegate = object : CoachMarkDelegate {
            override fun onClose() {
                // to do 
            }
        }
```

Show coach mark
```kotlin
binding.coachMarkView.show(this)
```

Proguard Setting
```kotlin
-keep public class id.co.edtslib.edtsscreen.inappbanner.data.mapper.**
-keep public class id.co.edtslib.edtsscreen.inappbanner.domain.model.** { *; }
```