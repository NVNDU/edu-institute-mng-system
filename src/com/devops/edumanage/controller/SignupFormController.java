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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SignupFormController {
    public AnchorPane context;
    public TextField txtFirstName;
    public TextField txtEmail;
    public TextField txtLastName;
    public TextField txtPassword;



    public void alreadyHaveAnAccOnAction(ActionEvent actionEvent) throws IOException {
        setUi("LoginForm");
    }

    private void setUi(String location) throws IOException {
//        Window window = context.getScene().getWindow();
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }

    public void signupOnAction(ActionEvent actionEvent) throws IOException {
        String email = txtEmail.getText().trim().toLowerCase();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String password = new PasswordManager().encrypt(txtPassword.getText().trim());

//        Database.userTable.add( //userTable is static ArrayList, so it can call from class name "Database"
//                new User(firstName,lastName,email,password)
//        );

        User createUser = new User(firstName, lastName, email, password);

        try{
            boolean isSaved = signup(createUser);
            if (isSaved){
                new Alert(Alert.AlertType.INFORMATION,"Welcome").show();
                setUi("LoginForm");
            }else{
                new Alert(Alert.AlertType.WARNING,"Try Again").show();
            }
        }catch (SQLException | ClassNotFoundException e){
            new Alert(Alert.AlertType.WARNING,e.toString()).show();
        }

    }

    //==============================
//    public boolean signup(String email, String firstName,String lastName, String password){
//
//    }

    public boolean signup(User user) throws ClassNotFoundException, SQLException {
        //load driver
        Class.forName("com.mysql.cj.jdbc.Driver"); //com.mysql.cj.jdbc.Driver - deprecated
        //create connection
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lms_3","root","1234");
        //write a SQL
        String sql = "INSERT INTO user_table values('"+user.getEmail()+"'," +
                "'"+user.getFirstName()+"'," +
                "'"+user.getLastName()+"'," +
                "'"+user.getPassword()+"')";
        //create statement
        Statement statement = connection.createStatement();
        //set sql in to statement and execute
        return statement.executeUpdate(sql) > 0; //INSERT , UPDATE, DELETE
//        return rowCount > 0;
    }
}
