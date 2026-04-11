package edu.atu.healthlog.studenthealthweatherlog.services;

import edu.atu.healthlog.studenthealthweatherlog.models.Correlation;
import edu.atu.healthlog.studenthealthweatherlog.models.HealthEntry;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CorrelationService — computes wellness-to-weather correlation scores and
 * provides aggregation helpers for chart display.
 *
 * Score formula (result 0–5):
 *   mood       40%  (Low=1, Tired=2, Neutral=3, Good=4, Great=5)
 *   sleep      30%  (ideal = 8 h; capped at 1.0)
 *   water      20%  (ideal = 2.0 L; capped at 1.0)
 *   activity   10%  (any activity other than placeholder = 1.0)
 */
public class CorrelationService {

    // Chart conditions recognised by WeatherInsightsController (order matches bar array)
    private static final String[] CHART_CONDITIONS = {"Sunny", "Cloudy", "Rainy", "Snow", "Partly"};

    // ── public API ────────────────────────────────────────────────────────────

    /**
     * Builds a {@link Correlation} from a saved {@link HealthEntry}.
     * The entry must have a positive id (i.e. already persisted).
     */
    public Correlation createCorrelation(HealthEntry entry) {
        if (entry == null) throw new IllegalArgumentException("HealthEntry cannot be null");

        int moodNum = moodToNumeric(entry.getMoodScore());
        double score = computeScore(entry);
        String condition = normalizeCondition(entry.getWeatherCondition());

        return new Correlation(
                entry.getUserId(),
                entry.getId(),
                entry.getEntryDate(),
                condition,
                entry.getTemperature(),
                moodNum,
                score
        );
    }

    /**
     * Computes a weighted wellness score (0–5) for the given entry.
     */
    public double computeScore(HealthEntry entry) {
        double moodFraction  = moodToNumeric(entry.getMoodScore()) / 5.0;
        double sleepFraction = Math.min(entry.getSleepHours() / 8.0, 1.0);
        double waterFraction = Math.min(entry.getWaterIntake() / 2.0, 1.0);
        double actFraction   = hasActivity(entry.getExercise()) ? 1.0 : 0.0;

        double raw = moodFraction * 0.40
                   + sleepFraction * 0.30
                   + waterFraction * 0.20
                   + actFraction   * 0.10;

        // Round to 2 decimal places and scale to 0–5
        return Math.round(raw * 5.0 * 100.0) / 100.0;
    }

    /**
     * Returns the average correlation score per chart condition.
     * The map is keyed by one of: Sunny, Cloudy, Rainy, Snow, Partly.
     * Conditions with no data return -1.0 (caller should fall back to default height).
     */
    public Map<String, Double> averageScoreByCondition(List<Correlation> correlations) {
        Map<String, Double> sum   = new LinkedHashMap<>();
        Map<String, Integer> count = new LinkedHashMap<>();

        for (String cond : CHART_CONDITIONS) {
            sum.put(cond, 0.0);
            count.put(cond, 0);
        }

        for (Correlation c : correlations) {
            String key = normalizeCondition(c.getWeatherCondition());
            if (sum.containsKey(key)) {
                sum.put(key, sum.get(key) + c.getCorrelationScore());
                count.put(key, count.get(key) + 1);
            }
        }

        Map<String, Double> result = new LinkedHashMap<>();
        for (String cond : CHART_CONDITIONS) {
            int n = count.get(cond);
            result.put(cond, n > 0 ? Math.round(sum.get(cond) / n * 100.0) / 100.0 : -1.0);
        }
        return result;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    /**
     * Maps a mood string to a numeric value (1–5).
     */
    public int moodToNumeric(String mood) {
        if (mood == null) return 3;
        return switch (mood.trim()) {
            case "Low"     -> 1;
            case "Tired"   -> 2;
            case "Neutral" -> 3;
            case "Good"    -> 4;
            case "Great"   -> 5;
            default        -> 3;
        };
    }

    /**
     * Normalises an OpenWeatherMap condition string (or a user-entered value)
     * to one of the five chart buckets: Sunny, Cloudy, Rainy, Snow, Partly.
     */
    public String normalizeCondition(String raw) {
        if (raw == null || raw.isBlank()) return "Partly";
        String lower = raw.toLowerCase();
        if (lower.contains("clear") || lower.contains("sunny"))           return "Sunny";
        if (lower.contains("cloud") && !lower.contains("partly"))         return "Cloudy";
        if (lower.contains("rain") || lower.contains("drizzle")
                || lower.contains("thunder") || lower.contains("storm"))  return "Rainy";
        if (lower.contains("snow") || lower.contains("sleet")
                || lower.contains("hail") || lower.contains("blizzard"))  return "Snow";
        return "Partly"; // default: partly cloudy / mist / haze / fog
    }

    private boolean hasActivity(String exercise) {
        if (exercise == null || exercise.isBlank()) return false;
        return !exercise.startsWith("Select Activity Type");
    }
}
