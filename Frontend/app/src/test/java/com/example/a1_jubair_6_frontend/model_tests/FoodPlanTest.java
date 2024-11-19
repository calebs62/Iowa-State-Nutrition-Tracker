package com.example.a1_jubair_6_frontend.model_tests;

import static org.junit.Assert.*;

import com.example.a1_jubair_6_frontend.models.FoodPlan;

import org.junit.Before;
import org.junit.Test;

public class FoodPlanTest {

    private FoodPlan foodPlan;
    private static final String TEST_NAME = "Test Plan";
    private static final int TEST_CALORIES = 2000;
    private static final int TEST_FAT = 65;
    private static final int TEST_SODIUM = 2300;
    private static final int TEST_CARBS = 300;
    private static final int TEST_PROTEIN = 50;

    @Before
    public void setUp() {
        foodPlan = new FoodPlan();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull("Default constructor should create non-null object", foodPlan);
        assertEquals("Default calories should be -1", -1, foodPlan.getCalories());
        assertEquals("Default total fat should be -1", -1, foodPlan.getTotalFat());
        assertEquals("Default sodium should be -1", -1, foodPlan.getSodium());
        assertEquals("Default carbohydrate should be -1", -1, foodPlan.getCarbohydrate());
        assertEquals("Default protein should be -1", -1, foodPlan.getProtein());
        assertNull("Default name should be null", foodPlan.getName());
    }

    @Test
    public void testGetId() {
        int id = foodPlan.getId();
        assertEquals("Default ID should be 0", 0, id);
    }

    @Test
    public void testNameGetterAndSetter() {
        // Test initial null value
        assertNull("Initial name should be null", foodPlan.getName());

        // Test setting value
        foodPlan.setName(TEST_NAME);
        assertEquals("Name should match the set value",
                TEST_NAME,
                foodPlan.getName());

        // Test setting null
        foodPlan.setName(null);
        assertNull("Name should be able to be set to null",
                foodPlan.getName());

        // Test setting empty string
        foodPlan.setName("");
        assertEquals("Name should be able to be set to empty string",
                "",
                foodPlan.getName());
    }

    @Test
    public void testCaloriesGetterAndSetter() {
        // Test default value
        assertEquals("Default calories should be -1",
                -1,
                foodPlan.getCalories());

        // Test setting positive value
        foodPlan.setCalories(TEST_CALORIES);
        assertEquals("Calories should match the set value",
                TEST_CALORIES,
                foodPlan.getCalories());

        // Test setting zero
        foodPlan.setCalories(0);
        assertEquals("Calories should be able to be set to 0",
                0,
                foodPlan.getCalories());

        // Test setting negative value
        foodPlan.setCalories(-100);
        assertEquals("Calories should be able to be set to negative value",
                -100,
                foodPlan.getCalories());
    }

    @Test
    public void testTotalFatGetterAndSetter() {
        // Test default value
        assertEquals("Default total fat should be -1",
                -1,
                foodPlan.getTotalFat());

        // Test setting positive value
        foodPlan.setTotalFat(TEST_FAT);
        assertEquals("Total fat should match the set value",
                TEST_FAT,
                foodPlan.getTotalFat());

        // Test setting zero
        foodPlan.setTotalFat(0);
        assertEquals("Total fat should be able to be set to 0",
                0,
                foodPlan.getTotalFat());
    }

    @Test
    public void testSodiumGetterAndSetter() {
        // Test default value
        assertEquals("Default sodium should be -1",
                -1,
                foodPlan.getSodium());

        // Test setting positive value
        foodPlan.setSodium(TEST_SODIUM);
        assertEquals("Sodium should match the set value",
                TEST_SODIUM,
                foodPlan.getSodium());

        // Test setting zero
        foodPlan.setSodium(0);
        assertEquals("Sodium should be able to be set to 0",
                0,
                foodPlan.getSodium());
    }

    @Test
    public void testCarbohydrateGetterAndSetter() {
        // Test default value
        assertEquals("Default carbohydrate should be -1",
                -1,
                foodPlan.getCarbohydrate());

        // Test setting positive value
        foodPlan.setCarbohydrate(TEST_CARBS);
        assertEquals("Carbohydrate should match the set value",
                TEST_CARBS,
                foodPlan.getCarbohydrate());

        // Test setting zero
        foodPlan.setCarbohydrate(0);
        assertEquals("Carbohydrate should be able to be set to 0",
                0,
                foodPlan.getCarbohydrate());
    }

    @Test
    public void testProteinGetterAndSetter() {
        // Test default value
        assertEquals("Default protein should be -1",
                -1,
                foodPlan.getProtein());

        // Test setting positive value
        foodPlan.setProtein(TEST_PROTEIN);
        assertEquals("Protein should match the set value",
                TEST_PROTEIN,
                foodPlan.getProtein());

        // Test setting zero
        foodPlan.setProtein(0);
        assertEquals("Protein should be able to be set to 0",
                0,
                foodPlan.getProtein());
    }
}
