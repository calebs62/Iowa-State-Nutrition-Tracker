package com.example.a1_jubair_6_frontend.model_tests;

import static org.junit.Assert.*;

import com.example.a1_jubair_6_frontend.models.MenuItem;

import org.junit.Before;
import org.junit.Test;

public class MenuItemTest {

    private MenuItem menuItem;
    private static final int TEST_ID = 0;
    private static final int TEST_MENU_ID = 1;
    private static final int TEST_FOOD_ITEM_ID = 2;

    @Before
    public void setUp() {
        menuItem = new MenuItem(TEST_MENU_ID, TEST_FOOD_ITEM_ID);
    }

    @Test
    public void testDefaultConstructor() {
        MenuItem emptyMenuItem = new MenuItem();
        assertNotNull("Default constructor should create non-null object", emptyMenuItem);
        assertEquals("Default id should be 0", 0, emptyMenuItem.getId());
        assertEquals("Default menu id should be 0", 0, emptyMenuItem.getMenu());
        assertEquals("Default food item id should be 0", 0, emptyMenuItem.getFoodItem());
    }

    @Test
    public void testParameterizedConstructor() {
        assertNotNull("Parameterized constructor should create non-null object", menuItem);
        assertEquals("Menu ID should match constructor parameter",
                TEST_MENU_ID,
                menuItem.getMenu());
        assertEquals("Food item ID should match constructor parameter",
                TEST_FOOD_ITEM_ID,
                menuItem.getFoodItem());
    }

    @Test
    public void testGetId() {
        assertEquals("ID should be default value", TEST_ID, menuItem.getId());
    }

    @Test
    public void testGetMenu() {
        assertEquals("Menu ID should match constructor value",
                TEST_MENU_ID,
                menuItem.getMenu());
    }

    @Test
    public void testGetFoodItem() {
        assertEquals("Food item ID should match constructor value",
                TEST_FOOD_ITEM_ID,
                menuItem.getFoodItem());
    }

    @Test
    public void testParameterizedConstructorWithZeroValues() {
        MenuItem zeroMenuItem = new MenuItem(0, 0);
        assertEquals("Menu ID should be 0", 0, zeroMenuItem.getMenu());
        assertEquals("Food item ID should be 0", 0, zeroMenuItem.getFoodItem());
    }

    @Test
    public void testParameterizedConstructorWithNegativeValues() {
        MenuItem negativeMenuItem = new MenuItem(-1, -2);
        assertEquals("Menu ID should accept negative value", -1, negativeMenuItem.getMenu());
        assertEquals("Food item ID should accept negative value", -2, negativeMenuItem.getFoodItem());
    }

    @Test
    public void testParameterizedConstructorWithLargeValues() {
        int largeValue = Integer.MAX_VALUE;
        MenuItem largeMenuItem = new MenuItem(largeValue, largeValue);
        assertEquals("Menu ID should accept maximum integer value",
                largeValue,
                largeMenuItem.getMenu());
        assertEquals("Food item ID should accept maximum integer value",
                largeValue,
                largeMenuItem.getFoodItem());
    }

    @Test
    public void testMultipleInstances() {
        MenuItem menuItem1 = new MenuItem(1, 1);
        MenuItem menuItem2 = new MenuItem(2, 2);

        // Verify each instance maintains its own values
        assertEquals("First menu item should have menu ID 1", 1, menuItem1.getMenu());
        assertEquals("First menu item should have food item ID 1", 1, menuItem1.getFoodItem());
        assertEquals("Second menu item should have menu ID 2", 2, menuItem2.getMenu());
        assertEquals("Second menu item should have food item ID 2", 2, menuItem2.getFoodItem());
    }
}
