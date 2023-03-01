package com.example.foreigndoctor.pojo;

import java.io.Serializable;

public class PatientDetails implements Serializable {

    private String patient_id;

    private String imageURI;
    private String firstName;
    private String lastName;

    private String area;
    private String city;

    private String phone;
    private String email;
    private String password;

    private String userType;

    public PatientDetails() {
    }

    public PatientDetails(String patient_id, String imageURI, String firstName, String lastName, String area, String city, String phone, String email, String password, String userType) {
        this.patient_id = patient_id;
        this.imageURI = imageURI;
        this.firstName = firstName;
        this.lastName = lastName;
        this.area = area;
        this.city = city;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public PatientDetails(String imageURI, String firstName, String lastName, String area, String city, String phone, String email, String password, String userType) {
        this.imageURI = imageURI;
        this.firstName = firstName;
        this.lastName = lastName;
        this.area = area;
        this.city = city;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
