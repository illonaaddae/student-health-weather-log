CREATE DATABASE IF NOT EXISTS healthlog_db;
USE healthlog_db;

CREATE TABLE IF NOT EXISTS users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(100) NOT NULL,
    email     VARCHAR(255) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    city      VARCHAR(100) NOT NULL DEFAULT 'London'
);

CREATE TABLE IF NOT EXISTS health_entries (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    user_id           INT          NOT NULL,
    entry_date        DATE         NOT NULL,
    mood_score        VARCHAR(50),
    sleep_hours       DOUBLE,
    water_intake      DOUBLE,
    exercise          VARCHAR(255),
    notes             TEXT,
    weather_condition VARCHAR(100),
    temperature       DOUBLE,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- ── Demo users (password for both is: password123) ────────────────────────────
INSERT IGNORE INTO users (user_id, username, email, password, city) VALUES
(1, 'Alice Johnson', 'alice@demo.com',
 '5L03wequmcT1oaONAh+BBA==:PUKFc7Pyq/POugk6nCT4YKQujTnVcOJYELqE4XksHfE=',
 'Accra'),
(2, 'Bob Smith', 'bob@demo.com',
 'gLofua/tI5xS5QykZsIR1Q==:uTaR5l+BbLuKMiJl3PwqmuXS+wjCteqwdje6ke6AtZI=',
 'London');

-- ── Alice's entries (consistent exerciser, good sleeper) ──────────────────────
INSERT IGNORE INTO health_entries (user_id, entry_date, mood_score, sleep_hours, water_intake, exercise, notes, weather_condition, temperature) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 13 DAY), 'Good',    7.5, 2.2, 'Running (30 min)',          'Good start to the week.',          'Sunny',        28.0),
(1, DATE_SUB(CURDATE(), INTERVAL 12 DAY), 'Great',   8.0, 2.5, 'Cycling (45 min)',          'Felt energised all day.',          'Clear',        27.5),
(1, DATE_SUB(CURDATE(), INTERVAL 11 DAY), 'Neutral', 7.0, 2.0, 'Walking (30 min)',          'A bit distracted at work.',        'Partly Cloudy',25.0),
(1, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'Good',    7.8, 2.3, 'Yoga (40 min)',             'Morning yoga made a difference.',  'Sunny',        29.0),
(1, DATE_SUB(CURDATE(), INTERVAL  9 DAY), 'Tired',   5.5, 1.5, 'None (0 min)',              'Late night study session.',        'Cloudy',       23.0),
(1, DATE_SUB(CURDATE(), INTERVAL  8 DAY), 'Neutral', 6.5, 1.8, 'Walking (20 min)',          'Recovering from tired day.',       'Overcast',     22.0),
(1, DATE_SUB(CURDATE(), INTERVAL  7 DAY), 'Good',    8.0, 2.5, 'Running (35 min)',          'Weekend run, felt great.',         'Sunny',        30.0),
(1, DATE_SUB(CURDATE(), INTERVAL  6 DAY), 'Great',   8.5, 3.0, 'Strength Training (60 min)','New personal best at the gym!',   'Clear',        29.5),
(1, DATE_SUB(CURDATE(), INTERVAL  5 DAY), 'Good',    7.5, 2.2, 'Cycling (45 min)',          'Evening ride by the park.',        'Breezy',       26.0),
(1, DATE_SUB(CURDATE(), INTERVAL  4 DAY), 'Great',   8.0, 2.8, 'Running (40 min)',          'Feeling on top of things.',        'Sunny',        28.5),
(1, DATE_SUB(CURDATE(), INTERVAL  3 DAY), 'Good',    7.2, 2.0, 'Yoga (45 min)',             'Midweek reset session.',           'Partly Cloudy',27.0),
(1, DATE_SUB(CURDATE(), INTERVAL  2 DAY), 'Neutral', 7.0, 1.9, 'Walking (25 min)',          'Light day, focused on reading.',   'Cloudy',       24.0),
(1, DATE_SUB(CURDATE(), INTERVAL  1 DAY), 'Good',    7.8, 2.4, 'Cycling (40 min)',          'Good prep for the week ahead.',    'Sunny',        27.5),
(1, CURDATE(),                            'Great',   8.0, 2.5, 'Running (30 min)',          'Best mood all week!',              'Clear',        29.0);

-- ── Bob's entries (less consistent, more varied mood) ─────────────────────────
INSERT IGNORE INTO health_entries (user_id, entry_date, mood_score, sleep_hours, water_intake, exercise, notes, weather_condition, temperature) VALUES
(2, DATE_SUB(CURDATE(), INTERVAL 13 DAY), 'Low',     5.0, 1.0, 'None (0 min)',              'Rough start to the week.',         'Rainy',        15.0),
(2, DATE_SUB(CURDATE(), INTERVAL 12 DAY), 'Neutral', 6.0, 1.5, 'Walking (20 min)',          'Slowly getting back on track.',    'Overcast',     16.0),
(2, DATE_SUB(CURDATE(), INTERVAL 11 DAY), 'Good',    7.0, 2.0, 'Running (25 min)',          'Managed a short run, felt better.','Cloudy',       17.5),
(2, DATE_SUB(CURDATE(), INTERVAL 10 DAY), 'Tired',   5.5, 1.2, 'None (0 min)',              'Deadline pressure at college.',    'Rainy',        14.0),
(2, DATE_SUB(CURDATE(), INTERVAL  9 DAY), 'Neutral', 6.5, 1.8, 'Walking (30 min)',          'Submitted assignment, relieved.',  'Partly Cloudy',18.0),
(2, DATE_SUB(CURDATE(), INTERVAL  8 DAY), 'Good',    7.5, 2.2, 'Cycling (30 min)',          'Weekend ride lifted my mood.',     'Clear',        20.0),
(2, DATE_SUB(CURDATE(), INTERVAL  7 DAY), 'Good',    7.8, 2.5, 'Swimming (45 min)',         'First swim in months, loved it.',  'Sunny',        21.0),
(2, DATE_SUB(CURDATE(), INTERVAL  6 DAY), 'Tired',   5.0, 1.0, 'None (0 min)',              'Back to early morning lectures.',  'Rainy',        13.5),
(2, DATE_SUB(CURDATE(), INTERVAL  5 DAY), 'Neutral', 6.5, 1.5, 'Walking (20 min)',          'Trying to stay consistent.',       'Overcast',     15.5),
(2, DATE_SUB(CURDATE(), INTERVAL  4 DAY), 'Good',    7.0, 2.0, 'Running (30 min)',          'Progress! Third run this week.',   'Cloudy',       17.0),
(2, DATE_SUB(CURDATE(), INTERVAL  3 DAY), 'Great',   8.0, 2.5, 'Strength Training (45 min)','Gym session with friends.',        'Partly Cloudy',19.0),
(2, DATE_SUB(CURDATE(), INTERVAL  2 DAY), 'Good',    7.5, 2.2, 'Cycling (35 min)',          'Feeling more consistent now.',     'Clear',        20.5),
(2, DATE_SUB(CURDATE(), INTERVAL  1 DAY), 'Good',    7.0, 2.0, 'Running (30 min)',          'Good way to end the week.',        'Sunny',        22.0),
(2, CURDATE(),                            'Neutral', 6.5, 1.8, 'Walking (25 min)',           'Lazy Sunday morning walk.',       'Cloudy',       18.0);
