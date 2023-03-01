package com.example.foreigndoctor.pojo;

import android.net.Uri;

import java.io.Serializable;
import java.util.List;

public class DoctorDetails implements Serializable {

    private String doctorID;

    private String name;
    private String degree;
    private String speciality;
    private String registerNumber;
    private String startTime;
    private String endTime;
    private String block;
    private String floor;
    private String room;
    private String imageURI;
    private String patient_count;
    private String fees;

    private List<String> dateList;


    public DoctorDetails() {
    }

    public DoctorDetails(String doctorID, String name, String degree, String speciality, String registerNumber, String startTime, String endTime, String block, String floor, String room, String imageURI, String patient_count, String fees, List<String> dateList) {
        this.doctorID = doctorID;
        this.name = name;
        this.degree = degree;
        this.speciality = speciality;
        this.registerNumber = registerNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.block = block;
        this.floor = floor;
        this.room = room;
        this.imageURI = imageURI;
        this.patient_count = patient_count;
        this.fees = fees;
        this.dateList = dateList;
    }

    public List<String> getDateList() {
        return dateList;
    }

    public void setDateList(List<String> dateList) {
        this.dateList = dateList;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getPatient_count() {
        return patient_count;
    }

    public void setPatient_count(String patient_count) {
        this.patient_count = patient_count;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }


}