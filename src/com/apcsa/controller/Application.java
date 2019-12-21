package com.apcsa.controller;

import java.util.Scanner;
import com.apcsa.data.PowerSchool;
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
import java.util.Date;
import com.apcsa.controller.Utils;

public class Application {

    private Scanner in;
    private User activeUser;
    enum RootAction { PASSWORD, DATABASE, LOGOUT, SHUTDOWN }
    enum StudentAction { COURSEGRADE, ASSIGNMENTGRADE, PASSWORD, LOGOUT }
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
    }

    /**
     * Starts the PowerSchool application.
     * @param conn 
     */

    public void startup() {
        System.out.println("PowerSchool -- now for students, teachers, and school administrators!");

        // continuously prompt for login credentials and attempt to login

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

        public void createAndShowUI() {
            System.out.println("\nHello, again, " + activeUser.getFirstName() + "!");

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
        
        private void showAdministratorUI() {
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
        /*
         * Retrieves a root user's menu selection.
         * 
         * @return the menu selection
         */

        private RootAction getRootMenuSelection() {
            System.out.println();
            
            System.out.println("[1] Reset user password.");
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
            
            System.out.println("[1] View course grades.");
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
            //
            // prompt root user to enter username of user whose password needs to be reset
            //
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
        }
        
        /*
         * Logs out of the application.
         */

        private void logout() {
            String response = "c";
            System.out.println("Are you sure? (y/n)");
            response = in.next();
            if (response.equals("y")) {
            	
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
