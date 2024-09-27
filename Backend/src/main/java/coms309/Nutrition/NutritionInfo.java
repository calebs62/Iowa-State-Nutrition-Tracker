package coms309.Nutrition;

public class NutritionInfo {
    String name;

    // Unique id in
    int id;

    // Nutrition Info variables
    int calories;
    int totalFat;
    int cholesterol;
    int sodium;
    int totalCarbohydrate;
    int protein;

    public NutritionInfo() {}

    public void changeName(String newName) {
        this.name = newName;
    }
    public String getName() { return name;}
    public int getId() {return id;}
    public int getCalories() {return calories;}
    public int getTotalFat() {return totalFat;}
    public int getCholesterol() {return cholesterol;}
    public int getSodium() {return sodium;}
    public int getTotalCarbohydrate() {return totalCarbohydrate;}
    public int getProtein() {return protein;}
    public String toString() {
        return "Id: " + id +
                "/nName: " + name +
                "/nCalories: " + calories +
                "/nTotal Fat: " + totalFat +
                "/nCholesterol: " + cholesterol +
                "/nSodium: " + sodium +
                "/nTotal Carbohydrate: " + totalCarbohydrate +
                "/nProtein: " + protein;
    }
}
