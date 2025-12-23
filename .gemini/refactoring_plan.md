# Planner App Refactoring Plan

## Architecture Overview

Following Single Responsibility Principle (SRP) and Clean Architecture:

```
app/src/main/java/com/lssgoo/planner/
├── data/                          # Backend Layer (Storage, Processing, Logic)
│   ├── local/                     # Local Storage
│   │   └── LocalStorageManager.kt
│   ├── remote/                    # AWS S3 Sync
│   │   └── S3Manager.kt
│   ├── model/                     # Shared Data Models
│   │   └── Models.kt              # Type aliases + shared models
│   ├── repository/                # Data Operations (NEW)
│   │   ├── GoalRepository.kt
│   │   ├── TaskRepository.kt
│   │   ├── NoteRepository.kt
│   │   ├── HabitRepository.kt
│   │   ├── JournalRepository.kt
│   │   ├── FinanceRepository.kt
│   │   └── SearchRepository.kt
│   └── analytics/                 # Analytics Processing
│       └── AnalyticsManager.kt
│
├── features/                      # Feature Modules
│   ├── dashboard/
│   │   ├── screens/              # UI Screens
│   │   └── components/           # Feature-specific components
│   ├── goals/
│   │   ├── models/               # Goal data classes
│   │   ├── screens/              # GoalsScreen, GoalDetailScreen
│   │   └── components/           # GoalCard, etc.
│   ├── tasks/
│   │   ├── models/
│   │   ├── screens/
│   │   └── components/
│   ├── notes/
│   │   ├── models/
│   │   ├── screens/
│   │   └── components/
│   ├── habits/
│   │   ├── models/
│   │   ├── screens/
│   │   └── components/
│   ├── journal/
│   │   ├── models/
│   │   ├── screens/
│   │   └── components/
│   ├── finance/
│   │   ├── models/
│   │   ├── screens/
│   │   └── components/
│   ├── calendar/
│   │   ├── screens/
│   │   └── components/
│   ├── search/
│   │   ├── models/
│   │   ├── screens/
│   │   └── components/
│   ├── analytics/
│   │   ├── screens/
│   │   └── components/
│   ├── reminders/
│   │   ├── models/
│   │   ├── screens/
│   │   └── components/
│   ├── settings/
│   │   ├── models/
│   │   ├── screens/
│   │   └── components/
│   └── onboarding/
│       └── screens/
│
├── ui/                            # Global UI
│   ├── components/               # Reusable UI Components
│   │   ├── AnimatedProgressBar.kt
│   │   ├── GradientFAB.kt
│   │   ├── EmptyState.kt
│   │   ├── SectionHeader.kt
│   │   ├── StatsCard.kt
│   │   ├── AppIcons.kt
│   │   └── CommonCards.kt
│   ├── theme/                    # Dynamic Theming
│   │   ├── Color.kt              # Semantic colors
│   │   ├── Theme.kt              # Theme setup
│   │   └── Type.kt               # Typography
│   ├── navigation/
│   │   └── Navigation.kt
│   └── viewmodel/                # App-level ViewModel
│       └── PlannerViewModel.kt   # Break into smaller VMs
│
└── MainActivity.kt
```

## Key Principles

1. **Max 200-300 lines per file**
2. **Dynamic theme with semantic colors**
3. **Global UI components for reuse**
4. **Data layer as "backend" - all storage/logic here**
5. **Sync to AWS S3 at end**

## Phase 1: Fix Build Errors (Current)
- [x] Add missing HabitEntry model
- [ ] Fix AnalyticsManager model mismatches
- [ ] Fix UserProfile type conflict
- [ ] Ensure all imports resolve

## Phase 2: Split Large Files
- [ ] Break PlannerViewModel (1000+ lines) into feature ViewModels
- [ ] Break LocalStorageManager into repositories
- [ ] Break SettingsScreen (1154 lines) into components

## Phase 3: Consolidate UI Components
- [ ] Move all reusable components to ui/components
- [ ] Create semantic color tokens for dynamic theming
- [ ] Remove duplicate components from screens

## Phase 4: Finalize Architecture
- [ ] Ensure feature isolation
- [ ] Verify S3 sync works with new structure
- [ ] Document architecture
