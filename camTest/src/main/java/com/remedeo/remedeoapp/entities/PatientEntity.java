package com.remedeo.remedeoapp.entities;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class PatientEntity implements Serializable {
	
	private int id;
	private String patientId;
	private String name;
	private String gender;
	private int age;
	private List<String> photoFilePathList;
	private String DOB;
	private String hospitalName;
	private String doctorName;
	private String comment;
	private String address;
	
	public String getDOB() {
		return DOB;
	}
	
	public void setDOB(String dOB) {
		DOB = dOB;
	}
	
	public String getHospitalName() {
		return hospitalName;
	}
	
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public int getAge() {
		return age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
	
	public List<String> getPhotoFilePathList() {
		return photoFilePathList;
	}
	
	public void setPhotoFilePathList(List<String> photoFilePathList) {
		this.photoFilePathList = photoFilePathList;
	}

	public String getDoctorName() {
		return doctorName;
	}

	public void setDoctorName(String doctorName) {
		this.doctorName = doctorName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	
}
