module com.kingsley {
    requires javafx.controls;
    requires javafx.fxml;
    opens com.kingsley to javafx.fxml;
    exports com.kingsley;
    exports com.kingsley.controllers;
}