package com.apcsa.controller;

import java.util.Scanner;
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
    enum RootAction { PASSWORD, DATABASE, LOGOUT, SHUTDOWN }
    enum StudentAction { COURSEGRADE, ASSIGNMENTGRADE, PASSWORD, LOGOUT }
    enum AdminAction { FACULTY, FACULTY_BY_DEP, STUDENT, STUDENT_GRADE, STUDENT_COURSE, PASSWORD, LOGOUT }
    enum DeptList { CS, ENGLISH, HISTORY, MATH, PHYS_ED, SCIENCE }
    enum GradeList { FRESHMEN, SOPHMORE, JUNIOR, SENIOR }
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
                	System.out.print("Enter new password: ");
                	String newPassword = in.next();
                	String newPass = activeUser.setPassword(newPassword);
					PowerSchool.updatePassword (newPass, username);
                	
                }

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
                    default: System.out.println("\nInvalid selection."); break;
                }
            }
        }
        
        private void showStudentUI() {
            while (activeUser != null) {
                switch (getStudentMenuSelection()) {
                   // case COURSEGRADE: viewCourseGrades(); break;
                    //case ASSIGNMENTGRADE: viewAssignmentGrades(); break;
                    case PASSWORD: changePassword(); break;
                    case LOGOUT: logout(); break;
                    default: System.out.println("\nInvalid selection."); break;
                }
            }
        }
        
        private void showTeacherUI() {
            while (activeUser != null) {
                switch (getRootMenuSelection()) {
                    case PASSWORD: resetPassword(); break;
                    case DATABASE: factoryReset(); break;
                    case LOGOUT: logout(); break;
                    case SHUTDOWN: shutdown(); break;
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
        	System.out.println("\n[1] Freshmen.");
            System.out.println("[2] Sophmore.");
            System.out.println("[3] Junior.");
            System.out.println("[4] Senior.");
            System.out.print("\n::: ");
            
            switch (Utils.getInt(in, -1)) {
            	case 1: return GradeList.FRESHMEN;
            	case 2: return GradeList.SOPHMORE;
            	case 3: return GradeList.JUNIOR;
            	case 4: return GradeList.SENIOR;
            default: return null;
        }
        }
        
        private void showGradeUI() throws ClassNotFoundException, SQLException {
            
            switch (displayGrades()) {
                case FRESHMEN: studentsGrade(1); break;
                case SOPHMORE: studentsGrade(2); break;
                case JUNIOR: studentsGrade(3); break;
                case SENIOR: studentsGrade(4); break;
                default: System.out.println("\nInvalid selection."); break;
            }
        }
        
        private void studentsGrade(int gradeLevel) throws ClassNotFoundException, SQLException {
        	gradeLevel += 8;
        	ArrayList<Student> students = PowerSchool.showStudents();
        	int counter = 1;
        	for(Student i: students) {
        		if (i.getGradeLevel() == gradeLevel) {
        			System.out.print("\n" + counter + ". ");
        			System.out.print(i.getLastName() + ", ");
        			System.out.print(i.getFirstName() + " / #");
        			System.out.print(i.getClassRank());
        			counter++;
        		}
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
		}
        
        private void showDepartmentUI() throws ClassNotFoundException, SQLException {
            
                switch (getDepartmentSelection()) {
                    case CS: selectDepartment(1); break;
                    case ENGLISH: selectDepartment(2); break;
                    case HISTORY: selectDepartment(3); break;
                    case MATH: selectDepartment(4); break;
                    case PHYS_ED: selectDepartment(5); break;
                    case SCIENCE: selectDepartment(6); break;
                    default: System.out.println("\nInvalid selection."); break;
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
                default: return null;
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
                default: return null;
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
            default: return null;
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
            default: return null;
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
            
            String response = "c";
            System.out.print("\nAre you sure you want to reset the password for " + usernameForReset + "? (y/n) ");
            response = in.next();
            if (response.equals("y")) {
            	PowerSchool.resetPassword(usernameForReset);
            	System.out.print("Successfully reset password for " + usernameForReset + ".");
            } else if (response.equals("n")) {
            } else {
            	System.out.print("Invalid input.");
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
            //
            // ask root user to confirm intent to reset the database
            //
            // if confirmed...
            //      call database initialize method with parameter of true
            //      print success message
            //
        	String response = "c";
            System.out.print("\nAre you sure you want to reset all settings and data? (y/n) ");
            response = in.next();
            if (response.equals("y")) {
            	PowerSchool.initialize(true);
            	System.out.println("Successfully reset database.");
            } else if (response.equals("n")) {
            } else {
            	System.out.println("Invalid input.");
            }
        }
        
        /*
         * Logs out of the application.
         */

        private void logout() {
            String response = "c";
            System.out.print("\nAre you sure? (y/n) ");
            response = in.next();
            if (response.equals("y")) {
            	startup();
            } else if (response.equals("n")) {
            	
            } else {
            	System.out.println("Invalid input.");
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
    	System.out.print("Enter current password: ");
    	String passwordCheck = in.next();
    	System.out.print("Enter new password: ");
    	String newPassword = in.next();
    	
    	if (!(passwordCheck.equals(activeUser.getPassword()))) {
    		System.out.println("Invalid current password.");
    	} else {
    		String newPass = activeUser.setPassword(newPassword);
			PowerSchool.updatePassword(newPass, activeUser.getUsername());
			System.out.println("Successfully changed password.");
    	}
    }

    public static void main(String[] args) {
        Application app = new Application();

        app.startup();
    }
}
