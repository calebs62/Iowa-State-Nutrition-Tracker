package coms309.course;

/**
 * Provides the Definition/Structure for Course
 *
 * @author Connor Shepherd
 */

public class Course {
    private String courseName;
    private String courseNumber;
    private Integer numOfStudents;

    public Course() {

    }

    public Course(String courseName, String courseNumber, Integer numOfStudents) {
        this.courseName = courseName;
        this.courseNumber = courseNumber;
        this.numOfStudents = numOfStudents;
    }

    public String getCourseName() {
        return this.courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseNumber() {
        return this.courseNumber;
    }
    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public Integer getNumOfStudents() {
        return this.numOfStudents;
    }
    public void setNumOfStudents(Integer numOfStudents) {
        this.numOfStudents = numOfStudents;
    }

    @Override
    public String toString(){
        return courseName + " " + courseNumber + " " + numOfStudents;
    }

}
