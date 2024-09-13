package coms309.Places;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controller used for CRUDL of the Place object
 *
 * @author Connor Shepherd
 */
@RestController
public class PlaceController {
    // Place list denoted by name.
    HashMap<String, Place> placeList = new HashMap<>();

    // List
    // Returns JSON List of Place objects
    @GetMapping("/places")
    public HashMap<String, Place> getAllPlaces(){
        return placeList;
    }

    // Read
    // Returns a place object
    @GetMapping("/places/{placeName}")
    public Place getPlace(@PathVariable String placeName){
        return placeList.get(placeName);
    }

    // Create
    // Returns a message
    @PostMapping("/places")
    public String createPlace(@RequestBody Place place){
        System.out.println(place);
        placeList.put(place.getName(), place);
        return "New place " + place.getName() + " added.";
    }

    // Update
    // Returns updated place object
    @PutMapping("/places/{placeName}")
    public Place updatePlace(@PathVariable String placeName, @RequestBody Place p){
        placeList.replace(placeName, p);
        return placeList.get(placeName);
    }

    // Update
    // Returns updated place
    @PutMapping("/{person}/visits/{place}")
    public Place addVisitor(@PathVariable String person, @PathVariable String place){
        Place tmp = placeList.get(place);
        ArrayList<String> vis = tmp.getVistors();
        vis.add(person);
        tmp.setVistors(vis);
        placeList.replace(place, tmp);
        return placeList.get(place);
    }

    // Delete
    // Returns updated place list
    @DeleteMapping("/places/{placeName}")
    public HashMap<String, Place> deletePlace(@PathVariable String placeName){
        placeList.remove(placeName);
        return placeList;
    }

}
