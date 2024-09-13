package coms309.people;


import java.util.ArrayList;

/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre
 */

public class Person {

    private String firstName;

    private String lastName;

    private String description;

    private ArrayList<String> connections;

    public Person() {

    }

    public Person(String firstName, String lastName, String description, ArrayList<String> connections) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
        this.connections = connections;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getConnections() {
        return this.connections;
    }

    public void setConnections(ArrayList<String> connections) {
        this.connections = connections;
    }

    @Override
    public String toString() {
        return firstName + " " 
               + lastName + " "
               + description + " "
               + connections;
    }
}
