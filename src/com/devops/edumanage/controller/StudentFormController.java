package com.devops.edumanage.controller;

import com.devops.edumanage.db.Database;
import com.devops.edumanage.model.Student;
import com.devops.edumanage.view.tm.StudentTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;

public class StudentFormController {
    public TextField txtStudentId;
    public TextField txtFullName;
    public DatePicker txtDOB;
    public TextField txtAddress;
    public TextField txtSearchHere;

    public AnchorPane context;
    public TableView<StudentTm> tblStudent;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colDob;
    public TableColumn colAddress;
    public TableColumn colOption;

    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("button"));
        setStudentId();
        setTableData();
    }

    private void setTableData() {
        ObservableList<StudentTm> obList = FXCollections.observableArrayList();
        for (Student st:Database.studentTable) {
            Button btn = new Button("Delete");
            StudentTm tm = new StudentTm(
                    st.getStdId(),
                    st.getFullName(),
                    new SimpleDateFormat("yyyy-MM-dd").format(st.getDob()),
                    st.getAddress(),
                    btn
            );
            obList.add(tm);
        }
        tblStudent.setItems(obList);
    }

    private void setStudentId() {
        if (!Database.studentTable.isEmpty()){
            Student lastStudent = Database.studentTable.get(
                    Database.studentTable.size()-1
            );
            String lastId = lastStudent.getStdId();
            String[] splitData = lastId.split("-");
            String lastIntegerASString = splitData[1];
            int lastIntegerASInt = Integer.parseInt(lastIntegerASString);
            lastIntegerASInt++;

            String generatedStdId = "S-"+lastIntegerASInt;
            txtStudentId.setText(generatedStdId);
        }else{
            txtStudentId.setText("S-1");
        }
    }

    public void backToHomeOnAction(ActionEvent actionEvent) throws IOException {
        setUi("DashBoardForm");
    }

    public void addNewStudentOnAction(ActionEvent actionEvent) {
    }

    public void saveStudentOnAction(ActionEvent actionEvent) {
        Student student = new Student(
                txtStudentId.getText(),
                txtFullName.getText(),
                Date.from(txtDOB.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                txtAddress.getText()
        );
        Database.studentTable.add(student);
        new Alert(Alert.AlertType.INFORMATION,"Student Saved!").show();
        setStudentId();
        clear();
        System.out.println(student.toString());
        setTableData();
    }

    private void clear(){
        txtDOB.setValue(null);
//        txtFullName.setText("");
        txtFullName.clear();
        txtAddress.clear();
    }

    public void searchHereOnAction(ActionEvent actionEvent) {
    }

    private void setUi(String location) throws IOException {
        Stage stage = (Stage) context.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/"+location+".fxml"))));
    }
}
