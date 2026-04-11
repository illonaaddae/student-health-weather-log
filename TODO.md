# Student Health & Wellness Log — TODO

> Derived from `APP_PLAN.md`. Checked items are verified present in the codebase.

---

## Module 1 — User Authentication

### Models & Data
- [x] `User` model with validated fields (username, email, password, city)
- [x] `UserRepository` — `save`, `findById`, `findByUsername`, `findAll`, `update`, `delete`
- [x] Password hashing before storing (PBKDF2WithHmacSHA256)

### UI
- [x] Auth screen with sign-in / sign-up tabs (`AuthController`)
- [x] DB-backed authentication — login and signup wired to `UserRepository`
- [x] Account management — edit profile, change password, delete account wired to `UserRepository`
- [x] Input validation feedback on auth form (error messages shown inline via statusLabel)

### Tests
- [x] `UserModelTests` — constructor, all setters, validation boundaries, equals/hashCode
- [x] `UserRepositoryTest` — full CRUD integration tests with H2

---

## Module 2 — Health & Wellbeing

### Models & Data
- [x] `HealthEntry` model — moodScore (String: Low/Neutral/Good/Great/Tired), sleepHours, waterIntake, exercise, weatherCondition, temperature, notes
- [x] `HealthEntryRepository` — `save`, `findById`, `findByUserId`, `findAll`, `update`, `delete`

### UI
- [x] Daily log entry screen (`AddLogController`) — mood buttons, sleep/water/duration spinners, activity dropdown
- [x] Dashboard screen (`DashboardController`) — mood, sleep, water, exercise summary; weekly/monthly trends
- [x] History screen (`HistoryController`) — table view with filtering, search, pagination
- [x] History screen — export to CSV fully wired end-to-end
- [x] History screen — export to PDF fully wired end-to-end
- [x] `AddLogController` wired to `HealthEntryRepository` (saves to DB)
- [x] `DashboardController` loads real data from `HealthEntryRepository`
- [x] `HistoryController` loads real data from `HealthEntryRepository`

### Tests
- [x] `HealthEntryTests` — all setters, validation boundaries, equals/hashCode
- [x] `HealthEntryRepositoryTest` — full CRUD integration tests with H2

---

## Module 3 — Weather

### Models & Data
- [x] `WeatherData` model — temperature, humidity, condition, uvIndex, windSpeed, city, date (all validated)
- [x] `WeatherDataRepository` — `save`, `findById`, `findByCity`, `findAll`, `update`, `delete`
- [x] `WeatherService` — fetches live data from OpenWeatherMap API
- [x] Auto-fetch weather on login / app startup and persist to DB via `WeatherDataRepository`
- [x] Offline / error fallback — app continues to function when API call fails; retry on next launch

### UI
- [x] Weather insights screen (`WeatherInsightsController`) — weather-wellness correlation charts

### Tests
- [x] `WeatherDataTests` — all setters, validation boundaries, equals/hashCode
- [x] `WeatherDataRepositoryTest` — full CRUD integration tests with H2
- [x] `WeatherService` test — mock HTTP response to verify JSON parsing and `WeatherData` mapping

---

## Module 4 — Correlation & Analytics

### Models & Data
- [x] `Correlation` model — links a `HealthEntry` to a `WeatherData` record with computed score
- [x] `CorrelationRepository` — CRUD for correlation records
- [x] Correlation service / logic — computes relationship between weather fields and health scores

### UI
- [x] Weather insights screen exists (`WeatherInsightsController`) with correlation charts
- [x] Wire `WeatherInsightsController` to real correlation data (currently appears to use mock/seeded data)
- [x] Trend analysis view — mood over time, sleep trend, weather vs mood graphs

### Tests
- [x] `CorrelationRepositoryTest` — H2 integration tests

---

## Module 5 — Database & Model

### Infrastructure
- [x] `DatabaseConnection` — connects to MySQL via Docker (`localhost:3307/healthlog_db`), auto-creates tables
- [x] Mock mode fallback when DB connection fails
- [x] Remove legacy `HealthLogRepository` (`database` package) — deleted
- [ ] Schema migration strategy — currently drops and recreates; add versioned migrations

### JSON Library
- [x] Gson added as dependency (present in `module-info.java`)

---

## Module 6 — UI/UX (JavaFX)

### Screens
- [x] Auth screen (sign-in / sign-up)
- [x] Dashboard / home screen
- [x] Daily log entry screen
- [x] History / table view
- [x] Weather insights / correlation view
- [x] Settings screen — theme toggle, city, page size, profile

### Navigation & Utilities
- [x] `AppRouter` — scene switching between auth and main views
- [x] `MainController` — sidebar navigation with screen caching
- [x] `Toast` — lightweight in-app notification popup
- [x] `UserPreferences` — persists city, theme, username, email between sessions
- [x] Dark / light theme toggle wired in `SettingsController`

### Remaining UI work
- [x] Calendar view — `syncCalendar()` implemented with confirmation + progress dialog
- [x] Deep insights view — `viewDeepInsights()` navigates to Weather Insights screen
- [x] Goals view — `viewGoals()` implemented with sunlight goals dialog
- [x] Profile picture upload wired end-to-end — persists path via `UserPreferences`, restored on startup

---

## Cross-cutting

### Tech Stack
- [x] JavaFX with FXML
- [x] MVC architecture
- [x] MySQL + JDBC
- [x] Maven build
- [x] OpenWeatherMap API integration
- [x] Gson for JSON parsing

### Testing
- [x] Model unit tests — `User`, `WeatherData`, `HealthEntry`
- [x] Repository integration tests — all three repositories with H2 in-memory DB
- [ ] Controller tests — `AddLogController`, `DashboardController`, `HistoryController`
- [x] `WeatherService` unit test with mocked HTTP
- [x] `CorrelationRepository` integration test
