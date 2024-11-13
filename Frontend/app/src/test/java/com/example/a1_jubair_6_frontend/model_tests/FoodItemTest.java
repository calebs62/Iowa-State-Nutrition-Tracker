package com.example.a1_jubair_6_frontend.model_tests;

import static org.junit.Assert.*;

import com.example.a1_jubair_6_frontend.models.FoodItem;

import org.junit.Before;
import org.junit.Test;

public class FoodItemTest {

    private FoodItem foodItem;
    private static final String TEST_NAME = "Apple";
    private static final int TEST_CALORIES = 95;
    private static final int TEST_FAT = 0;
    private static final int TEST_SODIUM = 2;
    private static final int TEST_CARBS = 25;
    private static final int TEST_PROTEIN = 1;
    private static final String TEST_SERVING = "1 medium";
    private static final String TEST_DESCRIPTION = "Fresh red apple";
    private static final int TEST_ID = 1;

    @Before
    public void setUp() {
        foodItem = new FoodItem(
                TEST_NAME,
                TEST_CALORIES,
                TEST_FAT,
                TEST_SODIUM,
                TEST_CARBS,
                TEST_PROTEIN,
                TEST_SERVING,
                TEST_DESCRIPTION
        );
        foodItem.setId(TEST_ID);
    }

    @Test
    public void testDefaultConstructor() {
        FoodItem emptyFoodItem = new FoodItem();
        assertNotNull("Default constructor should create non-null object", emptyFoodItem);
    }

    @Test
    public void testParameterizedConstructor() {
        assertNotNull("Parameterized constructor should create non-null object", foodItem);
        assertEquals("Name should match constructor parameter", TEST_NAME, foodItem.getName());
        assertEquals("Calories should match constructor parameter", TEST_CALORIES, foodItem.getCalories());
        assertEquals("Fat should match constructor parameter", TEST_FAT, foodItem.getTotalFat());
        assertEquals("Sodium should match constructor parameter", TEST_SODIUM, foodItem.getSodium());
        assertEquals("Carbohydrates should match constructor parameter", TEST_CARBS, foodItem.getCarbohydrate());
        assertEquals("Protein should match constructor parameter", TEST_PROTEIN, foodItem.getProtein());
        assertEquals("Serving size should match constructor parameter", TEST_SERVING, foodItem.getServingsize());
        assertEquals("Description should match constructor parameter", TEST_DESCRIPTION, foodItem.getDescription());
    }

    @Test
    public void testIdSetterAndGetter() {
        assertEquals("ID should match the set value", TEST_ID, foodItem.getId());

        // Test changing ID
        int newId = 2;
        foodItem.setId(newId);
        assertEquals("ID should be updated to new value", newId, foodItem.getId());
    }

    @Test
    public void testQuantitySetterAndGetter() {
        assertEquals("Default quantity should be 0", 0, foodItem.getQuantity());

        // Test setting quantity
        int testQuantity = 5;
        foodItem.setQuantity(testQuantity);
        assertEquals("Quantity should match the set value", testQuantity, foodItem.getQuantity());

        // Test changing quantity
        int newQuantity = 3;
        foodItem.setQuantity(newQuantity);
        assertEquals("Quantity should be updated to new value", newQuantity, foodItem.getQuantity());
    }

    @Test
    public void testChangeName() {
        String newName = "Red Apple";
        foodItem.changeName(newName);
        assertEquals("Name should be updated to new value", newName, foodItem.getName());
    }

    @Test
    public void testToString() {
        String expectedString = "Id: " + TEST_ID +
                "/nName: " + TEST_NAME +
                "/nCalories: " + TEST_CALORIES +
                "/nTotal Fat: " + TEST_FAT +
                "/nSodium: " + TEST_SODIUM +
                "/nTotal Carbohydrate: " + TEST_CARBS +
                "/nProtein: " + TEST_PROTEIN +
                "/nServing Size: " + TEST_SERVING +
                "/nDescription: " + TEST_DESCRIPTION;

        assertEquals("toString should return correctly formatted string", expectedString, foodItem.toString());
    }

    @Test
    public void testNegativeQuantity() {
        foodItem.setQuantity(-1);
        assertTrue("Quantity can be negative but might want to validate this in the future",
                foodItem.getQuantity() < 0);
    }

    @Test
    public void testZeroQuantity() {
        foodItem.setQuantity(0);
        assertEquals("Quantity should allow zero", 0, foodItem.getQuantity());
    }

    @Test
    public void testLargeQuantity() {
        int largeQuantity = 999999;
        foodItem.setQuantity(largeQuantity);
        assertEquals("Quantity should handle large numbers", largeQuantity, foodItem.getQuantity());
    }
}
