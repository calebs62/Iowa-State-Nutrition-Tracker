package coms309.people;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class NutritionController {

    // Note that there is only ONE instance of PeopleController in 
    // Springboot system.
    HashMap<Integer, Nutrition> db = new  HashMap<Integer, Nutrition>();
    List<Nutrition> plate = new ArrayList<Nutrition>();

    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL

    // LIST -> outputs database of nutrition entries
    @GetMapping("/db")
    public  HashMap<Integer, Nutrition> getAllPersons() {
        return db;
    }

    // POST -> adds item to database
    @PostMapping("/db")
    public  String createEntry(@RequestBody Nutrition entry) {
        db.put(entry.getID(), entry);
        return entry.toString();
    }

    // GET -> based on id number
    @GetMapping("/db/{idnumber}")
    public Nutrition getEntry(@PathVariable Integer idnumber) {
        Nutrition n = db.get(idnumber);
        return n;
    }

    // PUT -> takes parameters
    @PutMapping("/db/name/{idnumber}")
    public Nutrition updateName(@PathVariable Integer idnumber, @RequestBody String newName) {
        Nutrition entry = db.get(idnumber);
        entry.setName(newName);
        return entry;
    }

    @PutMapping("/db/value/{idnumber}")
    public Nutrition updateName(@PathVariable Integer idnumber, @RequestBody Integer nutVal) {
        Nutrition entry = db.get(idnumber);
        entry.setNutValue(nutVal);
        return entry;
    }

    // DELETE -> deletes based off of id
    
    @DeleteMapping("/db/{id}")
    public HashMap<Integer, Nutrition> delete(@PathVariable Integer id) {
        db.remove(id);
        return db;
    }

    //Plate functions
    @GetMapping("/plate")
    public List<Nutrition> getPlate() {
        return plate;
    }

    @PutMapping("/plate")
    public List<Nutrition> addToPlate(@RequestBody Integer id) {
        Nutrition temp = db.get(id);
        plate.add(temp);
        return plate;
    }
    @DeleteMapping("/plate/{id}")
    public List<Nutrition> removeFromPlate(@PathVariable Integer id) {
        plate.remove(db.get(id));
        return plate;
    }
    @GetMapping("/plate/calcs")
    public int calcs(@RequestBody String x) {
        return Nutrition.total(plate);
    }
}

