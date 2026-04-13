# Quick Start - Student Health & Wellness Log

## 1) Prerequisites

- Java 21 installed (`java -version`)
- Docker Desktop running
- Git (optional, for pulls/branches)

> Use Maven Wrapper (`./mvnw`). You do not need global `mvn` installed.

## 2) Start database (Docker)

```bash
cd /Users/illona/Desktop/BeTech/Java/projects/student-health-weather-log
docker compose up -d
docker compose ps
```

Expected DB config (already used by app):

- host: `localhost`
- port: `3307`
- database: `healthlog_db`
- user: `root`
- password: `root`

## 3) Run the app

```bash
cd /Users/illona/Desktop/BeTech/Java/projects/student-health-weather-log
./mvnw clean javafx:run
```

If you only want to compile:

```bash
cd /Users/illona/Desktop/BeTech/Java/projects/student-health-weather-log
./mvnw clean compile
```

## 4) Sign in / Sign up

- **Sign in**: email + password
- **Sign up**: name + email + password
- Demo users are in `demo-credentials.txt`

## 5) Core features to test

- Add Log: create a daily entry
- Dashboard: verify cards/trends update with latest data
- History:
  - pagination (Prev/Next + page numbers)
  - filters (date, mood, weather, search)
  - export CSV / PDF
- Calendar Sync (History and Analytics):
  - exports `.ics`
  - on macOS: button supports **Open in Calendar**

## 6) Calendar sync behavior

- The app generates an iCalendar file from your saved logs.
- File can be imported/opened in Apple Calendar, Google Calendar, Outlook.
- This is file-based sync (no OAuth cloud account linkage yet).

## 7) Troubleshooting

### `zsh: command not found: mvn`

Use wrapper instead:

```bash
./mvnw clean javafx:run
```

### Database unavailable / mock mode

1. Ensure Docker is up:

```bash
docker compose ps
```

2. Restart DB:

```bash
docker compose down
docker compose up -d
```

3. Re-run app.

### MySQL schema errors like unknown column

Reset DB volume and re-initialize tables:

```bash
docker compose down -v
docker compose up -d
```

### JavaFX warnings about `sun.misc.Unsafe`

These warnings come from JavaFX/native dependencies and are usually non-fatal for local development.

## 8) Useful project files

- `README.md` - overview
- `demo-credentials.txt` - demo account details
- `docker-compose.yml` - MySQL container
- `db/init.sql` - DB bootstrap script

