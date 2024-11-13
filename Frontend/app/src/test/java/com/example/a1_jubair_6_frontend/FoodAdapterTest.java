package com.example.a1_jubair_6_frontend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.a1_jubair_6_frontend.adapters.FoodAdapter;
import com.example.a1_jubair_6_frontend.models.FoodItem;
import com.example.a1_jubair_6_frontend.models.Menu;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class FoodAdapterTest {

    private List<FoodItem> foodItems;
    private Menu testMenu;

    @Before
    public void setUp() {
        foodItems = new ArrayList<>();

        // Setup test menu
        testMenu = new Menu("Test Location", "Lunch", new Timestamp(System.currentTimeMillis()));
        testMenu.setId(1);
        testMenu.setFoodItems(new HashSet<>());
    }

    @Test
    public void testFoodItemCreation() {
        // Given
        FoodItem item = createSampleFoodItem(1, "Apple");

        // Then
        assertEquals("Apple", item.getName());
        assertEquals(100, item.getCalories());
        assertEquals(5, item.getTotalFat());
        assertEquals(50, item.getSodium());
        assertEquals(20, item.getCarbohydrate());
        assertEquals(5, item.getProtein());
        assertEquals("1 piece", item.getServingsize());
        assertEquals("Fresh Apple", item.getDescription());
    }

    @Test
    public void testFoodItemQuantityManagement() {
        // Given
        FoodItem item = createSampleFoodItem(1, "Apple");

        // When
        item.setQuantity(5);

        // Then
        assertEquals(5, item.getQuantity());
    }

    @Test
    public void testFoodItemIdManagement() {
        // Given
        FoodItem item = createSampleFoodItem(1, "Apple");

        // When
        item.setId(100);

        // Then
        assertEquals(100, item.getId());
    }

    @Test
    public void testFoodItemList() {
        // Given
        foodItems.add(createSampleFoodItem(1, "Apple"));
        foodItems.add(createSampleFoodItem(2, "Banana"));

        // Then
        assertEquals(2, foodItems.size());
        assertEquals("Apple", foodItems.get(0).getName());
        assertEquals("Banana", foodItems.get(1).getName());
    }

    @Test
    public void testMenuManagement() {
        // Given
        FoodItem item = createSampleFoodItem(1, "Apple");
        testMenu.getFoodItems().add(item);

        // Then
        assertEquals(1, testMenu.getFoodItems().size());
        assertTrue(testMenu.getFoodItems().contains(item));
    }

    private FoodItem createSampleFoodItem(int id, String name) {
        FoodItem item = new FoodItem(
                name,           // name
                100,           // calories
                5,             // totalFat
                50,            // sodium
                20,            // carbohydrate
                5,             // protein
                "1 piece",     // servingsize
                "Fresh " + name // description
        );
        item.setId(id);
        return item;
    }
}
