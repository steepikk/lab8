module client {
    requires javafx.fxml;
    requires javafx.controls;
    requires org.apache.commons.lang3;
    requires com.google.common;
    requires common;
    requires commons.validator;
    exports gui.managers;

    opens main;
    opens gui.managers to javafx.fxml;
}