package coms309.Places;

import java.util.ArrayList;

/**
 * Provides the structure of the Place object
 *
 * @author Connor Shepherd
 */
public class Place {
    private String name;
    private String description;
    private ArrayList<String> vistors;

    public Place() {
    }

    public Place(String name, String description, ArrayList<String> vistors) {
        this.name = name;
        this.description = description;
        this.vistors = vistors;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getVistors() {
        return this.vistors;
    }

    public void setVistors(ArrayList<String> vistors) {
        this.vistors = vistors;
    }

    @Override
    public String toString() {
        return name + " " + description + " " + vistors;
    }
}
