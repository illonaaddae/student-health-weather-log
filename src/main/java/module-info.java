module edu.atu.healthlog.studenthealthweatherlog {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.sql;
    requires com.google.gson;
    requires org.apache.pdfbox;
    requires org.apache.fontbox;
    requires java.prefs;

    opens edu.atu.healthlog.studenthealthweatherlog to javafx.fxml;
    opens edu.atu.healthlog.studenthealthweatherlog.models to javafx.base, com.google.gson;
    exports edu.atu.healthlog.studenthealthweatherlog;
    exports edu.atu.healthlog.studenthealthweatherlog.models;
    requires java.sql;
    opens com.kingsley to javafx.fxml;
    exports com.kingsley;
    exports com.kingsley.controllers;
}