# Planner 📱✨

**Planner** is a powerful, cross-platform productivity application built with **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**. It helps you manage your goals, tasks, habits, journals, and finances in one beautifully designed, unified interface.

---

## 🚀 Overview

Planner is designed to be your ultimate life companion for 2026 and beyond. By leveraging the power of Kotlin Multiplatform, it provides a consistent and premium experience across **Android** and **iOS**, with shared logic and UI.

## ✨ Key Features

### 🎯 Goal Achievement
- **Smart Goals**: Define and track life goals with milestones.
- **Visual Progress**: Dynamic progress bars and success indicators.
- **Categorization**: Organize goals into Health, Career, Learning, etc.

### ✅ Task Management
- **Smart Filters**: View tasks by Today, Upcoming, Overdue, or Completed.
- **Priority System**: Focus on what matters with P1 to P6 priority levels.
- **Recurring Tasks**: Support for Daily, Weekly, and Monthly repetitions.

### 🔄 Habit Tracking
- **Interactive Heatmap**: Visualize your consistency over 100 days.
- **Daily Check-ins**: Simple, rewarding tracking for your daily routines.

### 📝 Notes & Knowledge
- **Rich Notes**: Capture ideas, thoughts, and meeting summaries.
- **Linking**: Connect notes to specific goals for better context.

### 📅 Unified Calendar
- **Integrated View**: See tasks, events, and reminders in one place.
- **Monthly Insights**: Activity indicators to track your busy days.

### 📔 Mood & Journal
- **Mood Tracking**: Log your daily mood and see distribution charts.
- **Journal Entries**: Securely record your journey and reflections.

### ⚙️ Premium Settings
- **Dynamic Themes**: 10+ premium themes including Midnight, Rose Gold, and Deep Ocean.
- **Security**: App Lock / PIN protection for your data.
- **Cloud Sync**: Seamless backup and restore using AWS S3.

---

## 📁 Project Structure

The project follows a modern KMP architecture:

```
Planner/
├── composeApp/                 # Shared UI and Logic
│   ├── src/
│   │   ├── commonMain/         # Shared code (Business logic, UI, Components)
│   │   ├── androidMain/        # Android-specific platform code
│   │   └── iosMain/            # iOS-specific platform code
├── gradle/                     # Build configuration
├── README.md                   # You are here!
└── settings.gradle.kts         # Multi-module configuration
```

## 🛠 Tech Stack

- **Language**: 100% Kotlin
- **Shared UI**: Compose Multiplatform
- **Date/Time**: kotlinx-datetime
- **Multiplatform Storage**: Multiplatform Settings
- **Architecture**: MVVM with Shared ViewModels
- **Icons**: Material Symbols & Lucide-inspired Custom Icons

---

## 🛠 Setup & Installation

### Android
1. Open this project in **Android Studio (Ladybug or later)**.
2. Select the `composeApp` run configuration.
3. Click **Run**.

### iOS
1. Open the project in **Android Studio**.
2. Ensure you have **Xcode** installed.
3. Select the `iosApp` run configuration (if configured) or open the `iosApp` folder in Xcode.

---

## 🛡️ Security & Privacy

Planner is built with privacy in mind. Your data stays on your device, and cloud sync is optional and encrypted.

---

Built with ❤️ by **LSSGOO**