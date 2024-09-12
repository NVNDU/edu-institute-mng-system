package com.devops.edumanage.model;

import java.util.Date;

public class Student {
    private String stdId;
    private String FullName;
    private Date dob;
    private String Address;

    public Student() {
    }

    public Student(String stdId, String fullName, Date dob, String address) {
        this.stdId = stdId;
        FullName = fullName;
        this.dob = dob;
        Address = address;
    }

    public String getStdId() {
        return stdId;
    }

    public void setStdId(String stdId) {
        this.stdId = stdId;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    @Override
    public String toString() {
        return "Student{" +
                "stdId='" + stdId + '\'' +
                ", FullName='" + FullName + '\'' +
                ", dob=" + dob +
                ", Address='" + Address + '\'' +
                '}';
    }
}
