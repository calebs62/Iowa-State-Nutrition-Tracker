package com.example.a1_jubair_6_frontend.model_tests;

import static org.junit.Assert.*;

import com.example.a1_jubair_6_frontend.models.PrivacySettings;

import org.junit.Test;

public class PrivacySettingsTest {

    @Test
    public void testDefaultConstructor() {
        // When
        PrivacySettings settings = new PrivacySettings();

        // Then
        assertNotNull("Default constructor should create non-null object", settings);
        assertEquals("Default ID should be 0", 0, settings.getId());
        assertTrue("Default food setting should be true", settings.getFood());
        assertTrue("Default goal setting should be true", settings.getGoal());
        assertTrue("Default achievement setting should be true", settings.getAchievement());
    }

    @Test
    public void testParameterizedConstructor() {
        // When
        PrivacySettings settings = new PrivacySettings(false, true, false);

        // Then
        assertNotNull("Parameterized constructor should create non-null object", settings);
        assertEquals("Default ID should be 0", 0, settings.getId());
        assertFalse("Food setting should match constructor parameter", settings.getFood());
        assertTrue("Goal setting should match constructor parameter", settings.getGoal());
        assertFalse("Achievement setting should match constructor parameter", settings.getAchievement());
    }

    @Test
    public void testIdGetterAndSetter() {
        // Given
        PrivacySettings settings = new PrivacySettings();

        // When
        settings.setId(1);

        // Then
        assertEquals("ID should match the set value", 1, settings.getId());

        // Test with negative value
        settings.setId(-1);
        assertEquals("ID should accept negative values", -1, settings.getId());

        // Test with zero
        settings.setId(0);
        assertEquals("ID should accept zero", 0, settings.getId());

        // Test with maximum value
        settings.setId(Integer.MAX_VALUE);
        assertEquals("ID should accept maximum integer value", Integer.MAX_VALUE, settings.getId());
    }

    @Test
    public void testFoodGetterAndSetter() {
        // Given
        PrivacySettings settings = new PrivacySettings();

        // Test setting to false
        settings.setFood(false);
        assertFalse("Food setting should be false after setting false", settings.getFood());

        // Test setting to true
        settings.setFood(true);
        assertTrue("Food setting should be true after setting true", settings.getFood());
    }

    @Test
    public void testGoalGetterAndSetter() {
        // Given
        PrivacySettings settings = new PrivacySettings();

        // Test setting to false
        settings.setGoal(false);
        assertFalse("Goal setting should be false after setting false", settings.getGoal());

        // Test setting to true
        settings.setGoal(true);
        assertTrue("Goal setting should be true after setting true", settings.getGoal());
    }

    @Test
    public void testAchievementGetterAndSetter() {
        // Given
        PrivacySettings settings = new PrivacySettings();

        // Test setting to false
        settings.setAchievement(false);
        assertFalse("Achievement setting should be false after setting false", settings.getAchievement());

        // Test setting to true
        settings.setAchievement(true);
        assertTrue("Achievement setting should be true after setting true", settings.getAchievement());
    }

    @Test
    public void testAllPermutations() {
        // Test all possible combinations (2^3 = 8 combinations)
        boolean[][] combinations = {
                {false, false, false},
                {false, false, true},
                {false, true, false},
                {false, true, true},
                {true, false, false},
                {true, false, true},
                {true, true, false},
                {true, true, true}
        };

        for (boolean[] combination : combinations) {
            PrivacySettings settings = new PrivacySettings(combination[0], combination[1], combination[2]);

            assertEquals("Food setting should match input", combination[0], settings.getFood());
            assertEquals("Goal setting should match input", combination[1], settings.getGoal());
            assertEquals("Achievement setting should match input", combination[2], settings.getAchievement());
        }
    }

    @Test
    public void testSettingAllToFalse() {
        // Given
        PrivacySettings settings = new PrivacySettings();

        // When
        settings.setFood(false);
        settings.setGoal(false);
        settings.setAchievement(false);

        // Then
        assertFalse("Food setting should be false", settings.getFood());
        assertFalse("Goal setting should be false", settings.getGoal());
        assertFalse("Achievement setting should be false", settings.getAchievement());
    }

    @Test
    public void testSettingAllToTrue() {
        // Given
        PrivacySettings settings = new PrivacySettings(false, false, false);

        // When
        settings.setFood(true);
        settings.setGoal(true);
        settings.setAchievement(true);

        // Then
        assertTrue("Food setting should be true", settings.getFood());
        assertTrue("Goal setting should be true", settings.getGoal());
        assertTrue("Achievement setting should be true", settings.getAchievement());
    }

    @Test
    public void testMultipleToggling() {
        // Given
        PrivacySettings settings = new PrivacySettings();

        // When - Toggle each setting multiple times
        settings.setFood(false);
        settings.setFood(true);
        settings.setFood(false);

        settings.setGoal(false);
        settings.setGoal(true);
        settings.setGoal(false);

        settings.setAchievement(false);
        settings.setAchievement(true);
        settings.setAchievement(false);

        // Then
        assertFalse("Food setting should end up false", settings.getFood());
        assertFalse("Goal setting should end up false", settings.getGoal());
        assertFalse("Achievement setting should end up false", settings.getAchievement());
    }
}
