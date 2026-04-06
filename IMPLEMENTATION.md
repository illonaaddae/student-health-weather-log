# Wellness Sanctuary - JavaFX Implementation Guide

## Overview

This document provides a comprehensive guide to the Wellness Sanctuary desktop application, a JavaFX-based health and wellness tracking system built following the MVC (Model-View-Controller) architecture.

---

## Architecture Overview

### Design System: "The Digital Sanctuary"

The application follows a high-end editorial design philosophy focused on:
- **Negative Space**: Intentional whitespace for visual hierarchy
- **No-Line Rule**: Tonal layering instead of borders
- **Glassmorphism**: Subtle depth and elevation effects
- **Typography Hierarchy**: Manrope for headlines, Inter for body text
- **Surface Colors**: Layered background tiers for visual depth

### Color Palette

| Token | Hex | Usage |
|-------|-----|-------|
| Primary | #005faf | Buttons, accents, primary actions |
| Primary Dim | #00539a | Button hover states |
| Surface | #f9f9f9 | Main background |
| Surface Container Low | #f2f4f4 | Sidebar background |
| Surface Container | #ebeeef | Secondary sections |
| Surface Container High | #e4e9ea | Input field backgrounds |
| Surface Container Lowest | #ffffff | Cards and modals |
| On Surface | #2d3435 | Primary text |
| On Surface Variant | #70767a | Secondary text |

---

## Project Structure

```
src/main/
├── java/edu/atu/healthlog/studenthealthweatherlog/
│   ├── HelloApplication.java         # Application launcher
│   ├── MainController.java            # Main shell with navigation
│   ├── DashboardController.java       # Dashboard metrics view
│   ├── AddLogController.java          # Log entry form
│   ├── HistoryController.java         # History and analytics
│   ├── WeatherInsightsController.java # Weather correlations
│   └── SettingsController.java        # User preferences
│
└── resources/edu/atu/healthlog/studenthealthweatherlog/
    ├── styles.css                     # Design system stylesheet
    ├── main-view.fxml                 # Application shell
    ├── dashboard-view.fxml            # Dashboard screen
    ├── add-log-view.fxml              # Log entry form
    ├── history-view.fxml              # History screen
    ├── weather-insights-view.fxml     # Weather analytics
    └── settings-view.fxml             # Settings screen
```

---

## Screen Documentation

### 1. **Dashboard Screen** (`dashboard-view.fxml` + `DashboardController.java`)

**Purpose**: Main landing page showing daily wellness metrics and insights

**Structure**:
- **Header Section**: Personalized greeting with user name
- **Left Column**: Metric cards displaying:
  - Today's mood (with status trend)
  - Sleep duration with progress bar
  - Water intake and exercise stats
  - Activity trends chart placeholder
- **Right Column**: 
  - Weather widget (location, condition, temperature, details)
  - Weekly streak tracker
  - Wellness tips
  - Motivational quote

**Key Components**:
- `fx:id="moodLabel"` - Current mood display
- `fx:id="sleepLabel"` - Sleep duration
- `fx:id="sleepProgressBar"` - Visual progress indicator
- `fx:id="waterLabel"` - Water intake metric
- `fx:id="weatherConditionLabel"` - Weather condition
- `fx:id="weatherTempLabel"` - Temperature display

**Controller Functions**:
- `initialize()` - Loads mock wellness data
- `loadDashboardData()` - Populates all metric fields

**Design Notes**:
- Uses cards with `#ffffff` background on `#f9f9f9` container
- Color-coded metrics with blue accents (`#005faf`)
- Large typography (display-lg) for greeting to establish hierarchy

---

### 2. **Add Log Screen** (`add-log-view.fxml` + `AddLogController.java`)

**Purpose**: Form for users to log daily wellness activities

**Structure**:
- **Header Section**: Screen title and description
- **Left Column**: Form inputs including:
  - 5-option mood selector (emoji-based, selectable)
  - Sleep duration spinner (0-24 hours)
  - Water intake spinner (0-5000ml)
  - Activity type dropdown
  - Exercise duration spinner
  - Save button (full width, primary style)
- **Right Column**:
  - Hydration tip card
  - Weekly streak display
  - Motivational quote

**Key Components**:
- `fx:id="moodLowBtn"` through `moodTiredBtn` - Mood selection buttons
- `fx:id="sleepSpinner"` - Sleep hour input
- `fx:id="waterSpinner"` - Water ml input
- `fx:id="activityComboBox"` - Activity type selector
- `fx:id="durationSpinner"` - Exercise duration input
- `fx:id="saveBtn"` - Submit button

**Controller Functions**:
- `initialize()` - Sets up spinners and combobox
- `setupSpinners()` - Configures spinner value factories
- `setupActivityComboBox()` - Populates activity options
- `selectMood(ActionEvent)` - Handles mood selection with visual feedback
- `resetMoodButtons()` - Resets button styles
- `saveLogEntry()` - Creates LogEntry object and prepares for persistence

**Nested Class**:
- `LogEntry` - Data class storing mood, sleep, water, activity, duration

**Design Notes**:
- Mood buttons use `#f2f4f4` default, `#d4e3ff` with border when selected
- Spinners are configured with min/max values and step increments
- Save button uses primary color with full-width responsive layout

---

### 3. **History Screen** (`history-view.fxml` + `HistoryController.java`)

**Purpose**: Display historical wellness data with filtering and export options

**Structure**:
- **Header Section**: Screen title and description
- **Filter Bar**: Date range pickers, filter button, export CSV button
- **Left Column**:
  - Monthly summary card with key statistics:
    - Average sleep, mood, hydration
  - Data table showing:
    - Date, Mood, Sleep, Water, Exercise, Weather columns
  - Pagination controls
- **Right Column**:
  - Weather correlation insight card
  - Trend analysis (positive/negative indicators)
  - Calendar sync integration
  - Support links

**Key Components**:
- `fx:id="startDatePicker"` - Date range start
- `fx:id="endDatePicker"` - Date range end
- `fx:id="filterBtn"` - Apply filters button
- `fx:id="exportCsvBtn"` - Export data button
- `fx:id="historyTable"` - Data table for entries
- `fx:id="viewDeepInsightsBtn"` - Analytics button
- `fx:id="syncCalendarBtn"` - Calendar sync button

**Controller Functions**:
- `initialize()` - Loads history data
- `loadHistoryData()` - Populates table with mock entries
- `applyFilters()` - Applies date and filter criteria
- `exportCSV()` - Exports table to CSV format
- `viewDeepInsights()` - Opens detailed analysis
- `syncCalendar()` - Syncs with calendar app

**Nested Class**:
- `HistoryEntry` - Represents a single log entry with getters

**Design Notes**:
- Table uses `#f2f4f4` header background
- Summary card shows positive/negative trends with colored text
- Pagination buttons with active state styling
- Footer text in secondary color (`#70767a`)

---

### 4. **Weather Insights Screen** (`weather-insights-view.fxml` + `WeatherInsightsController.java`)

**Purpose**: Display correlations between weather and wellness metrics

**Structure**:
- **Header**: Screen title
- **Left Column**:
  - Mood Impact by Condition chart:
    - 30-day analysis
    - Bar chart showing correlation levels
  - Two side-by-side charts:
    - Humidity vs. Energy correlation
    - Weather Forecast Integration
- **Right Column**:
  - Wellness Insight card (blue background, actionable)
  - Correlated Metrics display:
    - Sleep Quality (+12%)
    - Activity Level (+18%)
    - Anxiety Frequency (-08%)
  - Quick action buttons

**Key Components**:
- `fx:id="syncCalendarBtn"` - Sync calendar button
- `fx:id="viewGoalsBtn"` - View sunlight goals button
- Chart placeholders with mock visualization data

**Controller Functions**:
- `initialize()` - Loads correlation data
- `loadWeatherInsights()` - Fetches weather/wellness data
- `switchToMonthlyView()` - Changes chart time period
- `openFilter()` - Opens filter dialog
- `viewGoals()` - Shows sunlight goals screen
- `syncCalendar()` - Calendar synchronization

**Nested Class**:
- `WeatherCorrelation` - Data class for weather/wellness metrics

**Design Notes**:
- Chart area uses `#f2f4f4` background with subtle padding
- Percentage changes color-coded:
  - Positive (red) = increase in negative metric
  - Green = decrease in negative metric
  - Blue = positive change
- Insight card uses light blue (`#d4e3ff`) with border

---

### 5. **Settings Screen** (`settings-view.fxml` + `SettingsController.java`)

**Purpose**: User preferences, account management, and app configuration

**Structure**:
- **Header**: Screen title
- **Left Column**:
  - Account Settings:
    - Profile info with edit button
    - Password change option
  - Notification Settings:
    - Daily reminders toggle + time
    - Weather alerts toggle
    - Weekly insights toggle
    - Goal reminders toggle
  - Wellness Goals:
    - Target sleep (4-12 hours)
    - Target water intake (1000-5000ml)
    - Target exercise (0-240 mins/day)
  - Privacy & Data:
    - Data sharing checkbox
    - Data export button
    - Account deletion button
  - Save Changes button
- **Right Column**:
  - About section (version, build, update date)
  - Support options (contact, guide, terms, privacy)
  - Appearance theme selector (light/dark/system)

**Key Components**:
- `fx:id="dailyReminders"` - Daily reminder checkbox
- `fx:id="weatherAlerts"` - Weather alert checkbox
- `fx:id="weeklyInsights"` - Weekly insights checkbox
- `fx:id="goalReminders"` - Goal reminder checkbox
- `fx:id="targetSleepSpinner"` - Sleep goal spinner
- `fx:id="targetWaterSpinner"` - Water goal spinner
- `fx:id="targetExerciseSpinner"` - Exercise goal spinner
- `fx:id="dataSharing"` - Data sharing checkbox
- `fx:id="saveBtn"` - Save all changes

**Controller Functions**:
- `initialize()` - Sets up spinners and loads settings
- `setupSpinners()` - Configures goal spinners
- `loadSettings()` - Loads user preferences (mock)
- `editProfile()` - Opens profile editor
- `changePassword()` - Opens password dialog
- `exportData()` - Exports user data
- `deleteAccount()` - Deletes account with confirmation
- `saveChanges()` - Persists all settings changes
- `contactSupport()` - Opens support contact
- `openUserGuide()` - Shows user guide
- `openTerms()` - Shows terms of service
- `openPrivacy()` - Shows privacy policy

**Nested Class**:
- `UserSettings` - Configuration class with getters/setters

**Design Notes**:
- Uses `Separator` elements for visual division
- Toggle options show time/state next to checkbox
- Danger actions (delete) shown in red (`#d32f2f`)
- Settings are grouped in logical card sections

---

## Main Application Shell

### Main View FXML (`main-view.fxml` + `MainController.java`)

**Purpose**: Application container with BorderPane layout and navigation

**Structure**:
- **TOP**: Header bar with:
  - "Wellness Sanctuary" title
  - Search field
  - Notification and settings buttons
  - Greeting with user profile circle
- **LEFT**: Sidebar navigation with:
  - Logo and tagline ("HEALTH & WELLNESS")
  - Navigation buttons:
    - Dashboard
    - Add Log
    - History
    - Weather Insights
  - Spacer
  - Bottom buttons:
    - Support
    - Sign Out
- **CENTER**: StackPane for dynamic screen content

**Key Components**:
- `fx:id="mainBorderPane"` - Root layout
- `fx:id="contentArea"` - Screen content container
- `fx:id="searchField"` - Search input
- `fx:id="dashboardBtn"` through `weatherInsightsBtn` - Navigation buttons
- `fx:id="supportBtn"`, `signOutBtn` - Bottom options

**Controller Functions**:
- `initialize()` - Loads dashboard by default
- `switchToDashboard()` - Loads dashboard view
- `switchToAddLog()` - Loads add log view
- `switchToHistory()` - Loads history view
- `switchToWeatherInsights()` - Loads weather insights view
- `openSettings()` - Loads settings view
- `openSupport()` - Opens support (TODO)
- `signOut()` - Signs out user (TODO)
- `loadScreen(String)` - Generic screen loader with error handling
- `updateNavigation(Button)` - Updates active nav button styling

**Design Notes**:
- Sidebar uses `#f2f4f4` background
- Navigation buttons are transparent with hover state text color change
- Active button shown in primary blue (`#005faf`) with bold weight
- Search field uses `#e4e9ea` background with focus underline

---

## Stylesheet Documentation (`styles.css`)

The stylesheet implements the complete design system with the following sections:

### 1. **Color Palette** (CSS Variables)
Defines all design tokens as CSS variables for consistent theming

### 2. **Typography Classes**
- `.headline-lg`, `.headline-md`, `.headline-sm` - Manrope headlines
- `.title-lg`, `.title-md` - Inter titles
- `.body-lg`, `.body-md` - Body text
- `.label-sm` - Small labels (uppercase)

### 3. **Component Styles**
- `.button-primary` - Primary action buttons
- `.button-secondary` - Secondary buttons
- `.card` - Content cards with padding and radius
- `.text-field`, `.combo-box` - Input field styling
- `.sidebar` - Sidebar container
- `.nav-item`, `.nav-item-active` - Navigation styling
- `.header` - Header bar styling

### 4. **Specialized Components**
- `.wellness-tip` - Tip card styling
- `.mood-chip` - Mood selector buttons
- `.floating-action-button` - FAB styling
- `.metric-card` - Metric display cards
- `.progress-bar` - Progress indicator styling

### 5. **Layout Utilities**
- `.spacing-sm`, `.spacing-md`, `.spacing-lg`, `.spacing-xl` - VBox/HBox spacing
- `.background-light`, `.background-card` - Background color utilities
- `.text-fill-primary`, `.text-fill-secondary` - Text color utilities

---

## Data Models

### LogEntry (AddLogController.java)
```java
public class LogEntry {
    private String mood;      // "Low", "Neutral", "Good", "Great", "Tired"
    private int sleep;        // 0-24 hours
    private int water;        // 0-5000 ml
    private String activity;  // Activity type selected
    private int duration;     // 0-240 minutes
}
```

### HistoryEntry (HistoryController.java)
```java
public class HistoryEntry {
    private String date;      // Date of entry
    private String mood;      // Mood recorded
    private String sleep;     // Sleep duration
    private String water;     // Water intake
    private String exercise;  // Exercise type
    private String weather;   // Weather condition
}
```

### WeatherCorrelation (WeatherInsightsController.java)
```java
public class WeatherCorrelation {
    private String weatherCondition;  // "Sunny", "Cloudy", etc.
    private int moodImpact;           // Correlation score
    private int sleepQuality;         // Sleep impact
    private int activityLevel;        // Activity impact
    private int anxietyFrequency;     // Anxiety impact
}
```

### UserSettings (SettingsController.java)
```java
public class UserSettings {
    private boolean dailyReminders;
    private boolean weatherAlerts;
    private boolean weeklyInsights;
    private boolean goalReminders;
    private int targetSleep;    // 4-12 hours
    private int targetWater;    // 1000-5000 ml
    private int targetExercise; // 0-240 mins/day
    private boolean dataSharing;
}
```

---

## Implementation Status

### ✅ Completed
- All 5 FXML screen layouts
- 7 controller classes (Main + 6 screens)
- Complete stylesheet with design system
- Navigation system with screen switching
- Mock data loading for dashboard
- Form inputs and controls
- UI component styling

### 📝 TODO / Future Enhancements
- **Data Persistence**: Connect to database or file system
- **Chart Integration**: Implement actual data visualizations (using ChartsJS or similar)
- **Weather API**: Integrate real weather data
- **Notifications**: Implement reminder and alert system
- **Authentication**: Add login/registration screens
- **Export Functionality**: Implement CSV/PDF export
- **Dark Mode**: Add dark theme stylesheet
- **Responsive Layout**: Optimize for different screen sizes
- **Data Validation**: Add input validation and error handling
- **Testing**: Create unit and integration tests

---

## Running the Application

1. **Compile and run**:
   ```bash
   cd student-health-weather-log
   mvn clean package
   mvn javafx:run
   ```

2. **Initial Screen**: Dashboard is loaded by default

3. **Navigation**: Click sidebar buttons to switch between screens

---

## Design Principles Applied

### "The Digital Sanctuary" Philosophy

1. **Negative Space**: Every card and section has breathing room
   - 1.5em padding in cards
   - 2em spacing between sections
   - Large white space around typography

2. **No-Line Rule**: No 1px borders used
   - Boundaries defined by background color shifts
   - Tonal hierarchy through surface containers
   - Optional: Ghost borders at 15% opacity if accessibility required

3. **Glassmorphism**: Floating elements have subtle depth
   - Cards use white background on light grey container
   - Primary color (#005faf) adds visual hierarchy
   - Hover states use darker primary (#00539a)

4. **Typography Hierarchy**: Clear visual distinction
   - Manrope headlines: Large, bold, impactful (32px for display-lg)
   - Inter body text: Clear, readable, functional (14px standard)
   - Extreme scale contrast creates editorial feel

5. **Color Restraint**: Limited palette for premium feel
   - Primary action: One blue (#005faf)
   - Neutral backgrounds: Greys (#f9f9f9 to #2d3435)
   - No rainbow of colors—focus on calmness

---

## Troubleshooting

### Screens Not Loading
- Verify FXML filenames match controller resource references
- Check fx:id attributes match controller field names
- Ensure styles.css is in the same resources directory

### Spinners Not Working
- Verify SpinnerValueFactory is initialized in controller
- Check min/max values are appropriate
- Ensure spinner type (Integer/Double) matches field

### Buttons Not Responding
- Verify onAction handler methods exist in controller
- Check controller is properly set in FXML with `fx:controller`
- Ensure method signatures match ActionEvent parameter

### Styling Not Applied
- Verify styles.css path is correct in HelloApplication
- Check CSS class names match FXML styleClass attributes
- Ensure CSS variables are properly defined in :root

---

## File Reference Quick Guide

| File | Type | Purpose |
|------|------|---------|
| HelloApplication.java | Controller | Application entry point |
| MainController.java | Controller | Navigation and screen switching |
| DashboardController.java | Controller | Dashboard metrics display |
| AddLogController.java | Controller | Log entry form handling |
| HistoryController.java | Controller | History table and analytics |
| WeatherInsightsController.java | Controller | Weather correlation analysis |
| SettingsController.java | Controller | User preferences management |
| main-view.fxml | View | Application shell layout |
| dashboard-view.fxml | View | Dashboard screen layout |
| add-log-view.fxml | View | Log form layout |
| history-view.fxml | View | History table layout |
| weather-insights-view.fxml | View | Analytics layout |
| settings-view.fxml | View | Settings form layout |
| styles.css | Style | Design system stylesheet |

---

## Next Steps

1. **Implement Database**: Create data models and persistence layer
2. **Add Charts**: Integrate real data visualization
3. **Weather API**: Connect to weather service
4. **User Authentication**: Add login/registration screens
5. **Testing**: Create comprehensive test suite
6. **Deployment**: Package as executable JAR or native image

---

**Design System Version**: 1.0  
**Implementation Date**: April 5, 2026  
**Last Updated**: April 5, 2026

