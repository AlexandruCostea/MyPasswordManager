module com.alexcostea.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens com.alexcostea.passwordmanager to javafx.fxml;
    exports com.alexcostea.passwordmanager;
    exports com.alexcostea.passwordmanager.Controller;
    exports com.alexcostea.passwordmanager.Service;
    exports com.alexcostea.passwordmanager.Domain;
    opens com.alexcostea.passwordmanager.Controller to javafx.fxml;
}