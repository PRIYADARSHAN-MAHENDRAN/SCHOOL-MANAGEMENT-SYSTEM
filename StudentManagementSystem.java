import java.sql.*;
import java.util.Scanner;

public class StudentManagementSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static Connection connection;

    public static void main(String[] args) {
        try {
            connect();
            createTable();
            while (true) {
                clear();
                System.out.println("\nStudent Management System");
                System.out.println("1. Add Student");
                System.out.println("2. View Students");
                System.out.println("3. Search Student");
                System.out.println("4. Update Student");
                System.out.println("5. Delete Student");
                System.out.println("6. Sort Students");
                System.out.println("7. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume newline

                switch (choice) {
                    case 1: addStudent(); break;
                    case 2: viewStudents(); break;
                    case 3: searchStudent(); break;
                    case 4: updateStudent(); break;
                    case 5: deleteStudent(); break;
                    case 6: sortStudents(); break;
                    case 7: disconnect(); return;
                    default: System.out.println("Invalid choice. Please try again.");
                }

                System.out.println("Press Enter to continue...");
                scanner.nextLine();
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

    private static void createTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS students (" +
                     "id INTEGER PRIMARY KEY," +
                     "name TEXT NOT NULL," +
                     "age INTEGER NOT NULL," +
                     "course TEXT NOT NULL)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void addStudent() {
        clear();
        System.out.print("Enter ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (isIdExist(id)) {
            System.out.println("ID already exists. Please enter a unique ID.");
            return;
        }

        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter age: ");
        int age = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.print("Enter course: ");
        String course = scanner.nextLine();

        String sql = "INSERT INTO students(id, name, age, course) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, age);
            pstmt.setString(4, course);
            pstmt.executeUpdate();
            System.out.println("Student added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }

    private static boolean isIdExist(int id) {
        String sql = "SELECT id FROM students WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Error checking ID: " + e.getMessage());
            return false;
        }
    }

    

    private static void searchStudent() {
        clear();
        System.out.print("Enter student ID or name to search: ");
        String input = scanner.nextLine();
        boolean found = false;

        String sql = "SELECT * FROM students WHERE id = ? OR name LIKE ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            try {
                int id = Integer.parseInt(input);
                pstmt.setInt(1, id);
            } catch (NumberFormatException e) {
                pstmt.setInt(1, -1); // Set to a value that won't match any ID
            }
            pstmt.setString(2, "%" + input + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String course = rs.getString("course");
                System.out.println(new Student(id, name, age, course));
                found = true;
            }
        } catch (SQLException e) {
            System.out.println("Error searching student: " + e.getMessage());
        }

        if (!found) {
            System.out.println("Student not found.");
        }
    }

    private static void updateStudent() {
        clear();
        System.out.print("Enter student ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (!isIdExist(id)) {
            System.out.println("Student not found.");
            return;
        }

        System.out.print("Enter new name: ");
        String name = scanner.nextLine();
        System.out.print("Enter new age: ");
        int age = scanner.nextInt();
        scanner.nextLine();  // Consume newline
        System.out.print("Enter new course: ");
        String course = scanner.nextLine();

        String sql = "UPDATE students SET name = ?, age = ?, course = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, age);
            pstmt.setString(3, course);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
            System.out.println("Student updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating student: " + e.getMessage());
        }
    }

    private static void deleteStudent() {
        clear();
        System.out.print("Enter student ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (!isIdExist(id)) {
            System.out.println("Student not found.");
            return;
        }

        String sql = "DELETE FROM students WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Student deleted successfully.");
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
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String sql;
        switch (choice) {
            case 1: sql = "SELECT * FROM students ORDER BY id"; break;
            case 2: sql = "SELECT * FROM students ORDER BY name"; break;
            case 3: sql = "SELECT * FROM students ORDER BY age"; break;
            case 4: sql = "SELECT * FROM students ORDER BY course"; break;
            default: System.out.println("Invalid choice. Please try again."); return;
        }

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String course = rs.getString("course");
                System.out.println(new Student(id, name, age, course));
            }
        } catch (SQLException e) {
            System.out.println("Error sorting students: " + e.getMessage());
        }
    }

    private static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
    // Method to add a subject
private static void addSubject(String subjectName) throws SQLException {
    String sql = "INSERT INTO subjects(name) VALUES(?)";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, subjectName);
        pstmt.executeUpdate();
    }
}

// Method to add an exam
private static void addExam(int period, int examNumber, int subjectId) throws SQLException {
    String sql = "INSERT INTO exams(period, exam_number, subject_id) VALUES(?, ?, ?)";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, period);
        pstmt.setInt(2, examNumber);
        pstmt.setInt(3, subjectId);
        pstmt.executeUpdate();
    }
}
private static void addStudentGrade(int studentId, int examId, double grade) throws SQLException {
    String sql = "INSERT INTO student_grades(student_id, exam_id, grade) VALUES(?, ?, ?)";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, studentId);
        pstmt.setInt(2, examId);
        pstmt.setDouble(3, grade);
        pstmt.executeUpdate();
    }
}
private static void viewStudents() throws SQLException {
    clear();
    String sql = "SELECT * FROM students";
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            int age = rs.getInt("age");
            String course = rs.getString("course");
            System.out.println("Student ID: " + id);
            System.out.println("Name: " + name);
            System.out.println("Age: " + age);
            System.out.println("Course: " + course);

            // Retrieve and display grades
            displayStudentGrades(id);
        }
    }
}

private static void displayStudentGrades(int studentId) throws SQLException {
    String sql = "SELECT s.name as subject, e.period, e.exam_number, g.grade " +
                 "FROM student_grades g " +
                 "JOIN exams e ON g.exam_id = e.id " +
                 "JOIN subjects s ON e.subject_id = s.id " +
                 "WHERE g.student_id = ?";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setInt(1, studentId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            String subject = rs.getString("subject");
            int period = rs.getInt("period");
            int examNumber = rs.getInt("exam_number");
            double grade = rs.getDouble("grade");
            System.out.printf("Subject: %s, Period: %d, Exam: %d, Grade: %.2f%n", 
                              subject, period, examNumber, grade);
        }
    }
}

}
