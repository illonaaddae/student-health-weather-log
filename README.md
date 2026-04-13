# Student Health & Wellness Log with Weather Correlation

Desktop JavaFX app for students to track daily wellness metrics and relate them to weather patterns.

## What the app does

- Daily logs for mood, sleep, water intake, exercise, and notes
- User accounts (Sign In / Sign Up)
- Dashboard with trend cards and weather summary
- History table with filtering, pagination, and CSV/PDF export
- Calendar sync via `.ics` export (works with Apple/Google/Outlook calendar import)

## Tech stack

- Java 21 (project compiles with `<release>21`)
- JavaFX 21 (FXML + Controllers)
- MySQL 8 (Docker Compose ready)
- Maven Wrapper (`./mvnw`)

## Quick run

```bash
cd /Users/illona/Desktop/BeTech/Java/projects/student-health-weather-log
docker compose up -d
./mvnw clean javafx:run
```

## Database defaults used by the app

- Host: `localhost`
- Port: `3307`
- DB: `healthlog_db`
- User: `root`
- Password: `root`

These match `docker-compose.yml` and `DatabaseConnection`.

## Login details

- Use demo users from `demo-credentials.txt`, or sign up a new account.
- Sign in requires `email + password`.
- Sign up requires `name + email + password`.

## Calendar sync

- Available from History and Analytics screens.
- Exports an `.ics` file from your wellness entries.
- On macOS, you can open it directly in Calendar from the export confirmation.

## Project docs

- `QUICK_START.md` - full setup + troubleshooting
- `IMPLEMENTATION.md` - implementation notes

## Authors

- Illona (UI/UX & Frontend)
- Kingsley (Backend & Database)
