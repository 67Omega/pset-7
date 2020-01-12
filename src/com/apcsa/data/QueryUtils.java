package com.apcsa.data;

public class QueryUtils {

    /////// QUERY CONSTANTS ///////////////////////////////////////////////////////////////
    
    /*
     * Determines if the default tables were correctly loaded.
     */
	
    public static final String SETUP_SQL =
        "SELECT COUNT(name) AS names FROM sqlite_master " +
            "WHERE type = 'table' " +
        "AND name NOT LIKE 'sqlite_%'";
    
    /*
     * Updates the last login timestamp each time a user logs into the system.
     */

    public static final String LOGIN_SQL =
        "SELECT * FROM users " +
            "WHERE username = ?" +
        "AND auth = ?";
    
    /*
     * Updates the last login timestamp each time a user logs into the system.
     */

    public static final String UPDATE_LAST_LOGIN_SQL =
        "UPDATE users " +
            "SET last_login = ? " +
        "WHERE username = ?";
    
    /*
     * Updates password?
     */
    
    public static final String UPDATE_PASSWORD_SQL =
            "UPDATE users " +
                "SET auth = ? " +
            "WHERE username = ?";
    
    /*
     * Retrieves an administrator associated with a user account.
     */

    public static final String GET_ADMIN_SQL =
        "SELECT * FROM administrators " +
            "WHERE user_id = ?";
    
    /*
     * Retrieves a teacher associated with a user account.
     */

    public static final String GET_TEACHER_SQL =
        "SELECT * FROM teachers " +
            "WHERE user_id = ?";
    
    public static String GET_FACULTY_SQL = 
            "SELECT * FROM teachers " +
            	"INNER JOIN departments " +
            	"ON teachers.department_id = departments.department_id " +
            	"ORDER BY teachers.first_name";
    
    public static String GET_STUDENTS_SQL = 
            "SELECT * FROM students " +
    			"ORDER BY last_name";
    
    /*
     * Retrieves a student associated with a user account.
     */

    public static final String GET_STUDENT_SQL =
        "SELECT * FROM students " +
            "WHERE user_id = ?";
    
    public static String GET_STUDENTS_COURSE = 
    		"SELECT * FROM students AS s " +
    			"INNER JOIN course_grades g ON g.student_id = s.student_id " + 
    			"INNER JOIN courses c ON c.course_id = g.course_id " +
    			"WHERE c.course_no = ? " + 
    			"ORDER BY s.last_name";
    
    public static String GET_COURSE_NO =
    		"SELECT course_no FROM courses";
    
    public static String CHECK_COURSE_NO =
    		"SELECT course_no FROM courses " +
    				"WHERE course_id = ?"; 
    
    public static String GET_COURSE_ID =
    		"SELECT course_id FROM courses " +
    				"WHERE course_no = ?";
    
    public static String GET_COURSE_TEACHER =
    		"SELECT * FROM courses AS c " +
    				"INNER JOIN teachers t " +
    				"ON t.teacher_id = c.teacher_id " +
    				"WHERE t.teacher_id = ?";
    
    public static String GET_COURSE_STUDENT =
    		"SELECT c.course_no FROM students AS s " +
        			"INNER JOIN course_grades g ON g.student_id = s.student_id " + 
        			"INNER JOIN courses c ON c.course_id = g.course_id " +
        			"WHERE s.student_id = ? ";
    public static String GET_STUDENT_GRADE = 
    		"SELECT c.grade FROM course_grades c " +
    				"INNER JOIN students s " +
    				"ON s.student_id = c.student_id " +
    				"WHERE (c.course_id = ? AND s.student_id = ?)";
    
    public static String ADD_ASSIGNMENT = 
    		"INSERT INTO assignments " +
    				"VALUES (?, ?, ?, ?, ?, ?, ?)";
    				
    public static String SHOW_ASSIGNMENTS = 
    	"SELECT title, point_value, assignment_id FROM assignments " +
    			"WHERE course_id = ? AND marking_period = ? AND is_midterm = ? AND is_final = ?";
    
    public static String DEL_ASSIGNMENT = 
    		"DELETE FROM assignments " +
    				"WHERE assignment_id = ? AND course_id = ?";
    
    public static String ADD_GRADE = 
    		"UPDATE assignment_grades " +
    	    		"SET points_earned = ?, is_graded = 1 " +
    	    		"WHERE (student_id = ? AND assignment_id = ? AND course_id = ?)";
    public static String GET_LAST_ID = 
    		"SELECT assignment_id FROM assignments " + 	
    				"WHERE assignment_id = ( SELECT max(assignment_id) FROM assignments WHERE course_id = ?) AND course_id = ?";
    
    public static final String ADD_STUDENT_TO_ASSIGNMENT = 
    		"INSERT INTO assignment_grades(course_id, assignment_id, student_id, points_possible, is_graded) " +
    	    		"VALUES (?, ?, ?, ?, 0)";
    public static String GET_STUDENTS_BY_GRADE_SQL(int grade) {
        return "SELECT * FROM students " +
        "WHERE grade_level = " + String.valueOf(grade) + " " +
        "ORDER BY student_id";//"ORDER BY last_name, first_name";
    }
    public static String GET_TEACHER_ASSIGNMENTS_SQL(int teacher_id, String course_no, int marking_period, int is_midterm, int is_final) {
        return "SELECT * FROM assignments a, courses c, teachers t " +
        "WHERE c.teacher_id = t.teacher_id AND c.course_id = a.course_id " +
        "AND t.teacher_id = " + teacher_id + " " +
        "AND a.course_id = " + PowerSchool.checkCourseId(course_no) + " " +
        "AND a.marking_period = " + marking_period + " " +
        "AND a.is_midterm = " + is_midterm + " " +
        "AND a.is_final = " + is_final + " " +
        "ORDER BY a.assignment_id";
    }
	public static final String DEL_ASSIGNMENT_GRADE = 
			"DELETE FROM assignment_grades WHERE assignment_id = ? AND course_id = ?";
    
    public static String STUDENT_ASSIGNMENT =
    		"SELECT * FROM students s " +
    				"INNER JOIN assignment_grades a " +
    				"ON s.student_id  = a.student_id " +
    				"WHERE a.assignment_id = ? AND a.course_id = ?";
    public static String GET_STUDENT_BY_ID =
    		"SELECT s.student_id FROM students s " +
    				"INNER JOIN assignment_grades a " +
    				"ON s.student_id  = a.student_id " +
    				"WHERE a.assignment_id = ? AND a.course_id = ?";
    
    public static String STUDENT_BY_GPA = 
    		"SELECT * FROM students " + 
    		"ORDER BY gpa";
    public static String UPDATE_COURSE_GRADE(String mp) {
        return "UPDATE course_grades " +
        "SET "+ mp +" =  ? " +
        "WHERE student_id = ? AND course_id = ?";
    }
    public static String UPDATE_COURSE_GRADE =
        "UPDATE course_grades " +
        "SET grade =  ? " +
        "WHERE student_id = ? AND course_id = ?";
        
    
    public static String GET_POINTS_POSSIBLE = 
    		"SELECT CAST(point_value AS float) AS points FROM assignments " +
    				"WHERE assignment_id = ? AND course_id = ?";
    public static String GET_MP_GRADE =
    		"SELECT CAST(g.points_earned AS float) / CAST(a.point_value AS float) * 100 AS grade " + 
    				"FROM assignments AS a " + 
    				"INNER JOIN assignment_grades g " + 
    				"ON g.assignment_id = a.assignment_id AND g.course_id = a.course_id " + 
    				"WHERE g.student_id = ? AND g.course_id = ? AND a.marking_period = ? " + 
    				"AND is_midterm = ? AND is_final = ?";
    public static String SHOW_GRADE =
    		"SELECT a.points_earned FROM assignment_grades a " +
    				"INNER JOIN students s " +
    				"ON s.student_id = a.student_id " +
    				"WHERE a.assignment_id = ? AND s.student_id = ? AND a.course_id = ?";
    public static String STUDENT_COURSE_GRADE = 
    		"SELECT ifnull(grade, -1) AS grade FROM course_grades "
    		+ "WHERE course_id = ? AND student_id = ?";
    
    public static String SHOW_COURSE_GRADE =
    		"SELECT c.title, g.grade " +
    				"FROM courses c " +
    				"INNER JOIN  course_grades g " +
    				"ON c.course_id = g.course_id " +
    				"WHERE g.student_Id = ?";
    public static String GET_COURSES_SQL(int courseId) {
        return "SELECT * FROM courses " + 
        "WHERE course_id = " + String.valueOf(courseId);
    }
    public static String GET_STUDENT_BY_STUDENT_ID_SQL(int studentId) {
        return "SELECT * FROM students " + 
        "WHERE student_id = " + String.valueOf(studentId) + " " +
        "ORDER BY last_name, first_name";
    }

    public static String GET_STUDENT_ID =
    		"SELECT student_id FROM students " +
    				"WHERE user_id = ?";
    public static String UPDATE_CLASS_RANK_SQL(int studentId, int classRank) {
        return 
        "UPDATE students " + 
            "SET class_rank = " + String.valueOf(classRank) + " " +
        "WHERE student_id = " + String.valueOf(studentId);

    }
    public static String GET_STUDENT_GRADES_SQL(int studentId) {
        return "SELECT grade FROM course_grades " +
        "WHERE student_id = " + String.valueOf(studentId) + " " +
        "ORDER BY course_id";
    } 
    public static String SHOW_ASSIGNMENT_GRADE =
    		"SELECT a.title, g.points_earned, a.point_value " +
    				"FROM assignments AS a " +
    				"INNER JOIN assignment_grades g " +
    				"ON g.assignment_id = a.assignment_id AND g.course_id = a.course_id " +
    				"WHERE g.student_id = ? AND g.course_id = ? AND a.marking_period = ? " +
    				"AND is_midterm = ? AND is_final = ?";

	public static String GET_PREV_AS_GRADE = 
			"SELECT CAST(g.points_earned AS float) / CAST(a.point_value AS float) * 100, g.is_graded " + 
			    	"FROM assignments AS a " + 
			   		"INNER JOIN assignment_grades g " + 
			   		"ON g.assignment_id = a.assignment_id AND g.course_id = a.course_id " + 
			   		"WHERE g.student_id = ? AND g.course_id = ? AND a.marking_period = ? " + 
			   		"AND is_midterm = ? AND is_final = ?";

	public static String GET_COURSE_GRADES = 
			"SELECT mp1, mp2, mp3, mp4, midterm_exam, final_exam " +
					"FROM course_grades " +
					"WHERE course_id = ? AND student_id = ?";
			
    public static final String UPDATE_COURSE_GRADE_STUDENT = "UPDATE course_grades SET mp1 = ?, mp2 = ?, midterm_exam = ?, mp3 = ?, mp4 = ?, final_exam = ?, grade = ? " +
    		"WHERE course_id = ? AND student_id = ?";
    public static String ENTER_GRADE_SQL(int course_id, int assignment_id, int student_id, int points_earned, int points_possible) {
        return "INSERT INTO assignment_grades " +
        "(course_id, assignment_id, student_id, points_earned, points_possible, is_graded) " +
        "VALUES (" + course_id + ", " + assignment_id + ", " + student_id + ", " +
                    points_earned + ", " + points_possible + ", " + 1 + ")";
    }
    public static String GET_STUDENT_GRADES_ALL_SQL(int studentId) {
        return "SELECT * FROM course_grades " + 
        "WHERE student_id = " + String.valueOf(studentId) + " " + 
        "ORDER BY course_id";
    }
    
}


