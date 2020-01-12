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

    public static ArrayList<Student> showStudentsAssignment(String assignment_id, int course_id) throws ClassNotFoundException, SQLException {
        ArrayList<Student> students = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.STUDENT_ASSIGNMENT)) {

        	  	stmt.setString(1, assignment_id);
        	  	stmt.setInt(2, course_id);
               
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
    public static ArrayList<Float> enumStudentCourseGrade(int course_id, Integer student_id) {
    	ArrayList<Float> gradesByMP = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_COURSE_GRADES)) {
        		stmt.setInt(1, course_id);
        		stmt.setInt(2, student_id);
        	
               try (ResultSet rs = stmt.executeQuery()) {
                
                   while (rs.next()) {
                	
                	  gradesByMP.add((float) rs.getInt("mp1"));
                	  gradesByMP.add((float) rs.getInt("mp2"));
                	  gradesByMP.add((float) rs.getInt("mp3"));
                	  gradesByMP.add((float) rs.getInt("mp4"));
                	  gradesByMP.add((float) rs.getInt("midterm_exam"));
                	  gradesByMP.add((float) rs.getInt("final_exam"));
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
        return gradesByMP;
      }
	
    public static ArrayList<String> showAssignmentGrade(int student_id, int course_id, int marking_period, int is_midterm, int is_final) throws ClassNotFoundException, SQLException {
        ArrayList<String> assignmentAndGrade = new ArrayList<>();
        try (Connection conn = getConnection();
               PreparedStatement stmt = conn.prepareStatement(QueryUtils.SHOW_ASSIGNMENT_GRADE)) {

        		stmt.setInt(1, student_id);
        		stmt.setInt(2, course_id);
        		stmt.setInt(3, marking_period);
        		stmt.setInt(4, is_midterm);
        		stmt.setInt(5, is_final);
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
    		  		//System.out.print(course_id + assignment_id + marking_period + is_midterm + is_final + title + point_value);
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
    
    public static Float getPointValue(int assignment_id, int course_id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_POINTS_POSSIBLE)) {
        	
        	stmt.setInt(1, assignment_id);
        	stmt.setInt(2, course_id);
            try (ResultSet rs = stmt.executeQuery()) {
             
                while (rs.next()) {
             	  return rs.getFloat("points");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		return null;
    }
    public static ArrayList<Float> calculateMPGrade (int marking_period, int is_midterm, int is_final, int student_id, int course_id) {
    	ArrayList<Float> assignmentGrades = new ArrayList<Float>();
    	try(Connection conn = getConnection();
    			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_MP_GRADE)) {
    			conn.setAutoCommit(false);
    			
    			stmt.setInt(1, student_id);
    			stmt.setInt(2, course_id);
    			stmt.setInt(3, marking_period);
    			stmt.setInt(4, is_midterm);
    			stmt.setInt(5, is_final);
    			try (ResultSet rs = stmt.executeQuery()) {
    	             
                    while (rs.next()) {
                 	  assignmentGrades.add(rs.getFloat("grade"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return assignmentGrades;
	    }
    public static int calculateAndSetCourseGrade (float mp1, float mp2, float mp3, float mp4, float final_exam, float midterm_exam, int student_id, int course_id, int realZero1, int realZero2, int realZero3,
	int realZero4,
	int realZero5,
	int realZero6) {
    	try(Connection conn = getConnection();
    			PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_COURSE_GRADE)) {
    			conn.setAutoCommit(false);
    			float total_weight = 0;
    			if (realZero1 == 1) {
    				total_weight += 0.2;
    			}
    			if (realZero2 == 1) {
    				total_weight += 0.2;
    			}
    			
    			if (realZero3 == 1) {
    				total_weight += 0.2;
    			}
    			
    			if (realZero4 == 1) {
    				total_weight += 0.2;
    			}
    			
    			if (realZero5 == 1) {
    				total_weight += 0.1;
    			}
    			if (realZero6 == 1) {
    				total_weight += 0.1;
    			}
    			
    			System.out.print(mp1 +" "+ mp2 +" "+ midterm_exam +" "+ mp3 +" "+ mp4 + " " + final_exam +" "+ student_id + " " + course_id);
    			float grade = (float) (((mp1 + mp2 + mp3 + mp4) * 0.2 + (midterm_exam + final_exam) * 0.1)/total_weight);
    			stmt.setFloat(1, grade);
    			stmt.setInt(2, student_id);
    			stmt.setInt(3, course_id);
    		
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
    public static ArrayList<Integer> showStudentsAs(int assignment_id, int course_id) throws ClassNotFoundException, SQLException {
        ArrayList<Integer> students = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_STUDENT_BY_ID)) {
        		stmt.setInt(1, assignment_id);
        		stmt.setInt(2, course_id);
        	
               try (ResultSet rs = stmt.executeQuery()) {
                
                   while (rs.next()) {
                	  students.add(rs.getInt("student_id"));
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
        return students;
      }
    public static int updateStudentCourseGrade(String marking_period,  int student_id, int course_id) throws ClassNotFoundException, SQLException{
		
  	  try (Connection conn = getConnection();
  	        	PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_COURSE_GRADE(marking_period))) {
  	            conn.setAutoCommit(false);

  	            
  	           
  	          
  	            stmt.setInt(1, student_id);
  	            stmt.setInt(2, course_id);
  	            
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
    public static ArrayList<Integer> getPrevMPGrades(int student_id, int course_id, int marking_period, int is_midterm, int is_final) {
    	ArrayList<Integer> prevMPgrades= new ArrayList<Integer>();
    	try(Connection conn = getConnection();
    			PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_PREV_AS_GRADE)) {
    			conn.setAutoCommit(false);
    			
    			stmt.setInt(1, student_id);
    			stmt.setInt(2, course_id);
    			stmt.setInt(3, marking_period);
    			stmt.setInt(4, is_midterm);
    			stmt.setInt(5, is_final);
    			
    			try (ResultSet rs = stmt.executeQuery()) {
    	             
                    while (rs.next()) {
                    	if (rs.getInt("is_graded") == 1) {
                    		prevMPgrades.add(rs.getInt("points_earned"));
                    	}
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return prevMPgrades;
    }
    
    public static int updateStudentMPGrade(String marking_period, float grade,  int student_id, int course_id) throws ClassNotFoundException, SQLException{
    		
    	  try (Connection conn = getConnection();
    	        	PreparedStatement stmt = conn.prepareStatement(QueryUtils.UPDATE_COURSE_GRADE(marking_period))) {
    	            conn.setAutoCommit(false);

    	            
    	           
    	            stmt.setFloat(1, grade);
    	            stmt.setInt(2, student_id);
    	            stmt.setInt(3, course_id);
    	            
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

    public static int gradeAssignment(int points_earned, int student_id, int assignment_id, int course_id) throws ClassNotFoundException, SQLException{
  	  try (Connection conn = getConnection();
  	        	PreparedStatement stmt = conn.prepareStatement(QueryUtils.ADD_GRADE)) {
  	            conn.setAutoCommit(false);

  	            stmt.setInt(1, points_earned);
  	            stmt.setInt(2, student_id);
  	            stmt.setInt(3, assignment_id);
  	            stmt.setInt(4, course_id);
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
    public static void delAssignment(int assignment_id, int course_id) throws ClassNotFoundException, SQLException{
  	  try (Connection conn = getConnection();
  	        	PreparedStatement stmt = conn.prepareStatement(QueryUtils.DEL_ASSIGNMENT)) {
  	            
  	            stmt.setInt(1, assignment_id);
  	            stmt.setInt(2, course_id);
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
    
    public static String getAssignmentGrade(int assignment_id, int student_id, int course_id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.SHOW_GRADE)) {

            stmt.setInt(1, assignment_id);
            stmt.setInt(2, student_id);
            stmt.setInt(3, course_id);

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
    public static int checkLastAId(int course_id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.GET_LAST_ID)) {
        	
        	stmt.setInt(1, course_id);
        	stmt.setInt(2, course_id);
        	
            try (ResultSet rs = stmt.executeQuery()) {
             
                if (rs.next()) {
             	  return rs.getInt("assignment_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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
    
    public static ArrayList<Student> studentByGpa() {
    	ArrayList<Student> students = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.STUDENT_BY_GPA)) {
        	

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
    
   
 
    public static void setStudentGpa(int studentId) {
        Student student = null;
        ArrayList<Double> courseGrades = new ArrayList<Double>();
        ArrayList<Integer> courseIds = new ArrayList<Integer>();
        ArrayList<Double> courseWeights = new ArrayList<Double>();
        try(Connection conn = getConnection()) {
            
            Statement stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENT_BY_STUDENT_ID_SQL(studentId))) {
                if (rs.next()) {
                    student = new Student(rs);
                }
            }

            stmt = conn.createStatement();
            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENT_GRADES_ALL_SQL(studentId))) {
                while (rs.next()) {
                    courseGrades.add(rs.getDouble("grade"));
                    courseIds.add(rs.getInt("course_id"));
                }
            }

            stmt = conn.createStatement();
            for (int courseId : courseIds) {
                try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_COURSES_SQL(courseId))) {
                    if (rs.next()) {
                        courseWeights.add(rs.getDouble("weight"));
                    }
                }
            }
            
            double totalWeight = 0;
            int numCourses = courseGrades.size();
            double calculatedGpa = -1.0;
            for (int i = 0; i < courseGrades.size(); i++) {
                totalWeight += courseWeights.get(i);
                calculatedGpa = courseGrades.get(i) * courseWeights.get(i);
            }

            if (courseGrades != null) {
                calculatedGpa = (((calculatedGpa / totalWeight) / numCourses) / 100) * 4.0;

            }

            student.setGPA(calculatedGpa);

            stmt = conn.createStatement();
            conn.setAutoCommit(false);
            stmt.executeUpdate("UPDATE students SET gpa = " + student.getGpa() + " WHERE student_id = " + student.getStudentId());
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
 

    public static ArrayList<Student> getStudentsByGrade(int grade) {
        ArrayList<Student> students = new ArrayList<Student>();

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENTS_BY_GRADE_SQL(grade))) {
                while (rs.next()) {
                    students.add(new Student(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }
   public static void updateCourseGrades(int student_id, int course_id) {
        String set = "";
        double mp1 = calculateMP(1, student_id, course_id);
        if (mp1 / mp1 == 1) set += ", mp1 = " + mp1;
        double mp2 = calculateMP(2, student_id, course_id);
        if (mp2 / mp2 == 1) set += ", mp2 = " + mp2;
        double mp3 = calculateMP(3, student_id, course_id);
        if (mp3 / mp3 == 1) set += ", mp3 = " + mp3;
        double mp4 = calculateMP(4, student_id, course_id);
        if (mp4 / mp4 == 1) set += ", mp4 = " + mp4;
        double midterm = calculateMP(5, student_id, course_id);
        if (midterm / midterm == 1) set += ", midterm_exam = " + midterm;
        double final_exam = calculateMP(6, student_id, course_id);
        if (final_exam / final_exam == 1) set += ", final_exam = " + final_exam;
        double grade = ((mp1 + mp2 + mp3 + mp4) * 0.2 + (midterm + final_exam) * 0.1);
        if (grade / grade == 1) set += ", grade = " + grade;

        

        String updateCG =
        "UPDATE course_grades " +
            "SET " + set.substring(2, set.length()) +
        " WHERE student_id = " + student_id + " AND course_id = " + course_id;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(updateCG);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static double calculateMP(int mp, int student_id, int course_id) {
        double ptsPoss = 0;
        double ptsEarned = 0;
        int mpVal = 0; int midtermVal = 0; int finalVal = 0;
        if (mp >= 1 && mp <= 4) {
            mpVal = mp;
            midtermVal = 0;
            finalVal = 0;
        } else if (mp == 5) {
            mpVal = 0;
            midtermVal = 1;
            finalVal = 0;
        } else if (mp == 6) {
            mpVal = 0;
            midtermVal = 0;
            finalVal = 1;
        }
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery("SELECT * FROM assignment_grades g, assignments a WHERE g.course_id = a.course_id AND g.assignment_id = a.assignment_id AND g.student_id = " + student_id + " AND g.course_id = " + course_id + " AND a.marking_period = " + mpVal + " AND a.is_midterm = " + midtermVal + " AND a.is_final = " + finalVal)) { 
                while (rs.next()) {
                    ptsPoss += rs.getInt("points_possible");
                    ptsEarned += rs.getInt("points_earned");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return (ptsEarned / ptsPoss);
    }

    public static ArrayList<Integer> getTeacherAssignmentPoints(User user, String course_no, int marking_period, int is_midterm, int is_final) {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_TEACHER_ASSIGNMENTS_SQL(((Teacher) user).getTeacherId(), course_no, marking_period, is_midterm, is_final))) {
                ArrayList<Integer> assignmentsPts = new ArrayList<Integer>();
                while (rs.next()) {
                    assignmentsPts.add(rs.getInt("point_value"));
                }
                return assignmentsPts;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    
  
	public static ArrayList<String> getGrades(int student_id) {
        ArrayList<String> grades = new ArrayList<String>();

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {

            try (ResultSet rs = stmt.executeQuery(QueryUtils.GET_STUDENT_GRADES_SQL(student_id))) {
                while (rs.next()) {
                    grades.add(new String(String.valueOf(rs)));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grades;
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
    
    public static ArrayList<String> checkAssignmentByTeacher(int course_id, int marking_period, int is_midterm, int is_final, int teacher_id) {
    	ArrayList<String> assignments = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(QueryUtils.SHOW_ASSIGNMENTS)) {

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
    
    public static Boolean resetPassword(String username) {
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
                    return false;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    	return true;
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

	public static void delAssignmentGrade(int assignment_id, int course_id) {
		try (Connection conn = getConnection();
  	        	PreparedStatement stmt = conn.prepareStatement(QueryUtils.DEL_ASSIGNMENT_GRADE)) {
  	            
  	           stmt.setInt(1, assignment_id);
  	          stmt.setInt(2, course_id);
  	          
  	            stmt.execute();
  	  			} catch (SQLException e) {
  	  				System.out.println(e.getMessage());
  	  			}

	}

	
}
