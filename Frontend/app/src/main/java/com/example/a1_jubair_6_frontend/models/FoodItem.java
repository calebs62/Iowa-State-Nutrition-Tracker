package com.example.a1_jubair_6_frontend.models;

public class FoodItem {
        private int id;
        private String name;
        private int calories;
        private int totalFat;
        private int sodium;
        private int carbohydrate;
        private int protein;
        private String servingsize;
        private String description;

        public FoodItem() {}
        public FoodItem(String name, int calories, int totalFat, int sodium, int carbohydrate,
                        int protein, String servingsize, String description) {

            this.name = name;
            this.calories = calories;
            this.totalFat = totalFat;
            this.sodium = sodium;
            this.carbohydrate = carbohydrate;
            this.protein = protein;
            this.servingsize = servingsize;
            this.description = description;
        }
        public void changeName(String newName) {
            this.name = newName;
        }
        public String getName() { return name;}
        public int getId() {return id;}
        public int getCalories() {return calories;}
        public int getTotalFat() {return totalFat;}
        public int getSodium() {return sodium;}
        public int getCarbohydrate() {return carbohydrate;}
        public int getProtein() {return protein;}
        public String getServingsize() {return servingsize;}
        public String getDescription() {return description;};
        public String toString() {
            return "Id: " + id +
                    "/nName: " + name +
                    "/nCalories: " + calories +
                    "/nTotal Fat: " + totalFat +
                    "/nSodium: " + sodium +
                    "/nTotal Carbohydrate: " + carbohydrate +
                    "/nProtein: " + protein +
                    "/nServing Size: " + servingsize +
                    "/nDescription: " + description;
        }
}
