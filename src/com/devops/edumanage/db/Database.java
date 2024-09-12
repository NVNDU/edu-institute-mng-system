package com.devops.edumanage.db;

import com.devops.edumanage.model.Student;
import com.devops.edumanage.model.User;
import com.devops.edumanage.util.security.PasswordManager;

import java.util.ArrayList;

public class Database {
    public static ArrayList<User> userTable = new ArrayList<User>(); //static - only one for project

    public static ArrayList<Student> studentTable = new ArrayList<>();

    static {
        userTable.add(
                new User("navindu","ranjuka",
                        "n@gmail.com",
                        new PasswordManager().encrypt("1234"))
        );
    }

}
