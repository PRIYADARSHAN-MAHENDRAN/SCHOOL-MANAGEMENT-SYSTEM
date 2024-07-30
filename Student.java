import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Student implements Serializable {
    private int id;
    private String name;
    private int age;
    private String course;
    private Map<String, Map<Integer, Double>> grades; // {subject: {exam: grade}}

    public Student() {
        grades = new HashMap<>();
    }

    public Student(int id, String name, int age, String course) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
        this.grades = new HashMap<>();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCourse() { return course; }
    public Map<String, Map<Integer, Double>> getGrades() { return grades; } // Getter for grades

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setCourse(String course) { this.course = course; }
    public void setGrades(Map<String, Map<Integer, Double>> grades) { this.grades = grades; } // Setter for grades

    public void addGrade(String subject, int exam, double grade) {
        grades.computeIfAbsent(subject, k -> new HashMap<>()).put(exam, grade);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id)
          .append(", Name: ").append(name)
          .append(", Age: ").append(age)
          .append(", Course: ").append(course)
          .append(", Grades: ").append(grades);
        return sb.toString();
    }
}
