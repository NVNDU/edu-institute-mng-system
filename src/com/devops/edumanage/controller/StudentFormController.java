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
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

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
    public Button btn;
    String searchText = "";

    public void initialize(){
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("button"));
        setStudentId();
        setTableData(searchText);

        tblStudent.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable,oldValue,newValue)->{
                if (null != newValue){
                    setData(newValue);
                }
        });

        txtSearchHere.textProperty().addListener((observable, oldValue, newValue )-> {
            setTableData(newValue);
        });
    }

    private void setData(StudentTm tm) {
        txtStudentId.setText(tm.getId());
        txtFullName.setText(tm.getFullName());
        txtAddress.setText(tm.getAddress());
        txtDOB.setValue(LocalDate.parse(tm.getDob(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        btn.setText("Update Student");
    }

    private void setTableData(String searchText) {
        ObservableList<StudentTm> obList = FXCollections.observableArrayList();
        for (Student st:Database.studentTable) {
            if (st.getFullName().contains(searchText)){
                Button btn = new Button("Delete");
                StudentTm tm = new StudentTm(
                        st.getStdId(),
                        st.getFullName(),
                        new SimpleDateFormat("yyyy-MM-dd").format(st.getDob()),
                        st.getAddress(),
                        btn
                );
                btn.setOnAction(e->{
                    Alert alert = new Alert(
                            Alert.AlertType.CONFIRMATION,
                            "Are you sure to delete ?",
                            ButtonType.YES,ButtonType.NO
                    );
                    Optional<ButtonType> buttonType = alert.showAndWait();
                    if (buttonType.get().equals(ButtonType.YES)){
                        Database.studentTable.remove(st);
                        new Alert(Alert.AlertType.INFORMATION,"Deleted !").show();
                        setTableData(searchText);
                        setStudentId();
                    }
                });
                obList.add(tm);
            }
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
        clear();
        setStudentId();
        btn.setText("Save Student");
    }

    public void saveStudentOnAction(ActionEvent actionEvent) {
        if (btn.getText().equalsIgnoreCase("Save Student")){
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
            setTableData(searchText);
        } else{
            for (Student st:Database.studentTable) {
                if (st.getStdId().equals(txtStudentId.getText())){
                    st.setFullName(txtFullName.getText());
                    st.setAddress(txtAddress.getText());
                    st.setDob(Date.from(txtDOB.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    setTableData(searchText);
                    clear();
                    setStudentId();
                    btn.setText("Save Student");
                    return;
                }
            }
            new Alert(Alert.AlertType.WARNING,"Not Found").show();
        }
    }

    private void clear(){
        txtDOB.setValue(null);
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
