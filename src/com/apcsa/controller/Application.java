package com.apcsa.controller;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import com.apcsa.data.PowerSchool;
import com.apcsa.data.QueryUtils;
import com.apcsa.model.Administrator;
import com.apcsa.model.Student;
import com.apcsa.model.Teacher;
import com.apcsa.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import com.apcsa.controller.Utils;

public class Application {
    private Scanner in;
    private User activeUser;
    enum RootAction { PASSWORD, DATABASE, LOGOUT, SHUTDOWN, NULL }
    enum StudentAction { COURSEGRADE, ASSIGNMENTGRADE, PASSWORD, LOGOUT, NULL }
    enum AdminAction { FACULTY, FACULTY_BY_DEP, STUDENT, STUDENT_GRADE, STUDENT_COURSE, PASSWORD, LOGOUT, NULL }
    enum DeptList { CS, ENGLISH, HISTORY, MATH, PHYS_ED, SCIENCE, NULL }
    enum GradeList { FRESHMAN, SOPHMORE, JUNIOR, SENIOR, NULL }
    enum TeacherAction { ENROLLMENT, ADD, DELETE, GRADE, PASSWORD, LOGOUT, NULL }
    /**
     * Creates an instance of the Application class, which is responsible for interacting
     * with the user via the command line interface.
     */

    public Application() {
        this.in = new Scanner(System.in);

        try {
            PowerSchool.initialize(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("PowerSchool -- now for students, teachers, and school administrators!");
    }

    /**
     * Starts the PowerSchool application.
     * @param conn 
     */

    public void startup() {
       while (true) {
            System.out.print("\nUsername: ");
            String username = in.next();
            
            System.out.print("Password: ");
            String password = in.next();

            // if login is successful, update generic user to administrator, teacher, or student
        try {
            if (login(username, password)) {
                activeUser = activeUser.isAdministrator()
                    ? PowerSchool.getAdministrator(activeUser) : activeUser.isTeacher()
                    ? PowerSchool.getTeacher(activeUser) : activeUser.isStudent()
                    ? PowerSchool.getStudent(activeUser) : activeUser.isRoot()
                    ? activeUser : null;
                    
                if (isFirstLogin() && !activeUser.isRoot()) {
                    // first-time users need to change their passwords from the default provided
                	System.out.print("\nEnter new password: ");
                	String newPassword = in.next();
                	String newPass = activeUser.setPassword(newPassword);
					PowerSchool.updatePassword (newPass, username);
                	
                }
                activeUser.setUsername(username);
                createAndShowUI();
            } else {
                System.out.println("\nInvalid username and/or password.");
            }
        } catch (Exception e) {
        	System.out.println(e);
        	shutdown(e);
        } finally {
        	
        }
      }
    }
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
    
    public int showTeachersCourses (ArrayList<String> courses) {
    	String stringSelected = "";
    	int courseSelected;
    	int inSelect;
    	System.out.println("\nChoose a course.\n");
    	while (true) {
    		int counter = 1;
    		for(String i: courses) {
    			System.out.print("[" + counter + "] ");
    			System.out.println(i);
    			counter++;
        	}
			System.out.print("\n::: ");
			stringSelected = in.next();
			System.out.print("\n");
			if (!isInteger(stringSelected)) {
				System.out.print("");
			} else if (Integer.parseInt(stringSelected) >= counter || Integer.parseInt(stringSelected)  < 1 ) {
				System.out.print("");
			} else {
				inSelect = Integer.parseInt(stringSelected);
				courseSelected = PowerSchool.checkCourseId(courses.get(inSelect - 1));
    	        break;
    	    }
    	}
    	return courseSelected;
    }

        public void createAndShowUI() throws ClassNotFoundException, SQLException {
            System.out.print("\nHello, again, " + activeUser.getFirstName() + "!");

            if (activeUser.isRoot()) {
                showRootUI();
            } else if (activeUser.isStudent()){
                showStudentUI();
            } else if (activeUser.isTeacher()){
            	showTeacherUI();
            } else if (activeUser.isAdministrator()) {
            	showAdministratorUI();
            } else {
            	
            }
        }
        
        private void showRootUI() {
            while (activeUser != null) {
                switch (getRootMenuSelection()) {
                    case PASSWORD: resetPassword(); break;
                    case DATABASE: factoryReset(); break;
                    case LOGOUT: logout(); break;
                    case SHUTDOWN: shutdown(); break;
                    case NULL: System.out.println("\nInvalid selection."); showRootUI(); break;
                    default: System.out.println("\nInvalid selection."); break;
                }
            }
        }
        
        private void showStudentUI() throws ClassNotFoundException, SQLException {
            while (activeUser != null) {
                switch (getStudentMenuSelection()) {
                   	case COURSEGRADE: viewCourseGrades(); break;
                    case ASSIGNMENTGRADE: viewAssignmentGrades(); break;
                    case PASSWORD: changePassword(); break;
                    case LOGOUT: logout(); break;
                    case NULL: System.out.println("\nInvalid selection."); showStudentUI();
                    default: System.out.println("\nInvalid selection."); break;
                }
            }
        }
        
        

		private void showTeacherUI() throws ClassNotFoundException, SQLException {
            while (activeUser != null) {
                switch (getTeacherMenuSelection()) {
                    case ENROLLMENT: coursesTeacher(); break;
                    case ADD: addAssignment(); break;
                    case DELETE: deleteAssignment(); break;
                    case GRADE: addGrade(); break;
                    case PASSWORD: changePassword(); break;
                    case LOGOUT: logout(); break;
                    case NULL: System.out.println("\nInvalid selection."); showTeacherUI();
                    default: System.out.println("\nInvalid selection."); break;
                }
            }
        }
        
        private void showAdministratorUI() throws ClassNotFoundException, SQLException {
            while (activeUser != null) {
                switch (getAdminMenuSelection()) {
                    case FACULTY: showFaculty(); break;
                    case FACULTY_BY_DEP: showDepartmentUI(); break;
                    case STUDENT: showStudents(); break;
                    case STUDENT_GRADE: showGradeUI(); break;
                    case STUDENT_COURSE: studentCourse(); break;
                    case PASSWORD: changePassword(); break;
                    case LOGOUT: logout(); break;
                    case NULL: System.out.println("\nInvalid selection."); showAdministratorUI();
                    default: System.out.println("\nInvalid selection."); break;
                }
            }
        }
        
        private void showStudents() throws ClassNotFoundException, SQLException {
        	ArrayList<Student> students = PowerSchool.showStudents();
        	int counter = 1;
        	for(Student i: students) {
        		System.out.print("\n" + counter + ". ");
        		System.out.print(i.getLastName() + ", ");
        		System.out.print(i.getFirstName() + " / ");
        		System.out.print(i.getGraduationYear());
        		counter++;
        	}  
        }
        
        private GradeList displayGrades() {
        	System.out.println("\n[1] Freshman.");
            System.out.println("[2] Sophmore.");
            System.out.println("[3] Junior.");
            System.out.println("[4] Senior.");
            System.out.print("\n::: ");
            
            switch (Utils.getInt(in, -1)) {
            	case 1: return GradeList.FRESHMAN;
            	case 2: return GradeList.SOPHMORE;
            	case 3: return GradeList.JUNIOR;
            	case 4: return GradeList.SENIOR;
            default: return GradeList.NULL;
        }
        }
        
        private void viewCourseGrades() throws ClassNotFoundException, SQLException {
			ArrayList<String> courseAndGrade = PowerSchool.showCourseGrade(PowerSchool.getStudentId(activeUser.getUserId()));
			System.out.print("\n");
			for (int i = 0; i < courseAndGrade.size(); i += 2) {
				String grade = (courseAndGrade.get(i + 1) == null) ? "--" : (courseAndGrade.get(i + 1));
    			System.out.print(i + 1 + ". " + courseAndGrade.get(i) + " / " + grade + "\n");
    		}
        }
        
        private void viewAssignmentGrades() throws ClassNotFoundException, SQLException {
        	int is_final = 0;
        	int is_midterm = 0;
        	int marking_period;
        	Boolean validCourse = true;
        	Boolean realTerm;
        	ArrayList<String> courses = PowerSchool.checkCourseByStudent(PowerSchool.getStudentId(activeUser.getUserId()));
        	int course_select;
			do {
        		System.out.println("\nChoose a course.\n");
        		int counter = 1;
        		for(String i: courses) {
        			System.out.print("[" + counter + "] ");
        			System.out.println(i);
        			counter++;
            	}
    			System.out.print("\n::: ");
        		
    			course_select = in.nextInt();
    			
        		if (course_select <= 0 || course_select > counter) {
        			validCourse = false;
        			System.out.println("\nCourse not found.");
        		}
        	} while (!validCourse); 
			
			int course_id = PowerSchool.checkCourseId(courses.get(course_select - 1));

        	do {
        		realTerm = true;
        		System.out.print("\nChoose a marking period or exam status.\n");
        		System.out.println("\n[1] MP1 assignment.");
            	System.out.println("[2] MP2 assignment.");
            	System.out.println("[3] MP3 assignment.");
            	System.out.println("[4] MP4 assignment.");
            	System.out.println("[5] Midterm exam.");
            	System.out.println("[6] Final exam.");
            	System.out.print("\n::: ");
            	
            	marking_period = in.nextInt();
            	
            	switch (marking_period){
            		case 1: break;
            		case 2: break;
            		case 3: break;
            		case 4: break;
            		case 5: marking_period = -1;
            		is_midterm = 1;
            		break;
            		case 6: marking_period = -1;
            		is_final = 1;
            		break;
            		default: realTerm = false;
            		break;
            	}
        	} while (!realTerm);
        	
        	
			System.out.print("\n");
        	ArrayList<String> assignmentAndGrade = PowerSchool.showAssignmentGrade(PowerSchool.getStudentId(activeUser.getUserId()), course_id, marking_period, is_midterm, is_final);
        	for (int i = 0; i <= assignmentAndGrade.size() - 2; i += 3) {
        		System.out.print(i + 1 + ". " + assignmentAndGrade.get(i) + " / " + assignmentAndGrade.get(i + 1) + " (out of " + assignmentAndGrade.get(i + 2) + " pts)"); 
        	}
        }
        
        private void showGradeUI() throws ClassNotFoundException, SQLException {
            
            switch (displayGrades()) {
                case FRESHMAN: studentsGrade(1); break;
                case SOPHMORE: studentsGrade(2); break;
                case JUNIOR: studentsGrade(3); break;
                case SENIOR: studentsGrade(4); break;
                default: showGradeUI(); break;
            }
        }
        
        private void studentsGrade(int gradeLevel) throws ClassNotFoundException, SQLException {
        	gradeLevel += 8;
        	ArrayList<Student> students = PowerSchool.showStudents();
        	int counter = 1;
        	if (students.size() == 0) {
        		for(Student i: students) {
        			if (i.getGradeLevel() == gradeLevel) {
        				System.out.print("\n" + counter + ". ");
        				System.out.print(i.getLastName() + ", ");
        				System.out.print(i.getFirstName() + " / #");
        				System.out.print(i.getClassRank());
        				counter++;
        			}
        		}
        	} else {
        		System.out.print("\nNo students to display.\n");

        	}
		}
        

        private void studentCourse() throws ClassNotFoundException, SQLException {
        	Boolean realCourse = false;
        	String course_no = "";
        	ArrayList<String> courses = PowerSchool.checkCourseNo();
        	do {
        		System.out.print("\nCourse No.: ");
        		course_no = in.next();
        		for (String i: courses) {
        			if (i.equals(course_no)) {
        				realCourse = true;
        			}
            	}
        		if (!realCourse) {
        			System.out.println("\nCourse not found.");
        		}
        	} while (!realCourse); 
        	ArrayList<Student> students = PowerSchool.showStudentsCourse(course_no);
        	int counter = 1;
        	if (students.size() != 0) {
	        	for(Student i: students) {
	        			System.out.print("\n" + counter + ". ");
	        			System.out.print(i.getLastName() + ", ");
	        			System.out.print(i.getFirstName() + " / ");
	        			if (i.getGpa() == -1) {
	        				System.out.print("--");
	        			} else {
	        				System.out.print(i.getGpa());
	        			}
	        			counter++;
	        		} 
	        	} else {
	        		System.out.print("\nNo students to display.");
        	}
		}
        
        
        
        private void addGrade () throws ClassNotFoundException, SQLException {
        	ArrayList<String> assignments;
        	int realZero1 = -1;
        	int realZero2 = -1;
        	int realZero3 = -1;
        	int realZero4 = -1;
        	int realZero5 = -1;
        	int realZero6 = -1;
        	Float mp1;
        	Float mp2;
        	Float mp3;
        	Float mp4;
        	Float mid;
        	Float fin;
        	//int assignment_id;
        	//String point_value_string = "";
        	//int point_value;
        	int is_final = 0;
        	int is_midterm = 0;
        	int marking_period;
        	String marking_period_string = "";
        	String mp = "";
        	Boolean realTerm = true;
        	Float sum = (float) 0;
        	//Boolean rerunValue = false;
        	ArrayList<String> courses = PowerSchool.checkCourseByTeacher(activeUser.getUserId()-3);
        	int course_id = showTeachersCourses(courses);        

        	do {
        		if (!realTerm) {
        			System.out.print("\nI don't know how to categorize that.\n");
        		}
        		System.out.print("\nChoose a marking period or exam status.\n");
        		System.out.println("\n[1] MP1 assignment.");
            	System.out.println("[2] MP2 assignment.");
            	System.out.println("[3] MP3 assignment.");
            	System.out.println("[4] MP4 assignment.");
            	System.out.println("[5] Midterm exam.");
            	System.out.println("[6] Final exam.");
            	System.out.print("\n::: ");
            	
            	marking_period_string = in.next();
            	
            	switch (marking_period_string){
            		case "1": 
                		realTerm = true; mp = "mp1"; break;
            		case "2":
                		realTerm = true; mp = "mp2"; break;
            		case "3":
                		realTerm = true; mp = "mp3";  break;
            		case "4": 
                		realTerm = true; mp = "mp4";  break;
            		case "5": 
                		realTerm = true; marking_period_string = "-1";
            		is_midterm = 1; mp = "midterm_exam";
            		break;
            		case "6": 
                		realTerm = true; marking_period_string = "-1";
            		is_final = 1;
            		mp = "final_exam";
            		break;
            		default: realTerm = false;
            		break;
            	}
        	} while (!realTerm);
        	marking_period = Integer.parseInt(marking_period_string);
        	
        		System.out.print("\nChoose an assignment.\n\n");
        		assignments = PowerSchool.checkAssignmentByTeacher(course_id, marking_period, is_midterm, is_final, activeUser.getUserId()-3);
        		int counter = 1;
        		for (int i = 0; i < assignments.size(); i += 3) {
        			System.out.println("[" + counter + "]" + " " + assignments.get(i) + " (" + assignments.get(i + 1) + " pts)");
        			counter++;
        		}
        		System.out.print("\n::: ");
        		
        	
        		int assignmentSelected = in.nextInt();
				ArrayList<Student> students = PowerSchool.showStudentsAssignment(assignments.get(assignmentSelected * 3 - 1), course_id);
            	int counter1 = 1;
            	for(Student i: students) {
            			System.out.print("\n[" + counter1 + "] ");
            			System.out.print(i.getLastName() + ", ");
            			System.out.print(i.getFirstName());
            			counter1++;
            		} 
            	System.out.print("\n\n::: ");
        		int studentSelected = in.nextInt();
        		System.out.print("\nAssignment: " + assignments.get(assignmentSelected * 3 - 3) + "\n");
        		System.out.print("Student: " + students.get(studentSelected - 1).getLastName() + ", " + students.get(studentSelected - 1).getFirstName() + "\n");
        		System.out.print("Current Grade: ");
        		if (PowerSchool.getAssignmentGrade(Integer.parseInt(assignments.get(assignmentSelected * 3 - 1)), students.get(studentSelected - 1).getStudentId(), course_id) == null) {
    				System.out.print("--\n");
    			} else {
    				System.out.print(PowerSchool.getAssignmentGrade(Integer.parseInt(assignments.get(assignmentSelected * 3 - 1)), students.get(studentSelected - 1).getStudentId(), course_id) + "\n");
    			}
        		
        		System.out.print("\nNew Grade: ");
        		int points_earned = in.nextInt();        
        			switch (mp) {
        			case "mp1": realZero1 = 1;
        				break;
        			case "mp2": realZero2 = 1;
    				break;
        			case "mp3": realZero3 = 1;
    				break;
        			case "mp4": realZero4 = 1;
    				break;
        			case "mid": realZero5 = 1;
    				break;
        			case "fin": realZero6 = 1;
    				break;

        			
        			}
        	
        	if (Utils.confirm(in, "\nAre you sure you want to enter this grade? (y/n) ")) {
                	PowerSchool.gradeAssignment(points_earned, students.get(studentSelected - 1).getStudentId(), Integer.parseInt(assignments.get(assignmentSelected * 3 - 1)), course_id);

                	
                	ArrayList<Float> otherAssignments = PowerSchool.calculateMPGrade(marking_period, is_midterm, is_final, students.get(studentSelected - 1).getStudentId(), course_id);
                	
             
                	
                	for(int i = 0; i < otherAssignments.size(); i++) {
                		
                        sum = sum + otherAssignments.get(i);
                	}
                    float marking_period_grade = sum / otherAssignments.size();
   
                    ArrayList<Float> studentsMPGrades = PowerSchool.enumStudentCourseGrade(course_id, students.get(studentSelected - 1).getStudentId());
                	mp1 = studentsMPGrades.get(0);
                	mp2 = studentsMPGrades.get(1);
                	mp3 = studentsMPGrades.get(2);
                	mp4 = studentsMPGrades.get(3);
                	mid = studentsMPGrades.get(4);
                	fin = studentsMPGrades.get(5);
                	
                	PowerSchool.updateStudentMPGrade(mp, marking_period_grade, students.get(studentSelected - 1).getStudentId(), course_id);
                	PowerSchool.calculateAndSetCourseGrade (mp1, mp2, mp3, mp4, fin, mid, students.get(studentSelected - 1).getStudentId(), course_id, realZero1, realZero2, realZero3, realZero4, realZero5, realZero6);
                	PowerSchool.setStudentGpa(students.get(studentSelected - 1).getStudentId());
                	System.out.print("\nSuccessfully entered.");
                }
        	}        	
        

        
        private void addAssignment() throws ClassNotFoundException, SQLException {
        	int assignment_id;
        	String point_value_string = "";
        	int point_value;
        	int is_final = 0;
        	int is_midterm = 0;
        	int marking_period;
        	String marking_period_string = "";
        	String title = "";
        	
        	Boolean realTerm = true;
        	Boolean rerunValue = false;
        	ArrayList<String> courses = PowerSchool.checkCourseByTeacher(activeUser.getUserId()-3);
        	int course_id = showTeachersCourses(courses);        

        	do {
        		if (!realTerm) {
        			System.out.print("\nI don't know how to categorize that.\n");
        		}
        		System.out.print("\nChoose a marking period or exam status.\n");
        		System.out.println("\n[1] MP1 assignment.");
            	System.out.println("[2] MP2 assignment.");
            	System.out.println("[3] MP3 assignment.");
            	System.out.println("[4] MP4 assignment.");
            	System.out.println("[5] Midterm exam.");
            	System.out.println("[6] Final exam.");
            	System.out.print("\n::: ");
            	
            	marking_period_string = in.next();
            	
            	switch (marking_period_string){
            		case "1": 
                		realTerm = true; break;
            		case "2":
                		realTerm = true; break;
            		case "3":
                		realTerm = true; break;
            		case "4": 
                		realTerm = true; break;
            		case "5": 
                		realTerm = true; marking_period_string = "-1";
            		is_midterm = 1;
            		break;
            		case "6": 
                		realTerm = true; marking_period_string = "-1";
            		is_final = 1;
            		break;
            		default: realTerm = false;
            		break;
            	}
        	} while (!realTerm);
        	marking_period = Integer.parseInt(marking_period_string);
        	System.out.print("\nAssignment Title: ");
        	
        	do {
        		title = in.nextLine();
        	
        	} while (title.isEmpty() || title.isBlank());
        	do {
        		
        	if (rerunValue) {
        		System.out.println("Point values must be between 1 and 100.\n");
        	}
        	
        		System.out.print("Point Value: ");
        		point_value_string = in.next();
        		rerunValue = true;
        		if (isInteger(point_value_string)) { 
        			if (Integer.parseInt(point_value_string) > 0 && Integer.parseInt(point_value_string) <= 100) {
        				point_value = Integer.parseInt(point_value_string);
        				break;
        			}
        		}
        	} while (true);
        	
        	System.out.print("\n");
        	
        	if (Utils.confirm(in, "Are you sure you want to create this assignment? (y/n) ")) {
                if (in != null) {
                	
                		assignment_id = 1 + PowerSchool.checkLastAId(course_id);
                	
                	
                	PowerSchool.addAssignment(course_id, assignment_id, marking_period, is_midterm, is_final, title, point_value);
                	ArrayList<Student> students = PowerSchool.showStudentsCourse(PowerSchool.getCourseNo(course_id));
                	for(Student i: students) {
                		PowerSchool.addStudentToAssignment(course_id, assignment_id, i.getStudentId(), point_value);
               		} 
                	
                	System.out.print("\nSuccessfully created assignment.");
                }
        	}        	
        }
       private void setGpa() {
    	   if (weight = 1.0) {
    		   switch (course_grade) {
    		   
    		   }
    	   }
       }
        private void deleteAssignment() throws ClassNotFoundException, SQLException {
        	int realZero1 = 1;
        	int realZero2 = 1;
        	int realZero3 = 1;
        	int realZero4 = 1;
        	int realZero5 = 1;
        	int realZero6 = 1;
        	float sum = (float)0;
        	int is_final = 0;
        	int is_midterm = 0;
        	int marking_period;
        	Float mp1;
        	Float mp2;
        	Float mp3;
        	Float mp4;
        	Float mid;
        	Float fin;
        	
        	String mp = "";
        	String marking_period_string = "";
        	Boolean realTerm = true;
        	
        	ArrayList<String> courses = PowerSchool.checkCourseByTeacher(activeUser.getUserId()-3);
        	int course_id = showTeachersCourses(courses);        

        	 do {
        		if (!realTerm) {
        			System.out.print("\nI don't know how to categorize that.\n");
        		}
        		System.out.print("\nChoose a marking period or exam status.\n");
        		System.out.println("\n[1] MP1 assignment.");
            	System.out.println("[2] MP2 assignment.");
            	System.out.println("[3] MP3 assignment.");
            	System.out.println("[4] MP4 assignment.");
            	System.out.println("[5] Midterm exam.");
            	System.out.println("[6] Final exam.");
            	System.out.print("\n::: ");
            	
            	marking_period_string = in.next();
            	
            	switch (marking_period_string){
        		case "1": 
            		realTerm = true; mp = "mp1"; break;
        		case "2":
            		realTerm = true; mp = "mp2"; break;
        		case "3":
            		realTerm = true; mp = "mp3";  break;
        		case "4": 
            		realTerm = true; mp = "mp4";  break;
        		case "5": 
            		realTerm = true; marking_period_string = "-1";
        		is_midterm = 1; mp = "midterm_exam";
        		break;
        		case "6": 
            		realTerm = true; marking_period_string = "-1";
        		is_final = 1;
        		mp = "final_exam";
        		break;
        		default: realTerm = false;
        		break;
        	}
    	} while (!realTerm);
    	marking_period = Integer.parseInt(marking_period_string);
        	ArrayList<String> assignments;
			int assignmentToDelete;
			String assignment_idString;
			boolean validAssignment;
			marking_period = Integer.parseInt(marking_period_string);
			do {
        		System.out.println("\nChoose an assignment. ");
        		assignments = PowerSchool.checkAssignmentByTeacher(course_id, marking_period, is_midterm, is_final, activeUser.getUserId()-3);
        		int counter = 1;
        		for (int i = 0; i < assignments.size(); i += 3) {
        			System.out.println("[" + counter + "]" + " " + assignments.get(i) + " (" + assignments.get(i + 1) + " pts)");
        			counter++;
        		}
        		System.out.print("\n::: ");
        		assignmentToDelete = in.nextInt();
        		assignment_idString = assignments.get(assignmentToDelete * 3 - 1);
        		validAssignment = (assignmentToDelete <= counter && assignmentToDelete > 0);
        	} while (!validAssignment);
        	if (Utils.confirm(in, "Are you sure you want to delete this assignment? (y/n) ")) {
                if (in != null) {
                	PowerSchool.delAssignment(Integer.parseInt(assignment_idString), course_id);
                	ArrayList<Integer> students = PowerSchool.showStudentsAs(Integer.parseInt(assignment_idString), course_id);
                	PowerSchool.delAssignmentGrade(Integer.parseInt(assignment_idString), course_id);
                	

                	for (Integer u: students) {
                		ArrayList<Float> otherAssignments = PowerSchool.calculateMPGrade(marking_period, is_midterm, is_final, u, course_id);
                    	ArrayList<Float> studentsMPGrades = PowerSchool.enumStudentCourseGrade(course_id, u);
                    	mp1 = studentsMPGrades.get(0);
                    	mp2 = studentsMPGrades.get(1);
                    	mp3 = studentsMPGrades.get(2);
                    	mp4 = studentsMPGrades.get(3);
                    	mid = studentsMPGrades.get(4);
                    	fin = studentsMPGrades.get(5);
                    	
                    	if (studentsMPGrades.get(0) == null) {
                    		realZero1 = -1;
                    	}
                    	if (studentsMPGrades.get(1) == null) {
                    		realZero2 = -1;
                    	}if (studentsMPGrades.get(2) == null) {
                    		realZero3 = -1;
                    	}if (studentsMPGrades.get(3) == null) {
                    		realZero4 = -1;
                    	}if (studentsMPGrades.get(4) == null) {
                    		realZero5 = -1;
                    	}if (studentsMPGrades.get(5) == null) {
                    		realZero6 = -1;
                    	}
                    	PowerSchool.calculateAndSetCourseGrade(mp1, mp2, mp3, mp4, fin, mid, u, course_id, realZero1,realZero2,realZero3,realZero4,realZero5,realZero6);
                    
						for(int i = 0; i < otherAssignments.size(); i++) {
                    		
                            sum  = sum + otherAssignments.get(i);
                    	}
                        float marking_period_grade = sum / otherAssignments.size();
       
                        sum = 0;
                    	
                    	PowerSchool.updateStudentMPGrade(mp, marking_period_grade, u, course_id);
                	}
                	
                	
                	System.out.print("\nSuccessfully deleted " + assignments.get(assignmentToDelete * 3 - 3) + ".");
                }
        	}        	
        }
        
        private void coursesTeacher() throws ClassNotFoundException, SQLException {
        	ArrayList<String> courses = PowerSchool.checkCourseByTeacher(activeUser.getUserId()-3);
        	int course_id = showTeachersCourses(courses);        
    			
        	ArrayList<Student> students = PowerSchool.showStudentsCourse(courses.get(course_id - 1));
        	
        	if (students.size() == 0) {
        		System.out.print("No students to display.");
        	} else {
	        	int counter = 1;
	        	for(Student i: students) {
	        			System.out.print("\n" + counter + ". ");
	        			System.out.print(i.getLastName() + ", ");
	        			System.out.print(i.getFirstName() + " / ");
	        			if (PowerSchool.getGrade(course_id, i.getStudentId()) == null) {
	        				System.out.print("--");
	        			} else {
	        				System.out.print(PowerSchool.getGrade(course_id, i.getStudentId()));
	        			}
	        			counter++;
	        		} 
        	}
		}
        
        private void showDepartmentUI() throws ClassNotFoundException, SQLException {
            
                switch (getDepartmentSelection()) {
                    case CS: selectDepartment(1); break;
                    case ENGLISH: selectDepartment(2); break;
                    case HISTORY: selectDepartment(3); break;
                    case MATH: selectDepartment(4); break;
                    case PHYS_ED: selectDepartment(5); break;
                    case SCIENCE: selectDepartment(6); break;
                    case NULL: showDepartmentUI(); break;
                }
        }
        /*
         * Retrieves a root user's menu selection.
         * 
         * @return the menu selection
         */

        private void showFaculty() throws ClassNotFoundException, SQLException {
        	ArrayList<Teacher> teachers = PowerSchool.showFaculty();
        	int counter = 1;
        	for(Teacher i: teachers) {
        		System.out.print("\n" + counter + ". ");
        		System.out.print(i.getLastName() + ", ");
        		System.out.print(i.getFirstName() + " / ");
        		System.out.print(i.getDeptName());
        		counter++;
        	}  
		}
        
        private void selectDepartment(int department_id) throws ClassNotFoundException, SQLException {
        	ArrayList<Teacher> teachers = PowerSchool.showFaculty();
        	int counter = 1;
        	for(Teacher i: teachers) {
        		if (i.getDepartmentId() == department_id) {
        			System.out.print("\n" + counter + ". ");
        			System.out.print(i.getLastName() + ", ");
        			System.out.print(i.getFirstName() + " / ");
        			System.out.print(i.getDeptName());
        			counter++;
        		}
        	}  
		}

		private RootAction getRootMenuSelection() {
            System.out.println();
            
            System.out.println("\n[1] Reset user password.");
            System.out.println("[2] Factory reset database.");
            System.out.println("[3] Logout.");
            System.out.println("[4] Shutdown.");
            System.out.print("\n::: ");
            
            
            
            switch (Utils.getInt(in, -1)) {
                case 1: return RootAction.PASSWORD;
                case 2: return RootAction.DATABASE;
                case 3: return RootAction.LOGOUT;
                case 4: return RootAction.SHUTDOWN;
                default: return RootAction.NULL;
            }
         }
		
		private TeacherAction getTeacherMenuSelection() {
            System.out.println();
            
            System.out.println("\n[1] View enrollment by course.");
            System.out.println("[2] Add assignment.");
            System.out.println("[3] Delete assignment.");
            System.out.println("[4] Enter grade.");
            System.out.println("[5] Change password.");
            System.out.println("[6] Logout.");
            System.out.print("\n::: ");
            
            switch (Utils.getInt(in, -1)) {
                case 1: return TeacherAction.ENROLLMENT;
                case 2: return TeacherAction.ADD;
                case 3: return TeacherAction.DELETE;
                case 4: return TeacherAction.GRADE;
                case 5: return TeacherAction.PASSWORD;
                case 6: return TeacherAction.LOGOUT;
                default: return TeacherAction.NULL;
            }
         }
		
		
        
        private StudentAction getStudentMenuSelection() {
            System.out.println();
            
            System.out.println("\n[1] View course grades.");
            System.out.println("[2] View assignment grades by course.");
            System.out.println("[3] Change password.");
            System.out.println("[4] Logout.");
            System.out.print("\n::: ");
            
            switch (Utils.getInt(in, -1)) {
                case 1: return StudentAction.COURSEGRADE;
                case 2: return StudentAction.ASSIGNMENTGRADE;
                case 3: return StudentAction.PASSWORD;
                case 4: return StudentAction.LOGOUT;
                default: return StudentAction.NULL;
            }
         }
        
        private AdminAction getAdminMenuSelection() {
        	System.out.println();
            
            System.out.println("\n[1] View faculty.");
            System.out.println("[2] View faculty by department.");
            System.out.println("[3] View student enrollment.");
            System.out.println("[4] View student enrollment by grade.");
            System.out.println("[5] View student enrollment by course.");
            System.out.println("[6] Change password.");
            System.out.println("[7] Logout.");
            System.out.print("\n::: ");
            
            switch (Utils.getInt(in, -1)) {
                case 1: return AdminAction.FACULTY;
                case 2: return AdminAction.FACULTY_BY_DEP;
                case 3: return AdminAction.STUDENT;
                case 4: return AdminAction.STUDENT_GRADE;
                case 5: return AdminAction.STUDENT_COURSE;
                case 6: return AdminAction.PASSWORD;
                case 7: return AdminAction.LOGOUT;
                default: return AdminAction.NULL;
            }
        }
        
        private DeptList getDepartmentSelection() {
        	System.out.println("\n[1] Computer Science.");
            System.out.println("[2] English.");
            System.out.println("[3] History.");
            System.out.println("[4] Mathematics.");
            System.out.println("[5] Physical Education.");
            System.out.println("[6] Science.");
            System.out.print("\n::: ");
            
            switch (Utils.getInt(in, -1)) {
            	case 1: return DeptList.CS;
            	case 2: return DeptList.ENGLISH;
            	case 3: return DeptList.HISTORY;
            	case 4: return DeptList.MATH;
            	case 5: return DeptList.PHYS_ED;
            	case 6: return DeptList.SCIENCE;
            	default: return DeptList.NULL;
        }
        }
        /*
         * Shuts down the application after encountering an error.
         * 
         * @param e the error that initiated the shutdown sequence
         */

        private void shutdown(Exception e) {
            if (in != null) {
                in.close();
            }
            
            System.out.println("Encountered unrecoverable error. Shutting down...\n");
            System.out.println(e.getMessage());
                    
            System.out.println("\nGoodbye!");
            System.exit(0);
        }

        /*
         * Releases all resources and kills the application.
         */

        private void shutdown() {        
            System.out.println();
                
            if (Utils.confirm(in, "Are you sure? (y/n) ")) {
                if (in != null) {
                    in.close();
                }
                
                System.out.println("\nGoodbye!");
                System.exit(0);
            }
        }
        
        /*
         * Allows a root user to reset another user's password.
         */

        private void resetPassword() {
            
            System.out.print("\nUsername: ");
            String usernameForReset = in.next();
           
            if (Utils.confirm(in, "Are you sure you want to reset the password for " + usernameForReset + "? (y/n) ")) {
            	PowerSchool.resetPassword(usernameForReset);
            	if (PowerSchool.resetPassword(usernameForReset)) {
            		System.out.print("\nSuccessfully reset password for " + usernameForReset + ".");
            	} else {
            		System.out.print("\nNo such user...");
            	}
            } 
            // ask root user to confirm intent to reset the password for that username
            //
            // if confirmed...
            //      call database method to reset password for username
            //      print success message
            //
        }
        
        /*
         * Resets the database to its factory settings.
         */

        private void factoryReset() {
            if (Utils.confirm(in, "Are you sure you want to reset all settings and data? (y/n) ")) {
            	PowerSchool.initialize(true);
            	System.out.println("Successfully reset database.");
            } 
        }
        
        /*
         * Logs out of the application.
         */

        private void logout() {
        	System.out.print("\n");
            if (Utils.confirm(in, "Are you sure? (y/n) ")) {
            	startup();
            } 
        }
        
        
        
    /**
     * Logs in with the provided credentials.
     *
     * @param username the username for the requested account
     * @param password the password for the requested account
     * @return true if the credentials were valid; false otherwise
     */

    public boolean login(String username, String password) {
        activeUser = PowerSchool.login(username, password);

        return activeUser != null;
    }

    /**
     * Determines whether or not the user has logged in before.
     *
     * @return true if the user has never logged in; false otherwise
     */

    public boolean isFirstLogin() {
        return activeUser.getLastLogin().equals("0000-00-00 00:00:00.000");
    }

 
    
    /////// MAIN METHOD ///////////////////////////////////////////////////////////////////

    /*
     * Starts the PowerSchool application.
     *
     * @param args unused command line argument list
     */
    public void changePassword() {

    	System.out.print("\nEnter current password: ");
    	String passwordCheck = in.next();
    	System.out.print("Enter new password: ");
    	String newPassword = in.next();
    	
    	if (!(Utils.getHash(passwordCheck).equals(activeUser.getPassword()))) {
    		System.out.println("\nInvalid current password.");
    	} else {

    		String newPass = activeUser.setPassword(newPassword);
			PowerSchool.updatePassword(newPass, activeUser.getUsername());
			System.out.print("\nSuccessfully changed password.");
    	}
    }

    public static void main(String[] args) {
        Application app = new Application();

        app.startup();
    }
}
