
import java.io.*;
import java.sql.*;
import java.util.*;

public class StudentManagementSystem {

    private static Scanner scanner = new Scanner(System.in);
    private static Connection connection;
    private static String userRole;
    private static int studentid;

    public static void main(String[] args) {
        try {
            connect();
            createTables();

            if (login()) {
                while (true) {
                    clear();
                    System.out.println("~~ Student Management System ~~");

                    if (userRole.equals("admin")) {
                        displayAdminMenu();
                    } else if (userRole.equals("teacher")) {
                        displayTeacherMenu();
                    } else if (userRole.equals("student")) {
                        displayStudentMenu();
                    }

                    int choice = scanner.nextInt();
                    scanner.nextLine();  // Consume newline

                    switch (choice) {
                        case 1:
                            if (userRole.equals("admin")) {
                                addStudent(); 
                            }else if (userRole.equals("teacher")) {
                                addStudent(); 
                            }else if (userRole.equals("student")) {
                                viewmydetials();
                            }
                            break;
                        case 2:
                            if (userRole.equals("admin")) {
                                viewStudents(); 
                            }else if (userRole.equals("teacher")) {
                                viewStudents(); 
                            }else if (userRole.equals("student")) {
                                searchStudent();
                            }
                            break;
                        case 3:
                            if (userRole.equals("admin")) {
                                searchStudent(); 
                            }else if (userRole.equals("teacher")) {
                                searchStudent(); 
                            }else if (userRole.equals("student")) {
                                updateStudent();
                            }
                            break;
                        case 4:
                            if (userRole.equals("admin")) {
                                updateStudent(); 
                            }else if (userRole.equals("teacher")) {
                                updateStudent(); 
                            }else if (userRole.equals("student")) {
                                sortStudents();
                            }
                            break;
                        case 5:
                            if (userRole.equals("admin")) {
                                deleteStudent(); 
                            }else if (userRole.equals("teacher")) {
                                deleteStudent(); 
                            }else if (userRole.equals("student")) {
                                exportDataToCSV();
                            }
                            break;
                        case 6:
                            if (userRole.equals("admin")) {
                                sortStudents(); 
                            }else if (userRole.equals("teacher")) {
                                sortStudents();
                            }
                            break;
                        case 7:
                            if (userRole.equals("admin")) {
                                addStudentGrade(); 
                            }else if (userRole.equals("teacher")) {
                                addStudentGrade();
                            }
                            break;
                        case 8:
                            if (userRole.equals("admin")) {
                                addAttendance(); 
                            }else if (userRole.equals("teacher")) {
                                addAttendance();
                            }
                            break;
                        case 9:
                            if (userRole.equals("admin")) {
                                manageCourses(); 
                            }else if (userRole.equals("teacher")) {
                                manageCourses();
                            }
                            break;
                        case 10:
                            if (userRole.equals("admin")) {
                                manageTeachers(); 
                            }else if (userRole.equals("teacher")) {
                                exportDataToCSV();
                            }
                            break;
                        case 11:
                            if (userRole.equals("admin")) {
                                exportDataToCSV();
                            }
                            break;
                        case 0:
                            main(args);
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }

                    System.out.println("Press Enter to continue...");
                    scanner.nextLine();
                }
            } else {
                System.out.println("Authentication failed because of invalid ID/Name");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void connect() throws SQLException {
        String url = "jdbc:sqlite:students.db";
        connection = DriverManager.getConnection(url);
    }

    private static void disconnect() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    private static void createTables() throws SQLException {
        String[] sqlStatements = {
            "CREATE TABLE IF NOT EXISTS students (id INTEGER PRIMARY KEY, name TEXT NOT NULL, age INTEGER NOT NULL, course_id INTEGER, FOREIGN KEY(course_id) REFERENCES courses(id))",
            "CREATE TABLE IF NOT EXISTS teachers (id INTEGER PRIMARY KEY, name TEXT NOT NULL, course_id INTEGER, FOREIGN KEY(course_id) REFERENCES courses(id))",
            "CREATE TABLE IF NOT EXISTS courses (id INTEGER PRIMARY KEY, name TEXT NOT NULL)",
            "CREATE TABLE IF NOT EXISTS exams (id INTEGER PRIMARY KEY, period INTEGER NOT NULL, exam_number INTEGER NOT NULL, course_id INTEGER, FOREIGN KEY(course_id) REFERENCES courses(id))",
            "CREATE TABLE IF NOT EXISTS student_grades (id INTEGER PRIMARY KEY AUTOINCREMENT, student_id INTEGER NOT NULL, exam_id INTEGER NOT NULL, grade REAL NOT NULL, FOREIGN KEY(student_id) REFERENCES students(id), FOREIGN KEY(exam_id) REFERENCES exams(id))",
            "CREATE TABLE IF NOT EXISTS admins (id INTEGER PRIMARY KEY, name TEXT NOT NULL)"
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : sqlStatements) {
                stmt.execute(sql);
            }
        }

        String insertAdmin = "INSERT OR IGNORE INTO admins (id, name) VALUES (1, 'admin')";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(insertAdmin);
        }
    }

    private static boolean login() throws SQLException {
        clear();
        System.out.println("~~ Login as ~~");
        System.out.println("1.admin");
        System.out.println("2.teacher");
        System.out.println("3.student");
        System.out.println("0.Exit");
        String role = "";
        while (!role.equals("admin") && !role.equals("teacher") && !role.equals("student")) {
            System.out.print("Enter role : ");
            int rolenum = scanner.nextInt();
            switch (rolenum) {
                case 1:
                    role = "admin";
                    break;
                case 2:
                    role = "teacher";
                    break;
                case 3:
                    role = "student";
                    break;
                case 0:
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        clear();
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        userRole = role;
        return authenticate(role, id, name);
    }

    private static boolean authenticate(String role, int id, String name) throws SQLException {
        String tableName = role + "s";
        if (role.equals("student")) {
            studentid = id;
        }

        String sql = "SELECT * FROM " + tableName + " WHERE id = ? AND name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();  // If a row is returned, authentication is successful
        } catch (SQLException e) {
            System.out.println("Error during authentication: " + e.getMessage());
            return false;
        }
    }

    private static void displayAdminMenu() {
        System.out.println("1. Add Student");
        System.out.println("2. View Students");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Sort Students");
        System.out.println("7. Add Grades");
        System.out.println("8. Attendance");
        System.out.println("9. Manage Courses");
        System.out.println("10.Manage Teachers");
        System.out.println("11.Export Students Data to CSV");
        System.out.println("0. Logout");
        System.out.print("Choose an option: ");
    }

    private static void displayTeacherMenu() {
        System.out.println("1. Add Student");
        System.out.println("2. View Students");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Sort Students");
        System.out.println("7. Add Grades");
        System.out.println("8. Attendance");
        System.out.println("9. Manage Courses");
        System.out.println("10.Export Students Data to CSV");
        System.out.println("0. Logout");
        System.out.print("Choose an option: ");
    }

    private static void displayStudentMenu() {
        System.out.println("1. View my details");
        System.out.println("2. Search for other Students");
        System.out.println("3. Update My Details");
        System.out.println("4. Sort Students");
        System.out.println("5. Export Students Data to CSV");
        System.out.println("0. Logout");
        System.out.print("Choose an option: ");
    }

    private static void addStudent() throws SQLException {
        clear();
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (isIdExist(id, "students")) {
            System.out.println("ID already exists. Please enter a unique ID.");
            return;
        }

        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.println("Available courses:");
        viewCourses();
        System.out.print("Enter course ID: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (!isIdExist(courseId, "courses")) {
            System.out.println("Course ID does not exist.");
            return;
        }

        String sql = "INSERT INTO students(id, name, age, course_id) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, age);
            pstmt.setInt(4, courseId);
            pstmt.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    private static void viewStudents() {
        clear();
        String sql = "SELECT students.id, students.name, students.age, courses.name AS course, "
                + "(SELECT AVG(grade) FROM student_grades sg JOIN exams e ON sg.exam_id = e.id WHERE sg.student_id = students.id) AS grade "
                + " FROM students JOIN courses ON students.course_id = courses.id";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.print("ID: " + rs.getInt("id"));
                System.out.print(" Name: " + rs.getString("name"));
                System.out.print(" Age: " + rs.getInt("age"));
                System.out.print(" Course: " + rs.getString("course"));
                System.out.print(" Grade: " + String.format("%.2f", rs.getDouble("grade")));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error viewing students: " + e.getMessage());
        }
    }

    private static void viewmydetials() {
        clear();
        int id = studentid;
        String sql = "SELECT students.id, students.name, students.age, courses.name AS course, AVG(student_grades.grade) AS average_grade "
                + "FROM students "
                + "JOIN courses ON students.course_id = courses.id "
                + "LEFT JOIN student_grades ON students.id = student_grades.student_id "
                + "WHERE students.id = ? "
                + "GROUP BY students.id, students.name, students.age, courses.name";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Course: " + rs.getString("course"));
                double averageGrade = rs.getDouble("average_grade");
                System.out.printf("Average Grade: %.2f%n", averageGrade);
            } else {
                System.out.println("Student not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error searching student: " + e.getMessage());
        }
    }

    private static void searchStudent() {
        clear();
        System.out.print("Enter student ID to search: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String sql = "SELECT students.id, students.name, students.age, courses.name AS course, AVG(student_grades.grade) AS average_grade "
                + "FROM students "
                + "JOIN courses ON students.course_id = courses.id "
                + "LEFT JOIN student_grades ON students.id = student_grades.student_id "
                + "WHERE students.id = ? "
                + "GROUP BY students.id, students.name, students.age, courses.name";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Age: " + rs.getInt("age"));
                System.out.println("Course: " + rs.getString("course"));
                double averageGrade = rs.getDouble("average_grade");
                System.out.printf("Average Grade: %.2f%n", averageGrade);
            } else {
                System.out.println("Student not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error searching student: " + e.getMessage());
        }
    }

    private static void updateStudent() {
        clear();
        System.out.print("Enter student ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new age: ");
        int age = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.println("Available courses:");
        viewCourses();
        System.out.print("Enter new course ID: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String sql = "UPDATE students SET name = ?, age = ?, course_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setInt(3, courseId);
            pstmt.setInt(4, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student updated successfully.");
            } else {
                System.out.println("Student not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    private static void deleteStudent() {
        clear();
        System.out.print("Enter student ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Student deleted successfully.");
            } else {
                System.out.println("Student not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting student: " + e.getMessage());
        }
    }

    private static void sortStudents() {
        clear();
        System.out.println("Sort students by:");
        System.out.println("1. ID");
        System.out.println("2. Name");
        System.out.println("3. Age");
        System.out.println("4. Course");
        System.out.println("5. Grade");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline
    
        String orderBy = "";
        String orderin = "";
        switch (choice) {
            case 1:
                orderBy = "s.id";
                orderin = " ASC";
                break;
            case 2:
                orderBy = "s.name";
                orderin = " ASC";
                break;
            case 3:
                orderBy = "s.age";
                orderin = " DESC";
                break;
            case 4:
                orderBy = "c.name";
                orderin = " ASC";
                break;
            case 5:
                orderBy = "grade";
                orderin = " DESC";
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
                return;
        }
    
        String sql = "SELECT s.id, s.name, s.age, c.name AS course_name, "
                   + "(SELECT AVG(grade) FROM student_grades sg JOIN exams e ON sg.exam_id = e.id WHERE sg.student_id = s.id) AS grade "
                   + "FROM students s "
                   + "JOIN courses c ON s.course_id = c.id "
                   + "ORDER BY " + orderBy + orderin;
    
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String courseName = rs.getString("course_name");
                double grade = rs.getDouble("grade");
                System.out.printf("ID: %d, Name: %s, Age: %d, Course: %s, Grade: %.2f%n",
                                  id, name, age, courseName, grade);
            }
        } catch (SQLException e) {
            System.out.println("Error sorting students: " + e.getMessage());
        }
    }
    

    private static void addStudentGrade() {
        clear();
        System.out.print("Enter student ID: ");
        int studentId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter exam ID: ");
        int examId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter grade (0-100): ");
        double grade = scanner.nextDouble();
        scanner.nextLine();  // Consume newline

        if (grade < 0 || grade > 100) {
            System.out.println("Grade must be between 0 and 100.");
            return;
        }

        String sql = "INSERT INTO student_grades(student_id, exam_id, grade) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, examId);
            pstmt.setDouble(3, grade);
            pstmt.executeUpdate();
            System.out.println("Grade added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding grade: " + e.getMessage());
        }
    }

    private static void addAttendance() {
        clear();
        System.out.println("Adding attendance is not yet implemented.");
    }

    private static void manageCourses() {
        clear();
        System.out.println("1. Add Course");
        System.out.println("2. Update Course");
        System.out.println("3. View Courses");
        System.out.println("4. Exit");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        switch (choice) {
            case 1:
                addCourse();
                break;
            case 2:
                updateCourse();
                break;
            case 3:
                viewCourses();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void manageTeachers() throws SQLException {
        clear();
        System.out.println("1. Add Teacher");
        System.out.println("2. Update Teacher");
        System.out.println("3. Delete Teacher");
        System.out.println("4. View Teachers");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        switch (choice) {
            case 1:
                addTeacher();
                break;
            case 2:
                updateTeacher();
                break;
            case 3:
                deleteTeacher();
                break;
            case 4:
                viewTeachers();
                break;
            case 5:
                return;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void addCourse() {
        clear();
        System.out.print("Enter course ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter course name: ");
        String name = scanner.nextLine();

        String sql = "INSERT INTO courses(id, name) VALUES(?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
            System.out.println("Course added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }
    }

    private static void updateCourse() {
        clear();
        System.out.print("Enter course ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter new course name: ");
        String name = scanner.nextLine();

        String sql = "UPDATE courses SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Course updated successfully.");
            } else {
                System.out.println("Course not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating course: " + e.getMessage());
        }
    }

    private static void viewCourses() {
        clear();
        String sql = "SELECT * FROM courses";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error viewing courses: " + e.getMessage());
        }
    }

    private static void addTeacher() throws SQLException {
        clear();
        System.out.print("Enter teacher ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (isIdExist(id, "teachers")) {
            System.out.println("ID already exists. Please enter a unique ID.");
            return;
        }

        System.out.print("Enter teacher name: ");
        String name = scanner.nextLine();

        System.out.println("Available courses:");
        viewCourses();
        System.out.print("Enter course ID: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (!isIdExist(courseId, "courses")) {
            System.out.println("Course ID does not exist.");
            return;
        }

        String sql = "INSERT INTO teachers(id, name, course_id) VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, courseId);
            pstmt.executeUpdate();
            System.out.println("Teacher added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding teacher: " + e.getMessage());
        }
    }

    private static void updateTeacher() {
        clear();
        System.out.print("Enter teacher ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter new name: ");
        String name = scanner.nextLine();

        System.out.println("Available courses:");
        viewCourses();
        System.out.print("Enter new course ID: ");
        int courseId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String sql = "UPDATE teachers SET name = ?, course_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, courseId);
            pstmt.setInt(3, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Teacher updated successfully.");
            } else {
                System.out.println("Teacher not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating teacher: " + e.getMessage());
        }
    }

    private static void deleteTeacher() {
        clear();
        System.out.print("Enter teacher ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String sql = "DELETE FROM teachers WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Teacher deleted successfully.");
            } else {
                System.out.println("Teacher not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting teacher: " + e.getMessage());
        }
    }

    private static void viewTeachers() {
        clear();
        String sql = "SELECT teachers.id, teachers.name, courses.name AS course FROM teachers JOIN courses ON teachers.course_id = courses.id";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Course: " + rs.getString("course"));
                System.out.println();
            }
        } catch (SQLException e) {
            System.out.println("Error viewing teachers: " + e.getMessage());
        }
    }

    private static void exportDataToCSV() {
        clear();
        String sql = "SELECT * FROM students";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql); FileWriter csv = new FileWriter("students.csv")) {

            csv.append("ID,Name,Age,Course\n");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                int courseId = rs.getInt("course_id");

                String courseName = getCourseName(courseId);

                csv.append(String.format("%d,%s,%d,%s\n", id, name, age, courseName));
            }

            System.out.println("Data exported to students.csv successfully.");
        } catch (IOException | SQLException e) {
            System.out.println("Error exporting data: " + e.getMessage());
        }
    }

    private static String getCourseName(int courseId) throws SQLException {
        String sql = "SELECT name FROM courses WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getString("name") : "Unknown";
        }
    }

    private static boolean isIdExist(int id, String tableName) throws SQLException {
        String sql = "SELECT 1 FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    private static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void exitProgram() {
        try {
            disconnect();
        } catch (SQLException e) {
            System.out.println("Error disconnecting: " + e.getMessage());
        }
        System.out.println("Exiting the program.");
    }
}
