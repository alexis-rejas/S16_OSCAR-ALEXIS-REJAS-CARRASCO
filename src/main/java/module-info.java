module pe.edu.vallegrande.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens pe.edu.vallegrande.app to javafx.fxml;
    opens pe.edu.vallegrande.app.controller to javafx.fxml;
    opens pe.edu.vallegrande.app.model to javafx.base;

    exports pe.edu.vallegrande.app;
}