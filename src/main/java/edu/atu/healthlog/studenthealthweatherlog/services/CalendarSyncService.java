package edu.atu.healthlog.studenthealthweatherlog.services;

import edu.atu.healthlog.studenthealthweatherlog.models.HealthEntry;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * CalendarSyncService - exports wellness logs as an iCalendar (.ics) file.
 */
public class CalendarSyncService {
    private static final DateTimeFormatter FILE_TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final DateTimeFormatter ICS_DATE = DateTimeFormatter.BASIC_ISO_DATE;
    private static final DateTimeFormatter ICS_UTC = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

    public File createDefaultIcsFile() {
        String fileName = "wellness-calendar-" + LocalDateTime.now().format(FILE_TS) + ".ics";
        String home = System.getProperty("user.home", ".");
        return new File(home + File.separator + "Downloads", fileName);
    }

    public int exportToIcs(List<HealthEntry> entries, File outputFile) throws IOException {
        if (entries == null || entries.isEmpty()) {
            return 0;
        }

        StringBuilder ics = new StringBuilder();
        ics.append("BEGIN:VCALENDAR\r\n");
        ics.append("VERSION:2.0\r\n");
        ics.append("PRODID:-//StudentHealthWeatherLog//EN\r\n");
        ics.append("CALSCALE:GREGORIAN\r\n");

        String dtStamp = LocalDateTime.now().format(ICS_UTC);
        for (HealthEntry entry : entries) {
            if (entry == null || entry.getEntryDate() == null) {
                continue;
            }

            LocalDate start = entry.getEntryDate();
            LocalDate end = start.plusDays(1);

            String uid = "health-" + entry.getUserId() + "-" + entry.getId() + "-" + start + "@student-health-weather-log";
            String summary = "Wellness Log - " + safe(entry.getMoodScore());
            String description = "Mood: " + safe(entry.getMoodScore())
                    + "\\nSleep: " + entry.getSleepHours() + " hrs"
                    + "\\nWater: " + entry.getWaterIntake() + " L"
                    + "\\nExercise: " + safe(entry.getExercise())
                    + "\\nWeather: " + safe(entry.getWeatherCondition())
                    + "\\nNotes: " + safe(entry.getNotes());

            ics.append("BEGIN:VEVENT\r\n");
            ics.append("UID:").append(escape(uid)).append("\r\n");
            ics.append("DTSTAMP:").append(dtStamp).append("\r\n");
            ics.append("DTSTART;VALUE=DATE:").append(start.format(ICS_DATE)).append("\r\n");
            ics.append("DTEND;VALUE=DATE:").append(end.format(ICS_DATE)).append("\r\n");
            ics.append("SUMMARY:").append(escape(summary)).append("\r\n");
            ics.append("DESCRIPTION:").append(escape(description)).append("\r\n");
            ics.append("END:VEVENT\r\n");
        }

        ics.append("END:VCALENDAR\r\n");

        if (outputFile.getParentFile() != null) {
            File parent = outputFile.getParentFile();
            if (!parent.exists() && !parent.mkdirs()) {
                throw new IOException("Could not create directory: " + parent.getAbsolutePath());
            }
        }
        Files.writeString(outputFile.toPath(), ics.toString(), StandardCharsets.UTF_8);
        return entries.size();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String escape(String value) {
        return safe(value)
                .replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace(",", "\\,")
                .replace("\r", "")
                .replace("\n", "\\n");
    }
}

