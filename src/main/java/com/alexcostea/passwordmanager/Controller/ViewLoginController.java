package com.alexcostea.passwordmanager.Controller;

import com.alexcostea.passwordmanager.Domain.Login;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewLoginController {

    private final ObservableList<Login> logins;
    private Login login;

    private final String css;

    public ViewLoginController(ObservableList<Login> logins, Login login, String css) {
        this.logins = logins;
        this.login = login;
        this.css = css;
    }

    public void setLogin(Login login) {
        this.login = login;
        this.title.setText(this.login.getTitle());
        this.mailOrUser.setText(this.login.getMailOrUsername());
        this.password.setText(this.login.getPassword());
    }

    @FXML
    public TextField password;

    @FXML
    protected TextField mailOrUser;

    @FXML
    protected TextField title;

    public void updateLogin() {
        Login login = new Login(this.title.getText(), this.mailOrUser.getText(), this.password.getText());
        if(!login.equals(this.login)) {
            this.logins.remove(this.login);
            this.logins.add(login);
            this.setLogin(login);
        }
    }

    public void deleteLogin() {
        Login login = new Login(this.title.getText(), this.mailOrUser.getText(), this.password.getText());
        if(this.logins.contains(login)) {
            Stage stage = new Stage();
            Label label = new Label("Are you sure?");
            Button button1 = new Button("Yes");
            button1.setOnMouseClicked((MouseEvent event) -> {
                if (this.logins.contains(login))
                    this.logins.remove(login);
                stage.close();
            });
            Button button2 = new Button("No");
            button2.setOnMouseClicked((MouseEvent event) -> {
                stage.close();
            });
            HBox layout1 = new HBox();
            layout1.setAlignment(Pos.CENTER);
            layout1.setSpacing(20.0);
            layout1.getChildren().addAll(button1, button2);
            VBox layout2 = new VBox();
            layout2.setAlignment(Pos.CENTER);
            layout2.setSpacing(20.0);
            layout2.getChildren().addAll(label, layout1);
            Scene scene = new Scene(layout2, 200, 100);
            scene.getStylesheets().add(this.css);
            stage.setScene(scene);
            stage.show();
        }
    }
}
