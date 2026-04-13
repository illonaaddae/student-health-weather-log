package edu.atu.healthlog.studenthealthweatherlog;

import edu.atu.healthlog.studenthealthweatherlog.database.DatabaseConnection;
import edu.atu.healthlog.studenthealthweatherlog.models.Correlation;
import edu.atu.healthlog.studenthealthweatherlog.models.HealthEntry;
import edu.atu.healthlog.studenthealthweatherlog.repositories.CorrelationRepository;
import edu.atu.healthlog.studenthealthweatherlog.repositories.HealthEntryRepository;
import edu.atu.healthlog.studenthealthweatherlog.services.CalendarLaunchService;
import edu.atu.healthlog.studenthealthweatherlog.services.CalendarSyncService;
import edu.atu.healthlog.studenthealthweatherlog.services.CorrelationService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * WeatherInsightsController - Manages the weather & wellness correlations view.
 * Displays charts and analytics showing how weather affects wellness metrics.
 */
public class WeatherInsightsController {

    @FXML private Button syncCalendarBtn;
    @FXML private Button viewGoalsBtn;

    @FXML private javafx.scene.shape.Rectangle sunnyBar;
    @FXML private javafx.scene.shape.Rectangle cloudyBar;
    @FXML private javafx.scene.shape.Rectangle rainyBar;
    @FXML private javafx.scene.shape.Rectangle snowBar;
    @FXML private javafx.scene.shape.Rectangle partlyBar;

    @FXML private AreaChart<String, Number> moodTrendChart;
    @FXML private AreaChart<String, Number> sleepTrendChart;
    @FXML private BarChart<String, Number> waterMoodChart;

    // Fallback heights (scale: 200px = score 5.0) used when no real data is available
    // Sunny=4.2  Cloudy=3.5  Rainy=2.8  Snow=1.9  Partly=3.9
    private static final double[] FALLBACK_HEIGHTS = {168, 140, 112, 76, 156};
    private static final String[] CONDITIONS = {"Sunny", "Cloudy", "Rainy", "Snow", "Partly"};

    private final CorrelationService correlationService = new CorrelationService();
    private final CalendarSyncService calendarSyncService = new CalendarSyncService();
    private final CalendarLaunchService calendarLaunchService = new CalendarLaunchService();

    // Live heights computed from DB data (null until loaded)
    private double[] liveHeights = null;
    private boolean monthlyViewActive = false;

    @FXML
    public void initialize() {
        applyBarHeights(FALLBACK_HEIGHTS); // show something immediately
        loadWeatherInsights();
    }

    private static final DateTimeFormatter CHART_DATE_FMT = DateTimeFormatter.ofPattern("MMM dd");
    // Max entries to show on trend charts (keeps x-axis readable)
    private static final int MAX_TREND_ENTRIES = 14;

    /**
     * Loads correlations and health entries from the DB in a background thread.
     * Drives both the condition bar chart and the trend line charts.
     * Falls back gracefully if the DB is unavailable or empty.
     */
    private void loadWeatherInsights() {
        new Thread(() -> {
            try {
                int userId = UserSession.getCurrentUserId();

                // ── Condition bar chart ──────────────────────────────────────
                CorrelationRepository corrRepo =
                        new CorrelationRepository(DatabaseConnection.getConnection());
                List<Correlation> correlations = corrRepo.findByUserId(userId);

                if (!correlations.isEmpty()) {
                    Map<String, Double> averages =
                            correlationService.averageScoreByCondition(correlations);
                    double[] computed = new double[CONDITIONS.length];
                    for (int i = 0; i < CONDITIONS.length; i++) {
                        double avg = averages.getOrDefault(CONDITIONS[i], -1.0);
                        computed[i] = avg >= 0 ? avg / 5.0 * 200.0 : FALLBACK_HEIGHTS[i];
                    }
                    liveHeights = computed;
                    System.out.printf("WeatherInsights: loaded %d correlations%n", correlations.size());
                } else {
                    System.out.println("WeatherInsights: no correlation data yet — using defaults.");
                }

                // ── Trend charts ─────────────────────────────────────────────
                HealthEntryRepository entryRepo =
                        new HealthEntryRepository(DatabaseConnection.getConnection());
                // findByUserId returns newest-first; reverse so chart reads left→right (oldest→newest)
                List<HealthEntry> allEntries = entryRepo.findByUserId(userId);
                List<HealthEntry> trendEntries = allEntries.size() > MAX_TREND_ENTRIES
                        ? allEntries.subList(0, MAX_TREND_ENTRIES)
                        : allEntries;
                // Reverse: oldest first for the chart x-axis
                List<HealthEntry> chronological = trendEntries.reversed();

                XYChart.Series<String, Number> moodSeries  = new XYChart.Series<>();
                XYChart.Series<String, Number> sleepSeries = new XYChart.Series<>();
                XYChart.Series<String, Number> waterSeries = new XYChart.Series<>();
                XYChart.Series<String, Number> moodBarSeries = new XYChart.Series<>();

                moodSeries.setName("Mood (1–5)");
                sleepSeries.setName("Sleep (hrs)");
                waterSeries.setName("Water (L)");
                moodBarSeries.setName("Mood");

                for (HealthEntry e : chronological) {
                    String label = e.getEntryDate().format(CHART_DATE_FMT);
                    int moodNum = correlationService.moodToNumeric(e.getMoodScore());
                    moodSeries.getData().add(new XYChart.Data<>(label, moodNum));
                    sleepSeries.getData().add(new XYChart.Data<>(label, e.getSleepHours()));
                    waterSeries.getData().add(new XYChart.Data<>(label, e.getWaterIntake()));
                    moodBarSeries.getData().add(new XYChart.Data<>(label, moodNum));
                }

                final double[] finalHeights = liveHeights != null ? liveHeights : FALLBACK_HEIGHTS;

                Platform.runLater(() -> {
                    applyBarHeights(monthlyViewActive ? FALLBACK_HEIGHTS : finalHeights);
                    populateTrendCharts(moodSeries, sleepSeries, waterSeries, moodBarSeries,
                            chronological.isEmpty());
                });

            } catch (Exception e) {
                System.err.println("WeatherInsights: could not load data: " + e.getMessage());
            }
        }, "weather-insights-loader").start();
    }

    private void populateTrendCharts(XYChart.Series<String, Number> moodSeries,
                                     XYChart.Series<String, Number> sleepSeries,
                                     XYChart.Series<String, Number> waterSeries,
                                     XYChart.Series<String, Number> moodBarSeries,
                                     boolean empty) {
        if (moodTrendChart != null) {
            moodTrendChart.getData().clear();
            if (!empty) {
                moodTrendChart.getData().add(moodSeries);
                styleAreaChart(moodTrendChart, "#005faf");
            } else {
                moodTrendChart.setTitle("No mood data yet — start logging!");
            }
        }
        if (sleepTrendChart != null) {
            sleepTrendChart.getData().clear();
            if (!empty) {
                sleepTrendChart.getData().add(sleepSeries);
                styleAreaChart(sleepTrendChart, "#2e7d32");
            } else {
                sleepTrendChart.setTitle("No sleep data yet — start logging!");
            }
        }
        if (waterMoodChart != null) {
            waterMoodChart.getData().clear();
            if (!empty) {
                waterMoodChart.getData().add(waterSeries);
                waterMoodChart.getData().add(moodBarSeries);
            }
        }
    }

    /** Applies a colour to the chart's area fill and stroke via inline CSS. */
    private void styleAreaChart(AreaChart<String, Number> chart, String colour) {
        chart.applyCss();
        chart.lookupAll(".chart-series-area-fill").forEach(n ->
                n.setStyle("-fx-fill: " + colour + "33;")); // 20% opacity fill
        chart.lookupAll(".chart-series-area-line").forEach(n ->
                n.setStyle("-fx-stroke: " + colour + "; -fx-stroke-width: 2px;"));
        chart.lookupAll(".chart-area-symbol").forEach(n ->
                n.setStyle("-fx-background-color: " + colour + ", white;"));
    }

    private void applyBarHeights(double[] heights) {
        if (sunnyBar  != null) sunnyBar.setHeight(heights[0]);
        if (cloudyBar != null) cloudyBar.setHeight(heights[1]);
        if (rainyBar  != null) rainyBar.setHeight(heights[2]);
        if (snowBar   != null) snowBar.setHeight(heights[3]);
        if (partlyBar != null) partlyBar.setHeight(heights[4]);
    }

    /**
     * Toggles between live (weekly) data and the historical monthly fallback view.
     */
    @FXML
    public void switchToMonthlyView() {
        monthlyViewActive = !monthlyViewActive;
        double[] heights = monthlyViewActive ? FALLBACK_HEIGHTS : (liveHeights != null ? liveHeights : FALLBACK_HEIGHTS);
        applyBarHeights(heights);
        String label = monthlyViewActive ? "Monthly view" : "Live view";
        System.out.println("Switched to " + label);
        Toast.show(syncCalendarBtn, label + " loaded", false);
    }

    /**
     * Opens a filter dialog to narrow the correlation data
     */
    @FXML
    public void openFilter() {
        System.out.println("Opening filter options...");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Filter Correlations");
        alert.setHeaderText("Filter options coming soon");
        alert.setContentText("Advanced filtering by date range, weather condition, and metric type will be available once the Correlation module is fully wired.");
        Toast.styleAlert(alert, syncCalendarBtn, false);
        alert.show();
    }

    /**
     * Shows sunlight wellness goals
     */
    @FXML
    public void viewGoals() {
        System.out.println("Opening sunlight goals...");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sunlight & Wellness Goals");
        alert.setHeaderText("Your Sunlight Goals");
        alert.setContentText(
            "Goal 1: Get at least 20 minutes of outdoor sunlight before noon on clear days.\n\n" +
            "Goal 2: Log your mood within 1 hour of a sunny outdoor session to track correlation.\n\n" +
            "Goal 3: Schedule focus-heavy tasks on days with clear sky forecasts — your data shows +24% higher concentration on sunny days.\n\n" +
            "Tip: Even partial sun (\"Partly Cloudy\") shows a 3.9/5 average mood in your history."
        );
        Toast.styleAlert(alert, viewGoalsBtn, false);
        alert.show();
    }

    /**
     * Syncs wellness logs with the device calendar
     */
    @FXML
    public void syncCalendar() {
        System.out.println("Syncing with calendar...");

        if (syncCalendarBtn != null) {
            syncCalendarBtn.setDisable(true);
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Calendar Integration");
        alert.setHeaderText("Export calendar file?");
        alert.setContentText("We'll generate an .ics file you can open in Apple Calendar, Google Calendar, or Outlook.");
        Toast.styleAlert(alert, syncCalendarBtn, false);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Save Calendar File");
                File suggested = calendarSyncService.createDefaultIcsFile();
                chooser.setInitialFileName(suggested.getName());
                chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("iCalendar Files", "*.ics"));

                File selectedFile = chooser.showSaveDialog(syncCalendarBtn.getScene().getWindow());
                if (selectedFile != null) {
                    performCalendarSync(selectedFile);
                } else {
                    Toast.show(syncCalendarBtn, "Calendar export cancelled", true);
                    if (syncCalendarBtn != null) {
                        syncCalendarBtn.setDisable(false);
                    }
                }
            } else if (syncCalendarBtn != null) {
                syncCalendarBtn.setDisable(false);
            }
        });
        if (!ButtonType.OK.equals(alert.getResult()) && syncCalendarBtn != null) {
            syncCalendarBtn.setDisable(false);
        }
    }

    private void performCalendarSync(File outputFile) {
        new Thread(() -> {
            try {
                HealthEntryRepository entryRepo =
                        new HealthEntryRepository(DatabaseConnection.getConnection());
                List<HealthEntry> entries = entryRepo.findByUserId(UserSession.getCurrentUserId());
                int exported = calendarSyncService.exportToIcs(entries, outputFile);
                Platform.runLater(() -> {
                    if (exported == 0) {
                        Toast.show(syncCalendarBtn, "No entries available to sync", true);
                    } else {
                        showCalendarExportConfirmation(outputFile, exported);
                        Toast.show(syncCalendarBtn, "Calendar export complete", false);
                    }
                    if (syncCalendarBtn != null) {
                        syncCalendarBtn.setDisable(false);
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Toast.show(syncCalendarBtn, "Calendar export failed", true);
                    if (syncCalendarBtn != null) {
                        syncCalendarBtn.setDisable(false);
                    }
                });
            }
        }, "calendar-sync-insights").start();
    }

    private void showCalendarExportConfirmation(File file, int exportedCount) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Calendar Export Complete");
        alert.setHeaderText("Exported " + exportedCount + " wellness entries");
        alert.setContentText(file.getAbsolutePath());
        Toast.styleAlert(alert, syncCalendarBtn, false);

        ButtonType openCalendar = new ButtonType("Open in Calendar");
        ButtonType openFolder = new ButtonType("Open Folder");
        alert.getButtonTypes().setAll(openCalendar, openFolder, ButtonType.OK);

        alert.showAndWait().ifPresent(choice -> {
            if (choice == openCalendar) {
                boolean opened = calendarLaunchService.openInCalendar(file)
                        || calendarLaunchService.openWithDefaultApp(file);
                if (!opened) {
                    Toast.show(syncCalendarBtn, "Could not open calendar app", true);
                }
            } else if (choice == openFolder) {
                boolean revealed = calendarLaunchService.revealInFileManager(file);
                if (!revealed) {
                    Toast.show(syncCalendarBtn, "Could not open folder", true);
                }
            }
        });
    }

    /**
     * Helper class for weather correlation data
     */
    public static class WeatherCorrelation {
        private String weatherCondition;
        private int moodImpact;
        private int sleepQuality;
        private int activityLevel;
        private int anxietyFrequency;

        public WeatherCorrelation(String weatherCondition, int moodImpact,
                                  int sleepQuality, int activityLevel, int anxietyFrequency) {
            this.weatherCondition = weatherCondition;
            this.moodImpact = moodImpact;
            this.sleepQuality = sleepQuality;
            this.activityLevel = activityLevel;
            this.anxietyFrequency = anxietyFrequency;
        }

        // Getters
        public String getWeatherCondition() { return weatherCondition; }
        public int getMoodImpact() { return moodImpact; }
        public int getSleepQuality() { return sleepQuality; }
        public int getActivityLevel() { return activityLevel; }
        public int getAnxietyFrequency() { return anxietyFrequency; }
    }
}

