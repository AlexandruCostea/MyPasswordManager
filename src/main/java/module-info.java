module com.alexcostea.passwordmanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.alexcostea.passwordmanager to javafx.fxml;
    exports com.alexcostea.passwordmanager;
    exports com.alexcostea.passwordmanager.Controller;
    opens com.alexcostea.passwordmanager.Controller to javafx.fxml;
}