# Quick Start Guide - Wellness Sanctuary

## Running the Application

```bash
# Navigate to project directory
cd /Users/illona/Desktop/BeTech/Java/projects/student-health-weather-log

# Build and run
mvn clean package
mvn javafx:run
```

## Project Structure at a Glance

```
student-health-weather-log/
├── src/main/java/.../HelloApplication.java          ← Entry point
├── src/main/java/.../MainController.java            ← Navigation hub
├── src/main/java/.../DashboardController.java       ← Metrics screen
├── src/main/java/.../AddLogController.java          ← Form screen
├── src/main/java/.../HistoryController.java         ← Data table screen
├── src/main/java/.../WeatherInsightsController.java ← Analytics screen
├── src/main/java/.../SettingsController.java        ← Preferences screen
│
├── src/main/resources/.../main-view.fxml            ← App shell
├── src/main/resources/.../dashboard-view.fxml       ← Dashboard
├── src/main/resources/.../add-log-view.fxml         ← Add log form
├── src/main/resources/.../history-view.fxml         ← History table
├── src/main/resources/.../weather-insights-view.fxml ← Analytics
├── src/main/resources/.../settings-view.fxml        ← Settings
├── src/main/resources/.../styles.css                ← Design system
│
├── IMPLEMENTATION.md        ← Full documentation
├── CONVERSION_SUMMARY.md    ← What was created
└── pom.xml                  ← Maven configuration
```

---

## File Mapping: Design → Implementation

| Design Screen | FXML File | Controller | Status |
|---|---|---|---|
| Dashboard (Main) | dashboard-view.fxml | DashboardController | ✅ Complete |
| Add Today's Log | add-log-view.fxml | AddLogController | ✅ Complete |
| Your Wellness History | history-view.fxml | HistoryController | ✅ Complete |
| Weather & Wellness | weather-insights-view.fxml | WeatherInsightsController | ✅ Complete |
| Settings | settings-view.fxml | SettingsController | ✅ Complete |

---

## Key Design Decisions

### Navigation Pattern
- MainController manages all screen switching
- FXMLLoader dynamically loads/unloads screens
- StackPane in center displays current screen
- Sidebar buttons trigger navigation

### Layout System
- BorderPane root for main regions
- VBox/HBox for responsive layouts
- ScrollPane for overflow content
- Region for spacing and layout control

### Styling Approach
- Single stylesheet (styles.css) with CSS variables
- All colors defined in :root
- Component classes for reusability
- No inline styles (except dynamic interactions)

### Form Handling
- Spinners for numeric inputs
- ComboBox for selections
- Event handlers in controllers
- Data collected in controller, not FXML

---

## Color Quick Reference

```css
Primary Actions:        #005faf (blue)
Hover States:          #00539a (darker blue)
Main Background:       #f9f9f9 (light grey)
Card Background:       #ffffff (white)
Sidebar Background:    #f2f4f4 (light grey)
Input Background:      #e4e9ea (medium grey)
Primary Text:          #2d3435 (dark grey)
Secondary Text:        #70767a (medium grey)
Success/Positive:      #4caf50 (green)
Warning/Negative:      #d32f2f (red)
```

---

## Adding a New Screen

1. **Create FXML file** in `src/main/resources/.../new-screen-view.fxml`
   ```xml
   <ScrollPane xmlns="http://javafx.com/javafx/21" 
               xmlns:fx="http://javafx.com/fxml/1"
               fx:controller="...NewScreenController">
       <VBox><!-- Your content --></VBox>
   </ScrollPane>
   ```

2. **Create Controller class** in `src/main/java/.../NewScreenController.java`
   ```java
   public class NewScreenController {
       @FXML
       public void initialize() {
           // Load data or setup UI
       }
   }
   ```

3. **Add navigation button** in `main-view.fxml`
   ```xml
   <Button fx:id="newScreenBtn" text="🎯 New Screen" 
           onAction="#switchToNewScreen" />
   ```

4. **Add method in MainController**
   ```java
   @FXML
   public void switchToNewScreen() {
       loadScreen("new-screen-view.fxml");
       updateNavigation(newScreenBtn);
   }
   ```

---

## Common Modifications

### Changing Colors
Edit `styles.css` :root variables:
```css
.root {
    -primary: #005faf;  /* Change primary color */
    -surface: #f9f9f9;  /* Change background */
}
```

### Adding Form Fields
In controller's `initialize()`:
```java
SpinnerValueFactory<Integer> factory = 
    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 50);
mySpinner.setValueFactory(factory);
```

### Loading Mock Data
In any controller:
```java
private void loadData() {
    myLabel.setText("Some value");
    myTable.setItems(FXCollections.observableArrayList(/*data*/));
}
```

---

## Component Reference

### Text Input
```xml
<TextField fx:id="nameField" promptText="Enter name" />
```

### Numeric Input (Spinner)
```xml
<Spinner fx:id="ageSpinner" initialValue="18" />
```
Then in controller:
```java
SpinnerValueFactory<Integer> factory = 
    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 150, 18);
ageSpinner.setValueFactory(factory);
```

### Dropdown (ComboBox)
```xml
<ComboBox fx:id="typeBox" />
```
Then in controller:
```java
typeBox.getItems().addAll("Option 1", "Option 2", "Option 3");
```

### Toggle (CheckBox)
```xml
<CheckBox fx:id="enabled" text="Enable feature" selected="true" />
```
In controller:
```java
if (enabled.isSelected()) { /* do something */ }
```

### Button
```xml
<Button text="Click me" onAction="#handleClick" />
```
Then in controller:
```java
@FXML
public void handleClick() {
    System.out.println("Clicked!");
}
```

### Label
```xml
<Label fx:id="statusLabel" text="Initial text" />
```
In controller:
```java
statusLabel.setText("Updated text");
```

### Progress Bar
```xml
<ProgressBar fx:id="progress" prefHeight="8" />
```
In controller:
```java
progress.setProgress(0.75); // 0.0 to 1.0
```

### Table
```xml
<TableView fx:id="dataTable" prefHeight="400">
    <columns>
        <TableColumn text="Date" prefWidth="120" />
        <TableColumn text="Value" prefWidth="100" />
    </columns>
</TableView>
```

---

## Styling Components

### Add CSS Class to FXML
```xml
<Button styleClass="button-primary" text="Submit" />
```

### Add Inline Style
```xml
<Label style="-fx-font-size: 1.5em; -fx-text-fill: #005faf;" />
```

### Common Style Properties
```css
-fx-font-size: 1em;           /* Font size */
-fx-text-fill: #000000;        /* Text color */
-fx-background-color: #ffffff; /* Background */
-fx-padding: 1em;              /* Padding */
-fx-spacing: 1em;              /* Item spacing */
-fx-background-radius: 0.5em;  /* Border radius */
-fx-border-color: #000000;     /* Border */
-fx-border-width: 1;           /* Border width */
```

---

## Testing Your Changes

1. **Check Syntax**: Let IDE validate FXML/Java
2. **Compile**: `mvn clean compile`
3. **Run**: `mvn javafx:run`
4. **Navigate**: Click sidebar buttons to test screens
5. **Inspect**: Right-click → Inspect Element (if using SceneBuilder)

---

## Debugging Tips

### View Console Output
Controller print statements appear in IDE console:
```java
System.out.println("Debug: " + value);
```

### Check FXMLLoader Errors
Application will print if FXML doesn't load:
```
Error loading screen: add-log-view.fxml
```

### Verify fx:id Binding
Mismatched fx:id → @FXML field = NullPointerException

### Check Event Handlers
onAction method must exist in controller

---

## Performance Considerations

- **Lazy Loading**: Screens only load when accessed
- **StackPane Clearing**: Previous screen removed from memory
- **Mock Data**: Replace with database queries for large datasets
- **ScrollPane**: Used for overflow, lazy-renders visible content

---

## Security Notes (For Production)

- [ ] Implement authentication/authorization
- [ ] Hash passwords before storage
- [ ] Validate all user inputs
- [ ] Use prepared statements for database queries
- [ ] Encrypt sensitive data
- [ ] Add rate limiting for API calls
- [ ] Implement audit logging

---

## Mobile/Responsive Considerations

Current implementation assumes 1400x900 desktop resolution.

For responsive design:
```xml
<VBox spacing="15" VBox.vgrow="ALWAYS">
    <HBox spacing="20" HBox.hgrow="ALWAYS">
        <!-- Child elements will grow -->
    </HBox>
</VBox>
```

Use `Region` for flexible spacing and `HBox.hgrow`/`VBox.vgrow` for growth.

---

## Resources

- **JavaFX Documentation**: https://openjfx.io/
- **FXML Tutorial**: https://docs.oracle.com/javase/8/javafx/fxml-tutorial/
- **CSS Reference**: https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/css_tutorial.htm
- **Scene Builder**: Visual layout editor (optional)

---

## Support Files

- **IMPLEMENTATION.md** - Complete technical documentation
- **DESIGN.md** - Design system specifications
- **pom.xml** - Maven dependencies and build config

---

**Version**: 1.0  
**Last Updated**: April 5, 2026  
**Status**: Ready for development

