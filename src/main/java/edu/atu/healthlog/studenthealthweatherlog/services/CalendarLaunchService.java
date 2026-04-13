package edu.atu.healthlog.studenthealthweatherlog.services;

import java.io.File;
import java.io.IOException;

/**
 * CalendarLaunchService - handles opening exported calendar files.
 */
public class CalendarLaunchService {

    public boolean isMac() {
        return osName().contains("mac");
    }

    public boolean openInCalendar(File file) {
        if (file == null || !file.exists() || !isMac()) {
            return false;
        }
        return run(new ProcessBuilder("open", "-a", "Calendar", file.getAbsolutePath()));
    }

    public boolean openWithDefaultApp(File file) {
        if (file == null || !file.exists()) {
            return false;
        }

        String os = osName();
        if (os.contains("mac")) {
            return run(new ProcessBuilder("open", file.getAbsolutePath()));
        }
        if (os.contains("win")) {
            return run(new ProcessBuilder("cmd", "/c", "start", "", file.getAbsolutePath()));
        }
        return run(new ProcessBuilder("xdg-open", file.getAbsolutePath()));
    }

    public boolean revealInFileManager(File file) {
        if (file == null || !file.exists()) {
            return false;
        }

        String os = osName();
        if (os.contains("mac")) {
            return run(new ProcessBuilder("open", "-R", file.getAbsolutePath()));
        }
        if (os.contains("win")) {
            return run(new ProcessBuilder("explorer", "/select," + file.getAbsolutePath()));
        }
        File parent = file.getParentFile();
        if (parent == null) {
            return false;
        }
        return run(new ProcessBuilder("xdg-open", parent.getAbsolutePath()));
    }

    private boolean run(ProcessBuilder processBuilder) {
        try {
            processBuilder.start();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String osName() {
        return System.getProperty("os.name", "").toLowerCase();
    }
}

