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
import java.sql.*;
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

        try{
            User selectedUser = login(email);
            if (null != selectedUser){
                if (new PasswordManager().checkPassword(pwd,selectedUser.getPassword())){//selectedUser.get().getPassword().equals(pwd)
                    System.out.println(selectedUser.toString());
                    setUi("DashBoardForm");
                }else{
                    new Alert(Alert.AlertType.WARNING,("Wrong password")).show();
                }
            } else{
                new Alert(Alert.AlertType.WARNING,String.format("user not found (%s)",email)).show();
            }
        }catch (ClassNotFoundException | SQLException e){
            new Alert(Alert.AlertType.WARNING,e.toString()).show();
        }
//        Optional<User> selectedUser = userTable.stream().filter(e -> e.getEmail().equals(email)).findFirst();
//
//        if (selectedUser.isPresent()){
//            if (new PasswordManager().checkPassword(pwd,selectedUser.get().getPassword())){//selectedUser.get().getPassword().equals(pwd)
//                System.out.println(selectedUser.get().toString());
//                setUi("DashBoardForm");
//            }else{
//                new Alert(Alert.AlertType.WARNING,("Wrong password")).show();
//            }
//        }else{
//            new Alert(Alert.AlertType.WARNING,String.format("user not found (%s)",email)).show();
//        }

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

    private User login(String email) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection =
                DriverManager.getConnection("jdbc:mysql://localhost:3306/lms_3","root","1234");
        String sql = "SELECT * FROM user_table WHERE email='"+email+"'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);// SELECT
        if (resultSet.next()){
            User user = new User(
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getString("email"),
                    resultSet.getString(4)
            );
            System.out.println(user);
            return user;
        }
        return null;
    }
}
