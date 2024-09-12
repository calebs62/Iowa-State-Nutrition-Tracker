package coms309.course;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.HashMap;

/**
 * Controller used to showcase Create and Read from a LIST of Courses
 *
 * @author Connor Shepherd
 */
@RestController
public class CourseController {
    HashMap<String, Course> courseList = new HashMap<>();

    // List
    // Gets all courses in a list and returns in JSON format
    @GetMapping("/course")
    public HashMap<String, Course> getAllCourses() {
        return courseList;
    }

    // Create
    // Converts JSON input into a course object.
    // Returns a message.
    @PostMapping("/course")
    public String createCourse(@RequestBody Course course) {
        courseList.put(course.getCourseName(), course);
        return "New course " + course.getCourseName() + " added.";
    }

    // Update
    // Pull the course from the HashMap and modify it.
    // Returns the updated course object
    @PutMapping("/course/{courseName}")
    public Course updateCourse(@PathVariable String courseName, @RequestBody Course c) {
        courseList.replace(courseName, c);
        return courseList.get(courseName);
    }

    // Delete
    // Deletes the named course from HashMap
    // Returns HashMap list
    @DeleteMapping("/course/{courseName}")
    public HashMap<String, Course> deleteCourse(@PathVariable String courseName) {
        courseList.remove(courseName);
        return courseList;
    }

}
