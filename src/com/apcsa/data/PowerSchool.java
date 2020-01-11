package com.apcsa.data;

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
import com.apcsa.model.Administrator;
import com.apcsa.model.Student;
import com.apcsa.model.Teacher;
import com.apcsa.model.User;

public class PowerSchool {

    private final static String PROTOCOL = "jdbc:sqlite:";
    private final static String DATABASE_URL = "data/powerschool.db";

    /**
     * Initializes the database if needed (or if requested).
     *
     * @param force whether or not to force-reset the database
     * @throws Exception
     */

    public static void initialize(boolean force) {
        if (force) {
            reset();    // force reset
        } else {
            boolean required = false;

            // check if all tables have been created and loaded in database

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(QueryUtils.SETUP_SQL)) {

                while (rs.next()) {
                    if (rs.getInt("names") != 9) {
                        required = true;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // build database if needed

            if (required) {
                reset();
            }
        }
    }

    /**
     * Retrieves the User object associated with the requested login.
     *
     * @param username the username of the requested User
     * @param password the password of the requested User
     * @return the User object for valid logins; null for invalid logins
     */

    public static User login(String username, String password) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.LOGIN_SQL)) {
            stmt.setString(1, username);
            stmt.setString(2, Utils.getHash(password));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = new Timestamp(new Date().getTime());
                    int affected = PowerSchool.updateLastLogin(conn, username, ts.toString());

                    if (affected != 1) {
                        System.err.println("Unable to update last login (affected rows: " + affected + ").");
                    }
                    return new User(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Teacher> showFaculty() throws ClassNotFoundException, SQLException {
        ArrayList<Teacher> teachers = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_FACULTY_SQL)) {

               try (ResultSet rs = stmt.executeQuery()) {
                
                   while (rs.next()) {
                	  teachers.add(new Teacher(rs));
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
        return teachers;
      }

    public static ArrayList<Student> showStudentsAssignment(String assignment_id) throws ClassNotFoundException, SQLException {
        ArrayList<Student> students = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.STUDENT_ASSIGNMENT)) {

        	  	stmt.setString(1, assignment_id);
               
        	  	try (ResultSet rs = stmt.executeQuery()) {
                
                   while (rs.next()) {
                	  students.add(new Student(rs));
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
        return students;
      }
    public static ArrayList<Student> showStudentsCourse(String course_no) throws ClassNotFoundException, SQLException {
        ArrayList<Student> students = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENTS_COURSE)) {

        	  	stmt.setString(1, course_no);
               
        	  	try (ResultSet rs = stmt.executeQuery()) {
                
                   while (rs.next()) {
                	  students.add(new Student(rs));
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
        return students;
      }
    
    public static ArrayList<Student> showStudents() throws ClassNotFoundException, SQLException {
        ArrayList<Student> students = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENTS_SQL)) {

               try (ResultSet rs = stmt.executeQuery()) {
                
                   while (rs.next()) {
                	  students.add(new Student(rs));
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
        return students;
      }

    public static ArrayList<String> showCourseGrade(int student_id) throws ClassNotFoundException, SQLException {
        ArrayList<String> coursesAndGrade = new ArrayList<>();
        try (Connection conn = getConnection();
               PreparedStatement stmt = conn.prepareStatement(QueryUtils.SHOW_COURSE_GRADE)) {

        		stmt.setInt(1, student_id);
        	
               try (ResultSet rs = stmt.executeQuery()) {
                
                   while (rs.next()) {
                	  coursesAndGrade.add(rs.getString("title"));
                	  coursesAndGrade.add(rs.getString("grade"));
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
        return coursesAndGrade;
      }
    
    public static ArrayList<String> showAssignmentGrade(int student_id) throws ClassNotFoundException, SQLException {
        ArrayList<String> assignmentAndGrade = new ArrayList<>();
        try (Connection conn = getConnection();
               PreparedStatement stmt = conn.prepareStatement(QueryUtils.SHOW_ASSIGNMENT_GRADE)) {

        		stmt.setInt(1, student_id);
        	
               try (ResultSet rs = stmt.executeQuery()) {
                
                   while (rs.next()) {
                	  assignmentAndGrade.add(rs.getString("title"));
                	  assignmentAndGrade.add(rs.getString("points_earned"));
                	  assignmentAndGrade.add(rs.getString("point_value"));
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
        return assignmentAndGrade;
      }
    /**
     * Returns the administrator account associated with the user.
     *
     * @param user the user
     * @return 
     * @return the administrator account if it exists
     */
    public static int addAssignment(int course_id, int assignment_id, int marking_period, int is_midterm, int is_final, String title, int point_value) throws ClassNotFoundException, SQLException{
    	  try (Connection conn = getConnection();
    	        	PreparedStatement stmt = conn.prepareStatement(QueryUtils.ADD_ASSIGNMENT)) {
    	            conn.setAutoCommit(false);
    	            stmt.setInt(1, course_id);
    	            stmt.setInt(2, assignment_id);
    				stmt.setInt(3, marking_period);
    				stmt.setInt(4, is_midterm);
    				stmt.setInt(5, is_final);
    				stmt.setString(6, title);
    				stmt.setInt(7, point_value);
    	            if (stmt.executeUpdate() == 1) {
    	                conn.commit();

    	                return 1;
    	            } else {
    	                conn.rollback();

    	                return -1;
    	            }
    	        } catch (SQLException e) {
    	            e.printStackTrace();

    	            return -1;
    	        }
    	    }
    public static int addStudentToAssignment(int course_id, int assignment_id, int student_id, int point_value) throws ClassNotFoundException, SQLException{
  	  try (Connection conn = getConnection();
  	        	PreparedStatement stmt = conn.prepareStatement(QueryUtils.ADD_STUDENT_TO_ASSIGNMENT)) {
  	            conn.setAutoCommit(false);
  	            stmt.setInt(1, course_id);
  	            stmt.setInt(2, assignment_id);
  				stmt.setInt(3, student_id);
  				stmt.setInt(4, point_value);
  				if (stmt.executeUpdate() == 1) {
  	                conn.commit();

  	                return 1;
  	            } else {
  	                conn.rollback();

  	                return -1;
  	            }
  	        } catch (SQLException e) {
  	            e.printStackTrace();

  	            return -1;
  	        }
  	    }
  
    
    public static int gradeAssignment(int points_earned, int student_id, int assignment_id) throws ClassNotFoundException, SQLException{
  	  try (Connection conn = getConnection();
  	        	PreparedStatement stmt = conn.prepareStatement(QueryUtils.ADD_GRADE)) {
  	            conn.setAutoCommit(false);
  	            stmt.setInt(1, points_earned);
  	            stmt.setInt(2, student_id);
  	            stmt.setInt(3, assignment_id);
  	            if (stmt.executeUpdate() == 1) {
  	                conn.commit();

  	                return 1;
  	            } else {
  	                conn.rollback();

  	                return -1;
  	            }
  	        } catch (SQLException e) {
  	            e.printStackTrace();

  	            return -1;
  	        }
  	    }
    public static void delAssignment(int assignment_id) throws ClassNotFoundException, SQLException{
  	  try (Connection conn = getConnection();
  	        	PreparedStatement stmt = conn.prepareStatement(QueryUtils.DEL_ASSIGNMENT)) {
  	            
  	            stmt.setInt(1, assignment_id);
 
  	            stmt.execute();
  	  			} catch (SQLException e) {
  	  				System.out.println(e.getMessage());
  	  			}

  	}
  	
    public static User getAdministrator(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_ADMIN_SQL)) {

            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Administrator(user, rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
    
    public static String getGrade(int course_id, int student_id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_GRADE)) {

            stmt.setInt(1, course_id);
            stmt.setInt(2, student_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("grade");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static int getStudentId(int user_id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_ID)) {

            stmt.setInt(1, user_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("student_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public static String getAssignmentGrade(int assignment_id, int student_id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.SHOW_GRADE)) {

            stmt.setInt(1, assignment_id);
            stmt.setInt(2, student_id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("points_earned");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static ArrayList<String> checkCourseNo() {
    	ArrayList<String> courses = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_COURSE_NO)) {
        	

            try (ResultSet rs = stmt.executeQuery()) {
             
                while (rs.next()) {
             	  courses.add(rs.getString("course_no"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    

	public static int checkCourseId(String course_no) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_COURSE_ID)) {
        	
        	stmt.setString(1, course_no);

            try (ResultSet rs = stmt.executeQuery()) {
             
                while (rs.next()) {
             	  return rs.getInt("course_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return 0;
    }
	public static String getCourseNo(int course_id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.CHECK_COURSE_NO)) {
        	
        	stmt.setInt(1, course_id);

            try (ResultSet rs = stmt.executeQuery()) {
             
                while (rs.next()) {
             	  return rs.getString("course_no");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return "";
    }
    public static ArrayList<String> checkCourseByTeacher(int teacher_id) {
    	ArrayList<String> courses = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_COURSE_TEACHER)) {
        	
        	stmt.setInt(1, teacher_id);
        	
            try (ResultSet rs = stmt.executeQuery()) {
             
                while (rs.next()) {
             	  courses.add(rs.getString("course_no"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    
    public static ArrayList<String> checkCourseByStudent(int student_id) {
    	ArrayList<String> courses = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_COURSE_STUDENT)) {
        	
        	stmt.setInt(1, student_id);
        	
            try (ResultSet rs = stmt.executeQuery()) {
             
                while (rs.next()) {
             	  courses.add(rs.getString("course_no"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }
    
    public static ArrayList<String> checkAssignmentByTeacher(int course_id, int marking_period, int is_midterm, int is_final) {
    	ArrayList<String> assignments = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.SHOW_ASSIGNMENTS)) {
        	System.out.print(course_id + " " + marking_period + " " + is_midterm + " " + is_final);
        	conn.setAutoCommit(false);
            stmt.setInt(1, course_id);
			stmt.setInt(2, marking_period);
			stmt.setInt(3, is_midterm);
			stmt.setInt(4, is_final);
            try (ResultSet rs = stmt.executeQuery()) {
             
                while (rs.next()) {
             	 assignments.add(rs.getString("title"));
             	 assignments.add(rs.getString("point_value"));
             	 assignments.add(rs.getString("assignment_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return assignments;
    }
    
    public static void resetPassword(String username) {
        //
        // get a connection to the database
        // create a prepared statement (both of thses should go in a try-with-resources statement)
        //
        // insert parameters into the prepared statement
        //      - the user's hashed username
        //      - the user's plaintext username
        //
        // execute the update statement
        //
    	String password = Utils.getHash(username);
    	try (Connection conn = getConnection();
            	PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_PASSWORD_SQL)) {
                conn.setAutoCommit(false);
                stmt.setString(1, password);
    			stmt.setString(2, username);
    			updateLastLogin(conn, username, "0000-00-00 00:00:00.000");
    			
                if (stmt.executeUpdate() == 1) {
                    conn.commit();
                } else {
                    conn.rollback();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
    
    /**
     * Returns the teacher account associated with the user.
     *
     * @param user the user
     * @return the teacher account if it exists
     */

    public static User getTeacher(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_TEACHER_SQL)) {

            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Teacher(user, rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Returns the student account associated with the user.
     *
     * @param user the user
     * @return the student account if it exists
     */

    public static User getStudent(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_SQL)) {

            stmt.setInt(1, user.getUserId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(user, rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /*
     * Establishes a connection to the database.
     *
     * @return a database Connection object
     * @throws SQLException
     */

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(PROTOCOL + DATABASE_URL);
    }

    /*
     * Updates the last login time for the user.
     *
     * @param conn the current database connection
     * @param username the user's username
     * @param ts the current timestamp
     * @return the number of affected rows
     */

    private static int updateLastLogin(Connection conn, String username, String ts) {
        try (PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_LAST_LOGIN_SQL)) {

            conn.setAutoCommit(false);
            stmt.setString(1, ts);
            stmt.setString(2, username);

            if (stmt.executeUpdate() == 1) {
                conn.commit();

                return 1;
            } else {
                conn.rollback();

                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();

            return -1;
        }
    }
    
    

    public static int updatePassword(String password, String username) {
        try (Connection conn = getConnection();
        	PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_PASSWORD_SQL)) {
            conn.setAutoCommit(false);
            stmt.setString(1, password);
			stmt.setString(2, username);

            if (stmt.executeUpdate() == 1) {
                conn.commit();
                return 1;
            } else {
                conn.rollback();

                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();

            return -1;
        }
    }

    /*
     * Builds the database. Executes a SQL script from a configuration file to
     * create the tables, setup the primary and foreign keys, and load sample data.
     */

    private static void reset() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             BufferedReader br = new BufferedReader(new FileReader(new File("config/setup.sql")))) {

            String line;
            StringBuffer sql = new StringBuffer();

            // read the configuration file line-by-line to get SQL commands

            while ((line = br.readLine()) != null) {
                sql.append(line);
            }

            // execute SQL commands one-by-one

            for (String command : sql.toString().split(";")) {
                if (!command.strip().isEmpty()) {
                    stmt.executeUpdate(command);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: Unable to load SQL configuration file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error: Unable to open and/or read SQL configuration file.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error: Unable to execute SQL script from configuration file.");
            e.printStackTrace();
        }
    }
}
