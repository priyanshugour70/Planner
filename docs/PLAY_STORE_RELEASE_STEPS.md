# Google Play Store - Closed Testing Release Guide

Follow these steps to complete your first release for the **Planner** app.

## Step 1: Generate the Signed App Bundle (.aab)
Before you can upload anything, you need to create the production file.

1.  **Open Android Studio** and wait for the project to sync.
2.  Go to the top menu: **Build > Generate Signed Bundle / APK...**
3.  Select **Android App Bundle** and click **Next**.
4.  **Key store path:** Click "Choose existing" and select the `planner.jks` file in your project root.
5.  **Passwords:** Enter your Key store password and Key password (the ones you used when creating the keystore).
6.  **Key alias:** Select `planner`.
7.  Click **Next**.
8.  **Destination Folder:** You can choose any folder, but the default is usually `composeApp/`. (The `release` folder will be created automatically inside this destination once the build finishes).
9.  **Build Variants:** Select **release**.
10. Click **Create**.

Wait for the "Generate Signed Bundle" notification in the bottom right of Android Studio. **The folder will only appear AFTER this step is successful.**

Wait for the "Generate Signed Bundle" notification. Click "Locate" to find your `.aab` file.

---

## Step 2: Upload to Google Play Console
Now go back to your browser where you have the Google Play Console open.

1.  **App bundles:** Drag and drop the `.aab` file you just located into the upload box.
2.  Wait for the upload and processing to finish.

---

## Step 3: Fill in Release Details
1.  **Release name:** This will automatically populate with something like "1.0 (1)". You can leave it as is or change it to something like "Initial Closed Beta". (This is not shown to users).
2.  **Release notes:** 
    - You will see `<en-GB>` tags.
    - Replace the text inside with: `Initial release of the Planner app for closed testing.`
    - It should look like this:
      ```xml
      <en-GB>
      Initial release of the Planner app for closed testing.
      </en-GB>
      ```

---

## Step 4: Preview and Confirm
1.  Click **Next** (bottom right).
2.  You might see "Errors", "Warnings", or "Messages".
    - **Errors:** Must be fixed before you can publish.
    - **Warnings:** Recommended to fix, but you can usually proceed.
3.  Click **Save** at the bottom.

---

## Step 5: Start the Test
1.  After saving, click **Go to overview**.
2.  You will need to go to the **Testers** tab (near the top) to add email addresses of people who can test your app.
3.  Share the **Join on the web** or **Join on Android** link with your testers.

---

## Troubleshooting
- **Version Code Error:** If you upload a second time, you MUST increment the `versionCode` in `composeApp/build.gradle.kts` (line 78).
- **Keystore Error:** Ensure you use the exact same `planner.jks` every time you update the app.
