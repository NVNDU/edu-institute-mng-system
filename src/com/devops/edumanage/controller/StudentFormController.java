package com.devops.edumanage.controller;

import com.devops.edumanage.db.Database;
import com.devops.edumanage.db.DbConnection;
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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        try{
            for (Student st:searchStudents(searchText)) {
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

                            try {
                                deleteStudent(st.getStdId());
                                Database.studentTable.remove(st);
                                new Alert(Alert.AlertType.INFORMATION,"Deleted !").show();
                                setTableData(searchText);
                                setStudentId();
                            } catch (ClassNotFoundException |SQLException exception) {
                                new Alert(Alert.AlertType.WARNING,exception.toString()).show();
                            }
                        }
                    });
                    obList.add(tm);
            }
            tblStudent.setItems(obList);
        }catch (SQLException | ClassNotFoundException e){
            e.printStackTrace();
        }

    }

    private void setStudentId() {
        try{
            String lastId  =getLatId();
            if (null!=lastId){
                String[] splitData = lastId.split("-");
                String lastIntegerASString = splitData[1];
                int lastIntegerASInt = Integer.parseInt(lastIntegerASString);
                lastIntegerASInt++;

                String generatedStdId = "S-"+lastIntegerASInt;
                txtStudentId.setText(generatedStdId);
            }else {
                txtStudentId.setText("S-1");
            }
        }catch (SQLException | ClassNotFoundException e){
                e.printStackTrace();
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
        Student student = new Student(
                txtStudentId.getText(),
                txtFullName.getText(),
                Date.from(txtDOB.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                txtAddress.getText()
        );
        if (btn.getText().equalsIgnoreCase("Save Student")){
            try{
               if (saveStudent(student)){
                   Database.studentTable.add(student);
                   new Alert(Alert.AlertType.INFORMATION,"Student Saved!").show();
                   setStudentId();
                   clear();
                   System.out.println(student.toString());
                   setTableData(searchText);
               }else{
                   new Alert(Alert.AlertType.WARNING,"Try Again").show();
               }
            }catch (SQLException | ClassNotFoundException e){
                new Alert(Alert.AlertType.WARNING,e.toString()).show();
            }

        } else {
            try{
                if (updateStudent(student)){
                    clear();
                    setTableData(searchText);
                    new Alert(Alert.AlertType.INFORMATION,"Student Updated!").show();
                }else{
                    new Alert(Alert.AlertType.WARNING,"Try Again").show();
                }
            }catch (SQLException | ClassNotFoundException e){
                new Alert(Alert.AlertType.WARNING,e.toString()).show();
            }
        }
    }

    private boolean saveStudent(Student student) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        System.out.println(connection);
        String sql = "INSERT INTO student_table values(?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,student.getStdId());
        preparedStatement.setString(2,student.getFullName());
        preparedStatement.setObject(3,student.getDob());
        preparedStatement.setString(4,student.getAddress());
        return preparedStatement.executeUpdate() > 0;
    }

    private String getLatId() throws ClassNotFoundException, SQLException{
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lms_3","root","1234");
        String sql = "SELECT student_id FROM student_table ORDER BY CAST(SUBSTRING(student_id,3)AS UNSIGNED) DESC LIMIT 1";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()){
            return resultSet.getString(1);
        }
        return null;
    }

    private List<Student> searchStudents(String text) throws ClassNotFoundException, SQLException {
        text = "%" + text + "%";
        Connection connection = DbConnection.getInstance().getConnection();
        System.out.println(connection);
        String sql = "SELECT * FROM student_table WHERE full_name LIKE ? OR address LIKE ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,text);
        preparedStatement.setString(2,text);
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Student> list = new ArrayList<>();
        while(resultSet.next()){
            list.add(
                    new Student(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getDate(3),
                            resultSet.getString(4)
                    )
            );
        }
        return list;
    }

    private boolean deleteStudent(String id) throws ClassNotFoundException, SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        System.out.println(connection);
        String sql = "DELETE FROM student_table WHERE student_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, id);
        return preparedStatement.executeUpdate() > 0;
    }

    private boolean updateStudent(Student student) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lms_3","root","1234");
        String sql = "UPDATE student_table SET full_name=? , dob=?,address=? WHERE student_id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,student.getFullName());
        preparedStatement.setObject(2,student.getDob());
        preparedStatement.setString(3,student.getAddress());
        preparedStatement.setString(4,student.getStdId());
        return preparedStatement.executeUpdate() > 0;
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
