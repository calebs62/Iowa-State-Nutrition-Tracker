package coms309.course;

import coms309.people.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the Definition/Structure for Course
 *
 * @author Connor Shepherd
 */
public class Course {
    private String courseName;
    private String courseNumber;
    private Integer numOfStudents;
    private ArrayList<Person> students;

    public Course() {

    }

    public Course(String courseName, String courseNumber, Integer numOfStudents) {
        this.courseName = courseName;
        this.courseNumber = courseNumber;
        this.numOfStudents = numOfStudents;
        this.students = new ArrayList<Person>();
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

    public ArrayList<Person> getStudents(){
        return this.students;
    }
    public void addStudent(Person s){
        students.add(s);
    }

    @Override
    public String toString(){
        return courseName + " " + courseNumber + " " + numOfStudents + students;
    }

}
