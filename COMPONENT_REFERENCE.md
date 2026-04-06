# Component & FX:ID Reference Guide

## MainController (main-view.fxml)

### Navigation Components
```
fx:id="mainBorderPane"          │ BorderPane       │ Root layout container
fx:id="contentArea"             │ StackPane        │ Screen content area
fx:id="dashboardBtn"            │ Button           │ Dashboard navigation
fx:id="addLogBtn"               │ Button           │ Add log navigation
fx:id="historyBtn"              │ Button           │ History navigation
fx:id="weatherInsightsBtn"      │ Button           │ Weather insights navigation
fx:id="supportBtn"              │ Button           │ Support button
fx:id="signOutBtn"              │ Button           │ Sign out button
```

### Header Components
```
fx:id="searchField"             │ TextField        │ Global search input
fx:id="notificationBtn"         │ Button           │ Notifications button
fx:id="settingsBtn"             │ Button           │ Settings button
fx:id="greetingLabel"           │ Label            │ User greeting
```

---

## DashboardController (dashboard-view.fxml)

### Header Section
```
fx:id="welcomeLabel"            │ Label            │ "Good morning, [Name]"
```

### Metrics Cards
```
fx:id="moodLabel"               │ Label            │ Current mood text
fx:id="moodStatusLabel"         │ Label            │ Mood status/trend
fx:id="sleepLabel"              │ Label            │ Sleep hours display
fx:id="sleepProgressBar"        │ ProgressBar      │ Sleep progress indicator
fx:id="waterLabel"              │ Label            │ Water intake amount
fx:id="exerciseLabel"           │ Label            │ Exercise status
```

### Weather Widget
```
fx:id="weatherConditionLabel"   │ Label            │ "Partly Cloudy"
fx:id="weatherTempLabel"        │ Label            │ "68°F"
fx:id="weatherDetailsLabel"     │ Label            │ "42% humidity, 12mph wind"
```

### Insights Section
```
fx:id="wellnessTipLabel"        │ Label            │ Daily wellness tip text
```

---

## AddLogController (add-log-view.fxml)

### Mood Selection
```
fx:id="moodLowBtn"              │ Button           │ Low mood emoji button
fx:id="moodNeutralBtn"          │ Button           │ Neutral mood emoji button
fx:id="moodGoodBtn"             │ Button           │ Good mood emoji button (default)
fx:id="moodGreatBtn"            │ Button           │ Great mood emoji button
fx:id="moodTiredBtn"            │ Button           │ Tired mood emoji button
```

### Form Inputs
```
fx:id="sleepSpinner"            │ Spinner<Integer> │ Sleep duration (0-24 hours)
fx:id="waterSpinner"            │ Spinner<Integer> │ Water intake (0-5000ml)
fx:id="activityComboBox"        │ ComboBox<String> │ Activity type selector
fx:id="durationSpinner"         │ Spinner<Integer> │ Exercise duration (0-240 mins)
```

### Action Buttons
```
fx:id="saveBtn"                 │ Button           │ Save daily entry button
```

---

## HistoryController (history-view.fxml)

### Filter Section
```
fx:id="startDatePicker"         │ DatePicker       │ Start date for range
fx:id="endDatePicker"           │ DatePicker       │ End date for range
fx:id="filterBtn"               │ Button           │ Apply filters
fx:id="exportCsvBtn"            │ Button           │ Export to CSV
```

### Summary Card
```
                                │ (No fx:ids)      │ Monthly summary display
                                │ (Labels only)    │ Shows avg metrics
```

### Data Table
```
fx:id="historyTable"            │ TableView        │ History entries table
                                │ (Columns)        │ DATE, MOOD, SLEEP, WATER,
                                │                  │ EXERCISE, WEATHER
```

### Right Column Buttons
```
fx:id="viewDeepInsightsBtn"     │ Button           │ Open detailed analysis
fx:id="syncCalendarBtn"         │ Button           │ Sync with calendar
```

---

## WeatherInsightsController (weather-insights-view.fxml)

### Chart Controls
```
                                │ (No specific)    │ Mood Impact chart area
                                │ (Labels)         │ Monthly/Weekly toggle
                                │ (Labels)         │ Filter button
```

### Component Buttons
```
fx:id="syncCalendarBtn"         │ Button           │ Sync calendar button
fx:id="viewGoalsBtn"            │ Button           │ View sunlight goals button
```

### Analysis Section
```
                                │ (Labels)         │ Correlated metrics display
                                │ (Labels)         │ Percentage changes
```

---

## SettingsController (settings-view.fxml)

### Account Settings
```
                                │ (Labels/Circle)  │ Profile picture area
                                │ (Labels)         │ Name and email display
                                │ (Button)         │ Edit profile button
                                │ (Button)         │ Change password button
```

### Notification Toggles
```
fx:id="dailyReminders"          │ CheckBox         │ Daily logging reminder
fx:id="weatherAlerts"           │ CheckBox         │ Weather alerts toggle
fx:id="weeklyInsights"          │ CheckBox         │ Weekly insights toggle
fx:id="goalReminders"           │ CheckBox         │ Goal reminder toggle
```

### Wellness Goals
```
fx:id="targetSleepSpinner"      │ Spinner<Integer> │ Target sleep hours (4-12)
fx:id="targetWaterSpinner"      │ Spinner<Integer> │ Target water (1000-5000ml)
fx:id="targetExerciseSpinner"   │ Spinner<Integer> │ Target exercise (0-240 mins)
```

### Privacy Settings
```
fx:id="dataSharing"             │ CheckBox         │ Allow data sharing toggle
```

### Action Buttons
```
fx:id="saveBtn"                 │ Button           │ Save all changes
```

### Support Links (Text Buttons)
```
                                │ (Buttons)        │ Contact support
                                │ (Buttons)        │ Open user guide
                                │ (Buttons)        │ View terms of service
                                │ (Buttons)        │ View privacy policy
```

---

## Naming Conventions Used

### Button Names
- `[screen]Btn` - Navigation buttons (e.g., `dashboardBtn`)
- `[action]Btn` - Action buttons (e.g., `saveBtn`, `editBtn`)
- `[mood]Btn` - Mood selector buttons (e.g., `moodGoodBtn`)

### Input Names
- `[field]Spinner` - Numeric input spinners
- `[field]ComboBox` - Dropdown selectors
- `[field]Field` or `[field]TextField` - Text inputs
- `[field]Picker` - Date/time pickers
- `[field]Checkbox` or `[field]` - Boolean toggles

### Label Names
- `[item]Label` - Display text labels
- `[event]Label` - Event or status labels

### Container Names
- `[section]Pane` - Container controls
- `[screen]View` - Complete screen views

### Table Names
- `[entity]Table` - TableView components

---

## Data Binding Pattern

### Spinner Value Binding
```java
// In controller initialize():
SpinnerValueFactory<Integer> factory = 
    new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue);
mySpinner.setValueFactory(factory);

// When saving:
Integer value = mySpinner.getValue();
```

### ComboBox Binding
```java
// In controller initialize():
myComboBox.getItems().addAll("Option 1", "Option 2", "Option 3");
myComboBox.setValue("Option 1");

// When saving:
String selected = myComboBox.getValue();
```

### CheckBox Binding
```java
// Getting value:
if (myCheckBox.isSelected()) { /* do something */ }

// Setting value:
myCheckBox.setSelected(true);
```

### Label Text Binding
```java
// Setting text:
myLabel.setText("New text value");

// Getting text:
String text = myLabel.getText();
```

---

## Event Handler Methods

### Navigation Methods (MainController)
```java
switchToDashboard()              // Load dashboard screen
switchToAddLog()                 // Load add log screen
switchToHistory()                // Load history screen
switchToWeatherInsights()        // Load weather insights screen
openSettings()                   // Load settings screen
openSupport()                    // Open support dialog (TODO)
signOut()                        // Sign out user (TODO)
```

### Form Methods (AddLogController)
```java
selectMood(ActionEvent)          // Handle mood selection
saveLogEntry()                   // Save form data
setupSpinners()                  // Configure spinners
setupActivityComboBox()          // Populate activity options
resetMoodButtons()               // Reset mood button styles
```

### History Methods (HistoryController)
```java
applyFilters()                   // Apply date/filter criteria
exportCSV()                      // Export table to CSV
viewDeepInsights()               // Open analysis screen
syncCalendar()                   // Sync with calendar
```

### Weather Methods (WeatherInsightsController)
```java
switchToMonthlyView()            // Change chart view
openFilter()                     // Open filter dialog
viewGoals()                      // Show sunlight goals
syncCalendar()                   // Calendar sync
```

### Settings Methods (SettingsController)
```java
editProfile()                    // Open profile editor
changePassword()                 // Open password dialog
exportData()                     // Export user data
deleteAccount()                  // Delete account
saveChanges()                    // Save all settings
contactSupport()                 // Open support contact
openUserGuide()                  // Show user guide
openTerms()                      // Show terms
openPrivacy()                    // Show privacy policy
```

---

## CSS Classes Used

### Layout Classes
```css
.sidebar               /* Sidebar styling */
.header                /* Header bar styling */
.card                  /* Card container styling */
```

### Typography Classes
```css
.headline-lg           /* Large headlines (2rem) */
.headline-md           /* Medium headlines (1.5rem) */
.headline-sm           /* Small headlines (1.25rem) */
.title-lg              /* Large titles (1.125rem) */
.title-md              /* Medium titles (1rem) */
.body-lg               /* Large body text (1rem) */
.body-md               /* Medium body text (0.875rem) */
.label-sm              /* Small labels (0.75rem, uppercase) */
```

### Component Classes
```css
.button-primary        /* Primary blue button */
.button-secondary      /* Secondary light blue button */
.mood-chip             /* Mood selector button */
.nav-item              /* Navigation button */
.nav-item-active       /* Active navigation button */
.wellness-tip          /* Wellness tip card */
.metric-card           /* Metric display card */
.floating-action-button /* FAB styling */
```

### Utility Classes
```css
.spacing-sm            /* VBox spacing: 0.5em */
.spacing-md            /* VBox spacing: 1em */
.spacing-lg            /* VBox spacing: 1.5em */
.spacing-xl            /* VBox spacing: 2em */
.background-light      /* Light background color */
.background-card       /* White card background */
.text-fill-primary     /* Primary text color */
.text-fill-secondary   /* Secondary text color */
```

---

## Component Hierarchy

```
main-view.fxml (BorderPane)
├── TOP: Header (HBox)
│   ├── Title Label
│   ├── Search TextField
│   ├── Notification Button
│   ├── Settings Button
│   ├── Greeting Label
│   └── Profile Circle
│
├── LEFT: Sidebar (VBox)
│   ├── Logo VBox
│   ├── Navigation Buttons
│   │   ├── Dashboard Button
│   │   ├── Add Log Button
│   │   ├── History Button
│   │   └── Weather Insights Button
│   ├── Spacer Region
│   └── Bottom Buttons
│       ├── Support Button
│       └── Sign Out Button
│
└── CENTER: Content (StackPane)
    └── Loaded Screen (ScrollPane)
        └── Screen VBox
```

---

## Adding New Components

### To Add a New Button
```xml
<Button fx:id="myNewBtn" text="My Button" onAction="#myButtonAction" />
```

### To Add a New Label
```xml
<Label fx:id="myNewLabel" text="Initial text" styleClass="body-md" />
```

### To Add a New Spinner
```xml
<Spinner fx:id="myNewSpinner" />
```
Then in controller:
```java
SpinnerValueFactory<Integer> factory = 
    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 50);
myNewSpinner.setValueFactory(factory);
```

### To Add a New Input Field
```xml
<TextField fx:id="myNewField" promptText="Enter something" />
```

---

**Reference Version**: 1.0  
**Date**: April 5, 2026  
**Scope**: Complete component reference

