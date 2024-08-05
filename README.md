# School Management System

The School Management System is a Java-based console application designed to manage various aspects of a school's operations, including students, courses, teachers, grades, and attendance. It utilizes SQLite for data storage and retrieval, offering a user-friendly interface for efficient management of school data.

## Features

*   **Manage Students**: Add, view, update, and sort students.
*   **Manage Courses**: Add, view, and update courses.
*   **Manage Teachers**: Add, view, update, and delete teachers.
*   **Record Grades**: Assign and manage student grades for exams.
*   **Attendance Tracking**: Record and view attendance for students.
*   **Data Export**: Export student data to CSV format.

## Prerequisites

*   Java 8 or higher
*   SQLite JDBC driver (included in the project)

## Installation

1.  **Clone the Repository:**

        git clone https://github.com/PRIYADARSHAN-MAHENDRAN/SCHOOL-MANAGEMENT-SYSTEM.git

2.  **Navigate to the Project Directory:**

        cd school-management-system

3.  **Compile the Source Code:**

        javac -cp .;sqlite-jdbc-3.36.0.3.jar Student.java StudentManagementSystem.java

4.  **Run the Application:**

        java -cp .;sqlite-jdbc-3.36.0.3.jar StudentManagementSystem

## Usage

1.  **Run the application** using the steps in the Installation section.
2.  **Interact with the console** to manage students, courses, teachers, grades, and attendance.
3.  **Export data** to CSV format using the provided option in the menu.

## Contributing

Contributions are welcome! Please feel free to submit a pull request or report any issues.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
