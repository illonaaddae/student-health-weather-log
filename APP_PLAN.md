# Student Health & Wellness Log — Project Plan

> Extracted from `student_health_app_plan.docx`

---

## 1. Project Overview

A JavaFX desktop application that lets students record their daily health, sleep hours, physical activity, and mood. The app auto-fetches daily weather data so students can observe how weather conditions affect their wellbeing.

---

## 2. Technical Requirements

- JavaFX (FXML-based UI)
- MVC (Model-View-Controller) architecture
- MySQL database with JDBC
- Maven for build and dependency management
- Weather API integration
- JSON parsing library — Gson or Jackson (add to `pom.xml` from day one)

---

## 3. Module Overview

| Module | Responsibility |
|---|---|
| User Authentication | Account creation, login, access control, account management |
| Health & Wellbeing | Daily logging of mood, sleep, activity, energy, hydration, and notes |
| Weather | Auto-fetches daily weather data (temperature, humidity, condition, UV, wind speed) |
| Database & Model | CRUD via JDBC; model classes bridging DB and JavaFX |
| UI/UX (JavaFX) | All FXML scenes, controllers, and user-facing screens |

---

## 4. Module Decomposition

### 4.1 User Authentication Module
- Account creation unit
- Login & access unit
- Account management unit

### 4.2 Health & Wellbeing Module

**Fields logged per daily entry:**
- Mood rating (scale 1–5)
- Sleep hours and quality
- Activity type and duration
- Energy level (scale 1–5)
- Water intake
- Short notes field
- Date of entry

**Units within this module:**
- Mood unit
- Sleep unit
- Physical activity unit
- Nutrition & hydration unit
- Energy & wellbeing unit
- Social & lifestyle unit
- Symptoms unit

### 4.3 Weather Module *(Auto-fetched daily — not user-entered)*
- Temperature (°C)
- Humidity (%)
- Weather condition (sunny, rainy, etc.)
- UV index
- Wind speed

### 4.4 Database & Model Module

CRUD via JDBC for:
- User Authentication module
- Weather module
- Health & Wellbeing module
- Correlation module

Model classes (bridge between DB and JavaFX):
- `User` / Auth model
- `HealthEntry` model
- `WeatherData` model

### 4.5 UI/UX JavaFX Module
- Dashboard / home screen
- Daily log entry screen
- History / calendar view
- Charts & correlation view
- Settings screen

---

## 5. Recommended Final Module List

### User Authentication Module
- Account creation unit
- Login & access unit
- Account management unit

### Health & Wellbeing Module
- Mood unit
- Sleep unit
- Physical activity unit
- Nutrition & hydration unit
- Energy & wellbeing unit
- Symptoms unit

### Weather Module
- Daily auto-fetch unit
- Offline / error fallback unit

### Correlation & Analytics Module *(NEW — deserves its own module)*
- Weather-to-health scoring unit
- Trend analysis unit

### Database & Model Module
- JDBC CRUD unit
- Model classes (User, HealthEntry, WeatherData, Correlation)

### UI/UX JavaFX Module
- Dashboard / home screen
- Daily log entry screen
- History / calendar view
- Charts & correlation view
- Settings screen

---

## 6. Suggested Improvements

| Area | Suggestion |
|---|---|
| Correlation Module | Listed under Database module but deserves its own module or dedicated service class. It is the most distinctive feature of the app and needs logic for computing relationships between weather fields and health scores. |
| UI/UX Module | Break it down into: Dashboard/Home screen, Daily Log Entry screen, History/Calendar view, and Charts/Correlation view. |
| Settings Screen | Students need to set their city/location for weather fetching and manage their profile. Add a Settings unit to the UI module. |
| Error & Offline Handling | No mention of what happens when the Weather API call fails. App should still allow health logging and retry weather fetch later. Add this to the Weather module. |
| JSON Library | Recommend naming Gson or Jackson explicitly so it can be added to `pom.xml` from day one. |
| Reporting/Analytics Unit | Add an explicit Reporting unit inside the UI module for charts: mood over time, sleep trend, weather vs mood correlation graphs. Without planning it now it tends to get rushed at the end. |
