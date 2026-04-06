# Student Health & Wellness Log with Weather Correlation

A JavaFX application that allows students to record daily health metrics (sleep, activity, mood, etc.) and correlates them with local weather data.

## Project Overview

- **Health & Wellbeing Module**: Logs mood, sleep, physical activity, nutrition, and more.
- **Weather Module**: Automatically fetches weather data for the user's location.
- **Correlation Module**: Analyzes how weather conditions affect student wellbeing.
- **MVC Architecture**: Clear separation between UI (FXML/Controllers) and business logic.

## Technical Requirements

- Java 21+
- MySQL Database
- Maven
- JavaFX 21

## Setup Instructions

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/illonaaddae/student-health-weather-log.git
    ```
2.  **Database Configuration**:
    - Ensure MySQL is running.
    - Create a database named `wellness_db`.
    - (Upcoming: Run the migration scripts in `sql/`).
3.  **Build and Run**:
    ```bash
    ./mvnw clean javafx:run
    ```

## Recent Features & Fixes

- **Interactive Dashboard**: Programmatic control over trend views (Weekly vs. Monthly).
- **Navigation System**: Centralized navigation for seamless view switching.
- **Search & Filter**: Real-time search in the sidebar and data filtering in History.
- **Backend Integration**: Initial integration of backend dependencies (MySQL, Gson) and directory structure.

## Authors

- Illona (UI/UX & Frontend)
- Kingsley (Backend & Database)
- Junie (BeTech Assistant)
