package coms309.people;


import java.util.HashMap;
import java.util.List;

/**
 * Defining a simple class to store nutrition info
 *
 * @author Molly Heston
 */

public class Nutrition {
    private int id;
    private String name;
    private int nutValue;

    // Basic creation, will take info from POST
    public Nutrition (int idVar, String nameVar, int nutValueVar){
        this.id = idVar;
        this.name = nameVar;
        this.nutValue = nutValueVar;
    }

    // GET
    public int getID() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }

    public int getNutValue() {
        return this.nutValue;
    }

    // POST stuff
    public void setName(String nameVar) {
        this.name = nameVar;
        System.out.println(id + " name was changed to " + this.name);
    }

    public void setNutValue(int nutVar) {
        this.nutValue = nutVar;
        System.out.println(id + " nutritional value was changed to " + this.nutValue);
    }


    // Probably a better way to do this, but will calculate total nutrition based on a list of Nutrition classes
    public static int total(List<Nutrition> plate) {
        int tot = 0;
        for (Nutrition n: plate) {
            tot += n.getNutValue();
        }
        return tot;
    }
}
