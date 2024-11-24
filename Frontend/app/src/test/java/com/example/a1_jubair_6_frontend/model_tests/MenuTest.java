package com.example.a1_jubair_6_frontend.model_tests;

import static org.junit.Assert.*;

import com.example.a1_jubair_6_frontend.models.FoodItem;
import com.example.a1_jubair_6_frontend.models.Menu;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

public class MenuTest {

    private Menu menu;
    private static final String TEST_LOCATION = "Test Cafeteria";
    private static final String TEST_MEAL = "Lunch";
    private static final Timestamp TEST_DATE = new Timestamp(System.currentTimeMillis());
    private static final int TEST_ID = 1;

    @Before
    public void setUp() {
        menu = new Menu(TEST_LOCATION, TEST_MEAL, TEST_DATE);
        menu.setId(TEST_ID);
        menu.setFoodItems(new HashSet<>());
    }

    @Test
    public void testDefaultConstructor() {
        Menu newMenu = new Menu();
        assertNotNull("Default constructor should create non-null object", newMenu);
        assertNull("Location should be null", newMenu.getLocation());
        assertNull("Meal should be null", newMenu.getMeal());
        assertNull("Date should be null", newMenu.getDate());
        assertNotNull("FoodItems should not be null", newMenu.getFoodItems());
        assertTrue("FoodItems should be empty", newMenu.getFoodItems().isEmpty());
        assertEquals("Default id should be 0", 0, newMenu.getId());
    }

    @Test
    public void testParameterizedConstructor() {
        assertNotNull("Parameterized constructor should create non-null object", menu);
        assertEquals("Location should match constructor parameter",
                TEST_LOCATION,
                menu.getLocation());
        assertEquals("Meal should match constructor parameter",
                TEST_MEAL,
                menu.getMeal());
        assertEquals("Date should match constructor parameter",
                TEST_DATE,
                menu.getDate());
    }

    @Test
    public void testIdGetterAndSetter() {
        // Test initial value
        assertEquals("ID should match the set value", TEST_ID, menu.getId());

        // Test setting new value
        int newId = 2;
        menu.setId(newId);
        assertEquals("ID should be updated to new value", newId, menu.getId());

        // Test setting zero
        menu.setId(0);
        assertEquals("ID should be able to be set to 0", 0, menu.getId());

        // Test setting negative value
        menu.setId(-1);
        assertEquals("ID should be able to be set to negative value", -1, menu.getId());
    }

    @Test
    public void testLocationGetterAndSetter() {
        // Test initial value
        assertEquals("Location should match constructor value",
                TEST_LOCATION,
                menu.getLocation());

        // Test setting new value
        String newLocation = "New Cafeteria";
        menu.setLocation(newLocation);
        assertEquals("Location should be updated to new value",
                newLocation,
                menu.getLocation());

        // Test setting null
        menu.setLocation(null);
        assertNull("Location should be able to be set to null",
                menu.getLocation());

        // Test setting empty string
        menu.setLocation("");
        assertEquals("Location should be able to be set to empty string",
                "",
                menu.getLocation());
    }

    @Test
    public void testMealGetterAndSetter() {
        // Test initial value
        assertEquals("Meal should match constructor value", TEST_MEAL, menu.getMeal());

        // Test setting new value
        String newMeal = "Breakfast";
        menu.setMeal(newMeal);
        assertEquals("Meal should be updated to new value", newMeal, menu.getMeal());

        // Test setting null
        menu.setMeal(null);
        assertNull("Meal should be able to be set to null", menu.getMeal());

        // Test setting Dinner
        menu.setMeal("Dinner");
        assertEquals("Meal should handle 'Dinner' value", "Dinner", menu.getMeal());
    }

    @Test
    public void testDateGetterAndSetter() {
        // Test initial value
        assertEquals("Date should match constructor value", TEST_DATE, menu.getDate());

        // Test setting new value
        Timestamp newDate = new Timestamp(System.currentTimeMillis() + 86400000); // +1 day
        menu.setDate(newDate);
        assertEquals("Date should be updated to new value", newDate, menu.getDate());

        // Test setting null
        menu.setDate((Timestamp)null);
        assertNull("Date should be able to be set to null", menu.getDate());
    }

    @Test
    public void testDateSetterWithString() {
        // Test with valid date string
        String dateStr = "2024-01-01";
        menu.setDate(dateStr);
        assertNotNull("Date should not be null when set with valid string", menu.getDate());

        // Test with null string - should set to current time
        menu.setDate((String)null);
        assertNotNull("Date should not be null when set with null string", menu.getDate());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals("Date should be set to the correct value",
                "2024-01-01",
                sdf.format(menu.getDate().getTime()));
    }

    @Test
    public void testFoodItemsGetterAndSetter() {
        // Test initial empty set
        assertNotNull("FoodItems should not be null", menu.getFoodItems());
        assertTrue("FoodItems should be empty", menu.getFoodItems().isEmpty());

        // Test setting new set
        Set<FoodItem> newFoodItems = new HashSet<>();
        menu.setFoodItems(newFoodItems);
        assertSame("FoodItems should be the same set that was set",
                newFoodItems,
                menu.getFoodItems());

        // Test setting null
        menu.setFoodItems(null);
        assertNotNull("FoodItems should never be null",
                menu.getFoodItems());
        assertTrue("FoodItems should be empty when set to null",
                menu.getFoodItems().isEmpty());
    }

    @Test
    public void testAddFoodItem() {
        // Initialize food items set
        Set<FoodItem> foodItems = new HashSet<>();
        menu.setFoodItems(foodItems);

        // Create and add food item
        FoodItem item = new FoodItem("Test Food", 100, 5, 50, 20, 5, "1 serving", "Test description");
        menu.addFoodItem(item);

        assertEquals("FoodItems should have size 1", 1, menu.getFoodItems().size());
        assertTrue("FoodItems should contain added item", menu.getFoodItems().contains(item));
    }

    @Test
    public void testRemoveFoodItem() {
        // Initialize food items set
        Set<FoodItem> foodItems = new HashSet<>();
        menu.setFoodItems(foodItems);

        // Add and then remove food item
        FoodItem item = new FoodItem("Test Food", 100, 5, 50, 20, 5, "1 serving", "Test description");
        menu.addFoodItem(item);
        menu.removeFoodItem(item);

        assertEquals("FoodItems should be empty after removal",
                0,
                menu.getFoodItems().size());
        assertFalse("FoodItems should not contain removed item",
                menu.getFoodItems().contains(item));
    }

    @Test
    public void testSetAll() {
        // Create new menu with different values
        String newLocation = "New Location";
        String newMeal = "Dinner";
        Timestamp newDate = new Timestamp(System.currentTimeMillis() + 86400000);
        Menu updatedMenu = new Menu(newLocation, newMeal, newDate);

        // Update original menu
        menu.setAll(updatedMenu);

        // Verify all fields are updated
        assertEquals("Location should be updated", newLocation, menu.getLocation());
        assertEquals("Meal should be updated", newMeal, menu.getMeal());
        assertEquals("Date should be updated", newDate, menu.getDate());
    }

    @Test(expected = NullPointerException.class)
    public void testAddFoodItemWithNullSet() {
        menu.setFoodItems(null);
        FoodItem item = new FoodItem("Test Food", 100, 5, 50, 20, 5, "1 serving", "Test description");
        menu.addFoodItem(item);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveFoodItemWithNullSet() {
        menu.setFoodItems(null);
        FoodItem item = new FoodItem("Test Food", 100, 5, 50, 20, 5, "1 serving", "Test description");
        menu.removeFoodItem(item);
    }

    @Test
    public void testToString() {
        String expectedString = String.format("%s - %s (%s)",
                TEST_LOCATION,
                TEST_MEAL,
                new SimpleDateFormat("MM/dd/yyyy").format(TEST_DATE));

        assertEquals("toString should return correctly formatted string",
                expectedString,
                menu.toString());
    }
}
