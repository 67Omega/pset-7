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
    
    public static String GET_COURSE_ID =
    		"SELECT course_id FROM courses " +
    				"WHERE course_no = ?";
    
    public static String GET_COURSE_TEACHER =
    		"SELECT * FROM courses AS c " +
    				"INNER JOIN teachers t " +
    				"ON t.teacher_id = c.teacher_id " +
    				"WHERE t.teacher_id = ?";
    
    public static String GET_STUDENT_GRADE = 
    		"SELECT c.grade FROM course_grades c " +
    				"INNER JOIN students s " +
    				"ON s.student_id = c.student_id " +
    				"WHERE (c.course_id = ? AND s.student_id = ?)";
    
    public static String ADD_ASSIGNMENT = 
    		"INSERT INTO assignments " +
    				"VALUES (?, ?, ?, ?, ?, ?, ?)";
    				
    public static String SHOW_ASSIGNMENTS = 
    	"SELECT title, points " +
    			"WHERE course_id = ? AND marking_period = ? AND is_midterm = ? AND is_final = ?";
    
    public static String DEL_ASSIGNMENT = 
    		"DELETE assignments " +
    				"VALUES course_id = ? AND marking_period = ? AND is_midterm = ? AND is_final = ?";
}
