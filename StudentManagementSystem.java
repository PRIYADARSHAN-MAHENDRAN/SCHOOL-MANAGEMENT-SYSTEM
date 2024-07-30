import java.sql.*;
import java.util.Scanner;

public class StudentManagementSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static Connection connection;

    public static void main(String[] args) {
        try {
            connect();
            createTables();
            while (true) {
                clear();
                System.out.println("\nStudent Management System");
                System.out.println("1. Add Student");
                System.out.println("2. View Students");
                System.out.println("3. Search Student");
                System.out.println("4. Update Student");
                System.out.println("5. Delete Student");
                System.out.println("6. Sort Students");
                System.out.println("7. Add Grades");
                System.out.println("8. Exit");
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
                    case 7: addGrades(); break;
                    case 8: disconnect(); return;
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

    private static void createTables() throws SQLException {
        String createStudentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                                     "id INTEGER PRIMARY KEY," +
                                     "name TEXT NOT NULL," +
                                     "age INTEGER NOT NULL," +
                                     "course TEXT NOT NULL)";
        
        String createSubjectsTable = "CREATE TABLE IF NOT EXISTS subjects (" +
                                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                     "name TEXT NOT NULL)";
        
        String createExamsTable = "CREATE TABLE IF NOT EXISTS exams (" +
                                  "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                  "period INTEGER NOT NULL," +
                                  "exam_number INTEGER NOT NULL," +
                                  "subject_id INTEGER," +
                                  "FOREIGN KEY (subject_id) REFERENCES subjects(id))";
        
        String createGradesTable = "CREATE TABLE IF NOT EXISTS student_grades (" +
                                   "student_id INTEGER," +
                                   "exam_id INTEGER," +
                                   "grade REAL," +
                                   "FOREIGN KEY (student_id) REFERENCES students(id)," +
                                   "FOREIGN KEY (exam_id) REFERENCES exams(id)," +
                                   "PRIMARY KEY (student_id, exam_id))";
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createStudentsTable);
            stmt.execute(createSubjectsTable);
            stmt.execute(createExamsTable);
            stmt.execute(createGradesTable);
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
                double finalGrade = calculateFinalGrade(id);
                System.out.printf("ID: %d, Name: %s, Age: %d, Course: %s, Final Grade: %.2f%n",
                                  id, name, age, course, finalGrade);
            }
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
                double finalGrade = calculateFinalGrade(id);
                System.out.printf("ID: %d, Name: %s, Age: %d, Course: %s, Final Grade: %.2f%n",
                                  id, name, age, course, finalGrade);

                // Retrieve and display grades
                displayStudentGrades(id);
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
        System.out.println("5. Grade");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String orderBy = "";
        switch (choice) {
            case 1: orderBy = "id"; break;
            case 2: orderBy = "name"; break;
            case 3: orderBy = "age"; break;
            case 4: orderBy = "course"; break;
            case 5: orderBy = "grade"; break;
            default:
                System.out.println("Invalid choice. Please try again.");
                return;
        }

        String sql = "SELECT s.id, s.name, s.age, s.course, " +
                     "(SELECT AVG(grade) FROM student_grades sg JOIN exams e ON sg.exam_id = e.id WHERE sg.student_id = s.id) AS grade " +
                     "FROM students s ORDER BY " + orderBy;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String course = rs.getString("course");
                double grade = rs.getDouble("grade");
                System.out.printf("ID: %d, Name: %s, Age: %d, Course: %s, Grade: %.2f%n",
                                  id, name, age, course, grade);
            }
        } catch (SQLException e) {
            System.out.println("Error sorting students: " + e.getMessage());
        }
    }

    private static void addGrades() {
        clear();
        System.out.print("Enter student ID to add grades: ");
        int studentId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        if (!isIdExist(studentId)) {
            System.out.println("Student not found.");
            return;
        }

        for (int exam = 1; exam <= 3; exam++) {
            System.out.println("Enter grades for Exam " + exam);
            for (int subject = 1; subject <= 5; subject++) {
                System.out.print("Enter grade for Subject " + subject + " (0-100): ");
                double grade = scanner.nextDouble();
                if (grade < 0 || grade > 100) {
                    System.out.println("Invalid grade. Please enter a grade between 0 and 100.");
                    subject--;
                    continue;
                }
                try {
                    int subjectId = getSubjectId("Subject " + subject);
                    int examId = getExamId(exam, subjectId, exam);
                    addStudentGrade(studentId, examId, grade);
                } catch (SQLException e) {
                    System.out.println("Error adding grade: " + e.getMessage());
                }
            }
        }
    }

    private static int getSubjectId(String subjectName) throws SQLException {
        String sql = "SELECT id FROM subjects WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, subjectName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                String insertSql = "INSERT INTO subjects(name) VALUES(?)";
                try (PreparedStatement insertPstmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertPstmt.setString(1, subjectName);
                    insertPstmt.executeUpdate();
                    ResultSet keys = insertPstmt.getGeneratedKeys();
                    if (keys.next()) {
                        return keys.getInt(1);
                    } else {
                        throw new SQLException("Failed to insert new subject.");
                    }
                }
            }
        }
    }

    private static int getExamId(int period, int subjectId, int examNumber) throws SQLException {
        String sql = "SELECT id FROM exams WHERE period = ? AND subject_id = ? AND exam_number = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, period);
            pstmt.setInt(2, subjectId);
            pstmt.setInt(3, examNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                String insertSql = "INSERT INTO exams(period, subject_id, exam_number) VALUES(?, ?, ?)";
                try (PreparedStatement insertPstmt = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                    insertPstmt.setInt(1, period);
                    insertPstmt.setInt(2, subjectId);
                    insertPstmt.setInt(3, examNumber);
                    insertPstmt.executeUpdate();
                    ResultSet keys = insertPstmt.getGeneratedKeys();
                    if (keys.next()) {
                        return keys.getInt(1);
                    } else {
                        throw new SQLException("Failed to insert new exam.");
                    }
                }
            }
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

    private static double calculateFinalGrade(int studentId) throws SQLException {
        String sql = "SELECT AVG(grade) as avg_grade " +
                     "FROM student_grades sg " +
                     "JOIN exams e ON sg.exam_id = e.id " +
                     "WHERE sg.student_id = ? AND e.exam_number IN (1, 2, 3) " +
                     "GROUP BY e.exam_number";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            double totalGrade = 0;
            int count = 0;
            while (rs.next()) {
                totalGrade += rs.getDouble("avg_grade");
                count++;
            }
            return count == 3 ? totalGrade / 3 : 0; // Ensure there are 3 exam grades
        }
    }

    private static void displayStudentGrades(int studentId) throws SQLException {
        String sql = "SELECT e.exam_number, s.name as subject_name, sg.grade " +
                     "FROM student_grades sg " +
                     "JOIN exams e ON sg.exam_id = e.id " +
                     "JOIN subjects s ON e.subject_id = s.id " +
                     "WHERE sg.student_id = ? " +
                     "ORDER BY e.exam_number, e.subject_id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("Grades:");
            while (rs.next()) {
                int examNumber = rs.getInt("exam_number");
                String subjectName = rs.getString("subject_name");
                double grade = rs.getDouble("grade");
                System.out.printf("Exam %d, %s: %.2f%n", examNumber, subjectName, grade);
            }
        }
    }

    private static void clear() {
        // Clear the console screen (optional, based on your environment)
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
