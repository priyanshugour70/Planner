# iOS Setup Guide 🍎

It looks like the `iosApp` shell was missing. I've created the basic Swift files for you in the `iosApp/` directory. Now, you need to create the actual Xcode project to run the app on iOS.

## Step 1: Create the Xcode Project
1.  Open **Xcode**.
2.  Select **File > New > Project...**
3.  Choose **iOS > App** and click **Next**.
4.  **Product Name:** `iosApp`
5.  **Organization Identifier:** `com.lssgoo`
6.  **Interface:** `SwiftUI`
7.  **Language:** `Swift`
8.  **Storage:** `None` (or as you prefer)
9.  Click **Next** and save it in the **root of your project** (where the `composeApp` folder is). 
    *Note: Ensure the folder created is named `iosApp` and contains the `.xcodeproj` file.*

## Step 2: Add the Swift Files
If Xcode created its own `iOSApp.swift` and `ContentView.swift`, you can replace them with the ones I created:
-   `iosApp/iosApp/iOSApp.swift`
-   `iosApp/iosApp/ContentView.swift`

## Step 3: Link the Compose Framework
This is the most important step to connect Kotlin to iOS.

1.  In Xcode, select the **iosApp** project in the navigator.
2.  Select the **iosApp** target.
3.  Go to the **Build Phases** tab.
4.  Click the **+** (plus) icon and select **New Run Script Phase**.
5.  Rename it to `Run Konan` (optional).
6.  Move it to be the **second** phase (before "Compile Sources").
7.  Paste the following script into the phase:
    ```bash
    cd "$SRCROOT/.."
    ./gradlew :composeApp:embedAndSignAppleFrameworkForXcode
    ```
8.  Go to the **Build Settings** tab.
9.  Search for **Framework Search Paths**.
10. Add the following path (recursive):
    ```
    $(SRCROOT)/../composeApp/build/xcode-frameworks/$(CONFIGURATION)/$(SDK_NAME)
    ```

## Step 4: Run the App
1.  Select a simulator (e.g., iPhone 17 Pro).
2.  Click the **Run** button (Play icon) in Xcode.

---

## Troubleshooting
- **Build Error: Framework not found:** Run the following command in your terminal once to pre-build the framework:
  ```bash
  ./gradlew :composeApp:assembleDebugAppleFrameworkForXcode
  ```
- **Permission Denied:** Ensure `gradlew` is executable:
  ```bash
  chmod +x gradlew
  ```
