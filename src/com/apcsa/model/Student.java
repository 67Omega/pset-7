package com.apcsa.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.apcsa.model.User;

public class Student extends User {
	
	private User user;
	
	private int studentId;
    private int classRank;
    private int gradeLevel;
    private int graduationYear;
    private double gpa;
    private String firstName;
    private String lastName;
	
	public Student(User user, ResultSet rs) throws SQLException {
		super(user.getUserId(), user.getAccountType(), user.getFirstName(), user.getPassword(), user.getLastLogin());
		this.studentId = rs.getInt("student_id");
		this.classRank = rs.getInt("class_rank");
		this.gradeLevel = rs.getInt("grade_level");
		this.graduationYear = rs.getInt("graduation");
		this.gpa = rs.getDouble("gpa");
		this.firstName = rs.getString("first_name");
		this.lastName = rs.getString("last_name");
	}
	public Student(ResultSet rs) throws SQLException {
		super(-1,"student", null, null, null);
		this.studentId = rs.getInt("student_id");
		this.classRank = rs.getInt("class_rank");
		this.gradeLevel = rs.getInt("grade_level");
		this.graduationYear = rs.getInt("graduation");
		this.gpa = rs.getDouble("gpa");
		this.firstName = rs.getString("first_name");
		this.lastName = rs.getString("last_name");
	}
	
	public int getStudentID() {
		return studentId;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public int getClassRank() {
		return classRank;
	}
	
	public int getGraduationYear () {
		return graduationYear;
	}
	
	public int getGradeLevel () {
		return gradeLevel;
	}
	
	
	public double getGpa() {
		
		return gpa;
	}

	public void setClassRank(int i) {
		// TODO Auto-generated method stub
		
	}

}
