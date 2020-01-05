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
    		"SELECT s.student_id, s.first_name, s.last_name, c.course_no FROM students AS s" +
    			"INNER JOIN course_grades g ON g.student_id = s.student_id" + 
    			"INNER JOIN courses c ON c.course_id = g.course_id " +
    			"GROUP BY c.course_no";
    				
    				
    
}
