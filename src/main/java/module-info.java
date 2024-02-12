module com.alexcostea.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.baseEmpty;
    requires javafx.controlsEmpty;
    requires javafx.fxmlEmpty;
    requires javafx.graphicsEmpty;
    requires com.fasterxml.jackson.databind;
    requires itextpdf;


    opens com.alexcostea.passwordmanager to javafx.fxml;
    exports com.alexcostea.passwordmanager;
    exports com.alexcostea.passwordmanager.Controller;
    exports com.alexcostea.passwordmanager.Service;
    exports com.alexcostea.passwordmanager.Domain;
    opens com.alexcostea.passwordmanager.Controller to javafx.fxml;
}