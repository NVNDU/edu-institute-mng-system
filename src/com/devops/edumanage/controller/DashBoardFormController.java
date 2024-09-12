package com.devops.edumanage.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DashBoardFormController {
    public Label lblTime;
    public Label lblDate;
    public AnchorPane context;

    public void initialize(){
        setDate();
    }

    private void setDate() {
//        Date date = new Date();//util
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); //2024-09-12
//        String textDate = dateFormat.format(date);
//        lblDate.setText(textDate);
        lblDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
//        lblTime.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        e->{
                            DateTimeFormatter dateTimeFormatter =
                                DateTimeFormatter.ofPattern("hh:mm:ss");
                            lblTime.setText(LocalTime.now().format(dateTimeFormatter));
                        }
                ),
                new KeyFrame(Duration.seconds(1))
        );
    timeline.setCycleCount(Animation.INDEFINITE);
    timeline.play();
    }

    public void logoutOnAction(ActionEvent actionEvent) throws IOException {
        setUi("LoginForm");
    }

    public void openStudentFormOnAction(ActionEvent actionEvent) throws IOException {
        setUi("StudentForm");
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
        stage.centerOnScreen();
    }
}
