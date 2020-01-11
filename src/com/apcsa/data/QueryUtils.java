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
    				"WHERE assignment_id = ?";
    
    public static String ADD_GRADE = 
    		"UPDATE assignment_grades " +
    	    		"SET points_earned = ? " +
    	    		"WHERE student_id = ? AND assignment_id = ?";
    
    public static final String ADD_STUDENT_TO_ASSIGNMENT = 
    		"INSERT INTO assignment_grades(course_id, assignment_id, student_id, points_possible) " +
    	    		"VALUES (?, ?, ?, ?)";
    
    public static String STUDENT_ASSIGNMENT =
    		"SELECT * FROM students s " +
    				"INNER JOIN assignment_grades a " +
    				"ON s.student_id  = a.student_id " +
    				"WHERE a.assignment_id = ?";
    public static String SHOW_GRADE =
    		"SELECT a.points_earned FROM assignment_grades a " +
    				"INNER JOIN students s " +
    				"ON s.student_id = a.student_id " +
    				"WHERE (a.assignment_id = ? AND s.student_id = ?)";

    
    public static String SHOW_COURSE_GRADE =
    		"SELECT c.title, g.grade " +
    				"FROM courses c " +
    				"INNER JOIN  course_grades g " +
    				"ON c.course_id = g.course_id " +
    				"WHERE g.student_Id = ?";
    
    public static String GET_STUDENT_ID =
    		"SELECT student_id FROM students " +
    				"WHERE user_id = ?";
    
    public static String SHOW_ASSIGNMENT_GRADE =
    		"SELECT a.title, g.points_earned, a.point_value " +
    				"FROM assignments AS a " +
    				"INNER JOIN assignment_grades g " +
    				"ON g.assignment_id = a.assignment_id " +
    				"WHERE g.student_id = ?";
}

