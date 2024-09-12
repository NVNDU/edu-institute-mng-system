package com.devops.edumanage.controller;

import com.devops.edumanage.db.Database;
import com.devops.edumanage.model.User;
import com.devops.edumanage.util.security.PasswordManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Optional;

import static com.devops.edumanage.db.Database.userTable;

public class LoginFormController {
    public AnchorPane context;
    public TextField txtEmail;
    public TextField txtPassword;

    public void loginOnAction(ActionEvent actionEvent) throws IOException {
        String email = txtEmail.getText().toLowerCase();
        String pwd = txtPassword.getText().trim();

//        for (User user:Database.userTable) {
//            if (user.getEmail().equals(email)){
//                if (user.getPassword().equals(pwd)){
//                    System.out.println(user.toString());
//                }else{
//                    new Alert(Alert.AlertType.WARNING,("Wrong password")).show();
//                }
//                return;
//            }
//        }

        Optional<User> selectedUser = userTable.stream().filter(e -> e.getEmail().equals(email)).findFirst();

        if (selectedUser.isPresent()){
            if (new PasswordManager().checkPassword(pwd,selectedUser.get().getPassword())){//selectedUser.get().getPassword().equals(pwd)
                System.out.println(selectedUser.get().toString());
                setUi("DashBoardForm");
            }else{
                new Alert(Alert.AlertType.WARNING,("Wrong password")).show();
            }
        }else{
            new Alert(Alert.AlertType.WARNING,String.format("user not found (%s)",email)).show();
        }

    }

    public void createAnAccOnAction(ActionEvent actionEvent) throws IOException {
        setUi("SignupForm");
    }

    public void forgotPasswordOnAction(ActionEvent actionEvent) {
    }

    private void setUi(String location) throws IOException {
//        Window window = context.getScene().getWindow();
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }
}
