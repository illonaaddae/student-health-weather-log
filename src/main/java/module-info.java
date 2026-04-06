module edu.atu.healthlog.studenthealthweatherlog {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;
    requires java.sql;
    requires com.google.gson;

    opens edu.atu.healthlog.studenthealthweatherlog to javafx.fxml;
    exports edu.atu.healthlog.studenthealthweatherlog;
}