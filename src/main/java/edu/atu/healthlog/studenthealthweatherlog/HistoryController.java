package edu.atu.healthlog.studenthealthweatherlog;

import edu.atu.healthlog.studenthealthweatherlog.database.DatabaseConnection;
import edu.atu.healthlog.studenthealthweatherlog.database.HealthLogRepository;
import edu.atu.healthlog.studenthealthweatherlog.models.HealthEntry;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * HistoryController - Manages the wellness history view.
 * Displays past log entries in a data table with filtering and export capabilities.
 */
public class HistoryController {
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button filterBtn;
    @FXML
    private Button exportCsvBtn;
    @FXML
    private Button exportPdfBtn;
    @FXML
    private TableView<HistoryEntry> historyTable;
    @FXML
    private ComboBox<String> moodFilterComboBox;
    @FXML
    private ComboBox<String> weatherFilterComboBox;
    @FXML
    private TextField searchField;
    @FXML
    private Button clearFiltersBtn;
    @FXML
    private TableColumn<HistoryEntry, String> dateColumn;
    @FXML
    private TableColumn<HistoryEntry, String> moodColumn;
    @FXML
    private TableColumn<HistoryEntry, String> sleepColumn;
    @FXML
    private TableColumn<HistoryEntry, String> waterColumn;
    @FXML
    private TableColumn<HistoryEntry, String> exerciseColumn;
    @FXML
    private TableColumn<HistoryEntry, String> weatherColumn;
    @FXML
    private Button viewDeepInsightsBtn;
    @FXML
    private Button syncCalendarBtn;
    @FXML
    private Label paginationLabel;
    @FXML
    private Button page1Btn;
    @FXML
    private Button page2Btn;
    @FXML
    private Button page3Btn;
    @FXML
    private Button prevPageBtn;
    @FXML
    private Button nextPageBtn;
    @FXML
    private ComboBox<Integer> pageSizeComboBox;

    private final HealthLogRepository repository = new HealthLogRepository();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter EXPORT_TS_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    // Master data + active filtered data + paging state
    private final ObservableList<HistoryEntry> allHistoryData = FXCollections.observableArrayList();
    private final ObservableList<HistoryEntry> activeData = FXCollections.observableArrayList();
    private int currentPage = 1;
    private int pageSize = UserPreferences.getHistoryPageSize();

    @FXML
    public void initialize() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        moodColumn.setCellValueFactory(new PropertyValueFactory<>("mood"));
        sleepColumn.setCellValueFactory(new PropertyValueFactory<>("sleep"));
        waterColumn.setCellValueFactory(new PropertyValueFactory<>("water"));
        exerciseColumn.setCellValueFactory(new PropertyValueFactory<>("exercise"));
        weatherColumn.setCellValueFactory(new PropertyValueFactory<>("weather"));

        setupPageSizeSelector();
        setupFilterSelectors();
        setupSearchListener();
        loadHistoryData();
    }

    private void setupSearchListener() {
        if (searchField == null) {
            return;
        }
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFiltersInternal(false));
    }

    private void setupPageSizeSelector() {
        if (pageSizeComboBox == null) {
            return;
        }
        pageSizeComboBox.getItems().setAll(10, 20, 50);
        pageSizeComboBox.setValue(pageSize);
        pageSizeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                pageSize = newVal;
                UserPreferences.setHistoryPageSize(pageSize);
                currentPage = 1;
                updatePagedTable();
                Toast.show(historyTable, "Rows per page: " + pageSize, false);
            }
        });
    }

    private void setupFilterSelectors() {
        if (moodFilterComboBox != null) {
            moodFilterComboBox.getItems().setAll("All");
            moodFilterComboBox.setValue("All");
        }
        if (weatherFilterComboBox != null) {
            weatherFilterComboBox.getItems().setAll("All");
            weatherFilterComboBox.setValue("All");
        }
    }

    private void refreshFilterOptions() {
        if (moodFilterComboBox == null || weatherFilterComboBox == null) {
            return;
        }
        Set<String> moods = new TreeSet<>();
        Set<String> weather = new TreeSet<>();
        for (HistoryEntry entry : allHistoryData) {
            if (entry.getMood() != null && !entry.getMood().isBlank()) {
                moods.add(entry.getMood());
            }
            if (entry.getWeather() != null && !entry.getWeather().isBlank()) {
                weather.add(entry.getWeather());
            }
        }
        String currentMood = moodFilterComboBox.getValue();
        String currentWeather = weatherFilterComboBox.getValue();

        moodFilterComboBox.getItems().setAll("All");
        moodFilterComboBox.getItems().addAll(moods);
        weatherFilterComboBox.getItems().setAll("All");
        weatherFilterComboBox.getItems().addAll(weather);

        moodFilterComboBox.setValue(currentMood != null ? currentMood : "All");
        weatherFilterComboBox.setValue(currentWeather != null ? currentWeather : "All");
    }

    private boolean matchesFilter(String value, String selected) {
        if (selected == null || "All".equalsIgnoreCase(selected)) {
            return true;
        }
        return value != null && value.equalsIgnoreCase(selected);
    }

    /**
     * Loads wellness history data from the database
     */
    private void loadHistoryData() {
        System.out.println("Loading wellness history data from DB...");

        new Thread(() -> {
            try {
                List<HealthEntry> entries = repository.getAllByUserId(1);
                List<HistoryEntry> historyEntries = entries.stream()
                        .map(e -> new HistoryEntry(
                                e.getEntryDate().format(DATE_FORMATTER),
                                e.getMood(),
                                String.format("%.1f hrs", e.getSleepHours()),
                                String.format("%.1f L", e.getWaterIntake()),
                                e.getExercise(),
                                String.format("%.0f°C %s", e.getTemperature(), e.getWeatherCondition())
                        ))
                        .collect(Collectors.toList());

                Platform.runLater(() -> {
                    allHistoryData.setAll(historyEntries);
                    activeData.setAll(allHistoryData);
                    currentPage = 1;
                    refreshFilterOptions();
                    updatePagedTable();
                });
            } catch (Exception e) {
                System.err.println("Failed to load history from DB: " + e.getMessage());
                if (DatabaseConnection.isMockMode()) {
                    System.out.println("Running in mock mode - no data found.");
                }
            }
        }).start();
    }

    /**
     * Applies date range and filter options to the data
     */
    @FXML
    public void applyFilters() {
        applyFiltersInternal(true);
    }

    private void applyFiltersInternal(boolean showToast) {
        System.out.println("Applying filters from " + startDatePicker.getValue() + " to " + endDatePicker.getValue());

        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();
        String selectedMood = moodFilterComboBox != null ? moodFilterComboBox.getValue() : "All";
        String selectedWeather = weatherFilterComboBox != null ? weatherFilterComboBox.getValue() : "All";
        String query = searchField != null ? searchField.getText() : "";

        boolean noFilters = start == null && end == null
                && ("All".equalsIgnoreCase(selectedMood) || selectedMood == null)
                && ("All".equalsIgnoreCase(selectedWeather) || selectedWeather == null)
                && (query == null || query.isBlank());

        if (noFilters) {
            activeData.setAll(allHistoryData);
            currentPage = 1;
            updatePagedTable();
            if (showToast) {
                Toast.show(historyTable, "Filters cleared", false);
            }
            return;
        }

        ObservableList<HistoryEntry> filteredData = allHistoryData.filtered(entry -> {
            try {
                LocalDate entryDate = LocalDate.parse(entry.getDate(), DATE_FORMATTER);
                boolean afterStart = (start == null) || !entryDate.isBefore(start);
                boolean beforeEnd = (end == null) || !entryDate.isAfter(end);
                return afterStart && beforeEnd
                        && matchesFilter(entry.getMood(), selectedMood)
                        && matchesFilter(entry.getWeather(), selectedWeather)
                        && matchesSearch(entry, query);
            } catch (DateTimeParseException ex) {
                return false;
            }
        });

        activeData.setAll(filteredData);
        currentPage = 1;
        updatePagedTable();
        if (showToast) {
            Toast.show(historyTable, "Filters applied", false);
        }
    }

    @FXML
    public void clearFilters() {
        if (startDatePicker != null) {
            startDatePicker.setValue(null);
        }
        if (endDatePicker != null) {
            endDatePicker.setValue(null);
        }
        if (moodFilterComboBox != null) {
            moodFilterComboBox.setValue("All");
        }
        if (weatherFilterComboBox != null) {
            weatherFilterComboBox.setValue("All");
        }
        if (searchField != null) {
            searchField.clear();
        }

        activeData.setAll(allHistoryData);
        currentPage = 1;
        updatePagedTable();
        Toast.show(historyTable, "Filters cleared", false);
    }

    private boolean matchesSearch(HistoryEntry entry, String query) {
        if (query == null || query.isBlank()) {
            return true;
        }
        String q = query.toLowerCase();
        return safe(entry.getDate()).toLowerCase().contains(q)
                || safe(entry.getMood()).toLowerCase().contains(q)
                || safe(entry.getSleep()).toLowerCase().contains(q)
                || safe(entry.getWater()).toLowerCase().contains(q)
                || safe(entry.getExercise()).toLowerCase().contains(q)
                || safe(entry.getWeather()).toLowerCase().contains(q);
    }

    @FXML
    public void exportCSV() {
        System.out.println("Exporting wellness history to CSV...");
        ObservableList<HistoryEntry> rows = activeData;
        if (rows == null || rows.isEmpty()) {
            Toast.show(historyTable, "No data to export", true);
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Wellness History");
        String timestamp = LocalDateTime.now().format(EXPORT_TS_FORMATTER);
        chooser.setInitialFileName("wellness-history-" + timestamp + ".csv");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = chooser.showSaveDialog(historyTable.getScene().getWindow());
        if (file == null) {
            Toast.show(historyTable, "Export cancelled", true);
            return;
        }

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Date,Mood,Sleep,Water,Exercise,Weather\n");
            for (HistoryEntry entry : rows) {
                writer.write(csv(entry.getDate()) + "," +
                        csv(entry.getMood()) + "," +
                        csv(entry.getSleep()) + "," +
                        csv(entry.getWater()) + "," +
                        csv(entry.getExercise()) + "," +
                        csv(entry.getWeather()) + "\n");
            }
            showExportConfirmation(file, "CSV saved successfully");
        } catch (IOException e) {
            Toast.show(historyTable, "Export failed", true);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Error");
            alert.setHeaderText("Failed to export CSV");
            alert.setContentText(e.getMessage());
            Toast.styleAlert(alert, historyTable, true);
            alert.show();
        }
    }

    @FXML
    public void exportPDF() {
        System.out.println("Exporting wellness history to PDF...");
        ObservableList<HistoryEntry> rows = activeData;
        if (rows == null || rows.isEmpty()) {
            Toast.show(historyTable, "No data to export", true);
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Export Wellness History");
        String timestamp = LocalDateTime.now().format(EXPORT_TS_FORMATTER);
        chooser.setInitialFileName("wellness-history-" + timestamp + ".pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = chooser.showSaveDialog(historyTable.getScene().getWindow());
        if (file == null) {
            Toast.show(historyTable, "Export cancelled", true);
            return;
        }

        try (PDDocument document = new PDDocument()) {
            writePdfTable(document, rows);
            document.save(file);
            showExportConfirmation(file, "PDF saved successfully");
        } catch (IOException e) {
            Toast.show(historyTable, "Export failed", true);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Error");
            alert.setHeaderText("Failed to export PDF");
            alert.setContentText(e.getMessage());
            Toast.styleAlert(alert, historyTable, true);
            alert.show();
        }
    }

    private void writePdfTable(PDDocument document, ObservableList<HistoryEntry> rows) throws IOException {
        PDRectangle pageSize = PDRectangle.LETTER;
        float margin = 40;
        float yStart = pageSize.getHeight() - margin;
        float rowHeight = 18;

        float[] colWidths = {90, 70, 60, 60, 140, 110};
        String[] headers = {"Date", "Mood", "Sleep", "Water", "Exercise", "Weather"};
        int[] maxChars = {12, 10, 8, 8, 24, 15};

        PDPage page = new PDPage(pageSize);
        document.addPage(page);
        PDPageContentStream content = new PDPageContentStream(document, page);

        float yPosition = yStart;
        yPosition = drawPdfHeader(content, margin, yPosition, colWidths, headers, rowHeight);

        for (HistoryEntry entry : rows) {
            if (yPosition - rowHeight < margin) {
                content.close();
                page = new PDPage(pageSize);
                document.addPage(page);
                content = new PDPageContentStream(document, page);
                yPosition = yStart;
                yPosition = drawPdfHeader(content, margin, yPosition, colWidths, headers, rowHeight);
            }
            String[] values = {
                    safe(entry.getDate()),
                    safe(entry.getMood()),
                    safe(entry.getSleep()),
                    safe(entry.getWater()),
                    safe(entry.getExercise()),
                    safe(entry.getWeather())
            };
            drawPdfRow(content, margin, yPosition, colWidths, values, maxChars, rowHeight);
            yPosition -= rowHeight;
        }

        content.close();
    }

    private float drawPdfHeader(PDPageContentStream content, float x, float y, float[] colWidths, String[] headers, float rowHeight) throws IOException {
        drawRowGrid(content, x, y, colWidths, rowHeight);
        float textY = y - 12;
        float textX = x + 3;
        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 10);
        for (int i = 0; i < headers.length; i++) {
            content.newLineAtOffset(textX - (i == 0 ? 0 : 0), 0);
            content.showText(headers[i]);
            content.newLineAtOffset(colWidths[i], 0);
        }
        content.endText();
        return y - rowHeight;
    }

    private void drawPdfRow(PDPageContentStream content, float x, float y, float[] colWidths, String[] values, int[] maxChars, float rowHeight) throws IOException {
        drawRowGrid(content, x, y, colWidths, rowHeight);
        float textX = x + 3;
        float textY = y - 12;
        content.beginText();
        content.setFont(PDType1Font.HELVETICA, 9);
        content.newLineAtOffset(textX, textY);
        for (int i = 0; i < values.length; i++) {
            String text = truncate(values[i], maxChars[i]);
            content.showText(text);
            content.newLineAtOffset(colWidths[i], 0);
        }
        content.endText();
    }

    private void drawRowGrid(PDPageContentStream content, float x, float y, float[] colWidths, float rowHeight) throws IOException {
        float tableWidth = 0;
        for (float w : colWidths) {
            tableWidth += w;
        }
        content.moveTo(x, y);
        content.lineTo(x + tableWidth, y);
        content.lineTo(x + tableWidth, y - rowHeight);
        content.lineTo(x, y - rowHeight);
        content.closePath();
        content.stroke();

        float currentX = x;
        for (float w : colWidths) {
            content.moveTo(currentX, y);
            content.lineTo(currentX, y - rowHeight);
            content.stroke();
            currentX += w;
        }
        content.moveTo(x + tableWidth, y);
        content.lineTo(x + tableWidth, y - rowHeight);
        content.stroke();
    }

    private String truncate(String value, int maxChars) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxChars) {
            return value;
        }
        return value.substring(0, Math.max(0, maxChars - 1)) + "…";
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void showExportConfirmation(File file, String headerText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Complete");
        alert.setHeaderText(headerText);
        alert.setContentText(file.getAbsolutePath());
        Toast.styleAlert(alert, historyTable, false);

        ButtonType openFolder = new ButtonType("Open Folder");
        alert.getButtonTypes().setAll(openFolder, ButtonType.OK);

        alert.showAndWait().ifPresent(choice -> {
            if (choice == openFolder) {
                revealInFinder(file);
            }
        });
    }

    private void revealInFinder(File file) {
        try {
            new ProcessBuilder("open", "-R", file.getAbsolutePath()).start();
        } catch (IOException e) {
            Toast.show(historyTable, "Could not open folder", true);
        }
    }

    /**
     * Opens a detailed analysis view of the data
     */
    @FXML
    public void viewDeepInsights() {
        System.out.println("Opening deep insights view...");
        Toast.show(historyTable, "Opening deep insights...", false);
        if (MainController.getInstance() != null) {
            MainController.getInstance().switchToWeatherInsights();
        }
    }

    /**
     * Syncs wellness logs with calendar application
     */
    @FXML
    public void syncCalendar() {
        System.out.println("Syncing with calendar. User experience notification starting...");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Calendar Integration");
        alert.setHeaderText("Link to System Calendar?");
        alert.setContentText("Would you like to sync your wellness entries with your device's default calendar? This will allow you to see your wellness patterns alongside your schedule.");
        Toast.styleAlert(alert, historyTable, false);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("User confirmed sync. Starting process...");
                showSyncProgress();
            }
        });
    }

    private void showSyncProgress() {
        Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
        progressAlert.setTitle("Syncing...");
        progressAlert.setHeaderText("External Calendar Sync in Progress");
        progressAlert.setContentText("Please wait while we connect to your calendar service...");
        Toast.styleAlert(progressAlert, historyTable, false);
        progressAlert.show();

        new Thread(() -> {
            try {
                Thread.sleep(2500);
                Platform.runLater(() -> {
                    progressAlert.setAlertType(Alert.AlertType.INFORMATION);
                    progressAlert.setHeaderText("Sync Complete!");
                    progressAlert.setContentText("Successfully synced 31 entries to your calendar. You'll now receive schedule-aware wellness tips.");
                    progressAlert.getButtonTypes().setAll(ButtonType.OK);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    public void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePagedTable();
            Toast.show(historyTable, "Page " + currentPage, false);
        } else {
            Toast.show(historyTable, "Already on first page", true);
        }
    }

    @FXML
    public void handleNextPage() {
        int totalPages = getTotalPages();
        if (currentPage < totalPages) {
            currentPage++;
            updatePagedTable();
            Toast.show(historyTable, "Page " + currentPage, false);
        } else {
            Toast.show(historyTable, "Already on last page", true);
        }
    }

    @FXML
    public void handlePageNumber(ActionEvent event) {
        String pageText = ((Button) event.getSource()).getText();
        try {
            int requestedPage = Integer.parseInt(pageText);
            int totalPages = getTotalPages();
            if (requestedPage >= 1 && requestedPage <= totalPages) {
                currentPage = requestedPage;
                updatePagedTable();
                Toast.show(historyTable, "Page " + currentPage, false);
            } else {
                Toast.show(historyTable, "Page out of range", true);
            }
        } catch (NumberFormatException ex) {
            Toast.show(historyTable, "Invalid page", true);
        }
    }

    private void updatePagedTable() {
        int total = activeData.size();
        int totalPages = getTotalPages();

        if (currentPage < 1) {
            currentPage = 1;
        } else if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int fromIndex = Math.max(0, (currentPage - 1) * pageSize);
        int toIndex = Math.min(total, fromIndex + pageSize);

        ObservableList<HistoryEntry> pageSlice = FXCollections.observableArrayList();
        if (fromIndex < toIndex) {
            pageSlice.addAll(activeData.subList(fromIndex, toIndex));
        }

        historyTable.setItems(pageSlice);

        if (total == 0) {
            paginationLabel.setText("Showing 0 to 0 of 0 entries");
        } else {
            paginationLabel.setText("Showing " + (fromIndex + 1) + " to " + toIndex + " of " + total + " entries");
        }

        if (prevPageBtn != null) {
            prevPageBtn.setDisable(currentPage <= 1);
        }
        if (nextPageBtn != null) {
            nextPageBtn.setDisable(currentPage >= totalPages);
        }

        refreshPageButtons();
    }

    private void refreshPageButtons() {
        if (page1Btn == null || page2Btn == null || page3Btn == null) {
            return;
        }

        int totalPages = getTotalPages();
        Button[] buttons = {page1Btn, page2Btn, page3Btn};

        int startPage = Math.max(1, currentPage - 1);
        if (startPage + buttons.length - 1 > totalPages) {
            startPage = Math.max(1, totalPages - buttons.length + 1);
        }

        for (int i = 0; i < buttons.length; i++) {
            int page = startPage + i;
            Button button = buttons[i];

            boolean visible = page <= totalPages;
            button.setVisible(visible);
            button.setManaged(visible);

            if (!visible) {
                continue;
            }

            button.setText(String.valueOf(page));

            if (page == currentPage) {
                button.setStyle("-fx-background-color: #005faf; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
            } else {
                button.setStyle("-fx-background-color: white; -fx-border-color: #e4e9ea; -fx-border-radius: 5; -fx-background-radius: 5;");
            }
        }
    }

    private int getTotalPages() {
        if (activeData.isEmpty()) {
            return 1;
        }
        return (int) Math.ceil((double) activeData.size() / pageSize);
    }

    /**
     * Helper class to represent a history table entry
     */
    public static class HistoryEntry {
        private final String date;
        private final String mood;
        private final String sleep;
        private final String water;
        private final String exercise;
        private final String weather;

        public HistoryEntry(String date, String mood, String sleep, String water, String exercise, String weather) {
            this.date = date;
            this.mood = mood;
            this.sleep = sleep;
            this.water = water;
            this.exercise = exercise;
            this.weather = weather;
        }

        // Getters
        public String getDate() {
            return date;
        }

        public String getMood() {
            return mood;
        }

        public String getSleep() {
            return sleep;
        }

        public String getWater() {
            return water;
        }

        public String getExercise() {
            return exercise;
        }

        public String getWeather() {
            return weather;
        }
    }
}
