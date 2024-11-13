package com.example.a1_jubair_6_frontend.model_tests;

import static org.junit.Assert.*;

import com.example.a1_jubair_6_frontend.models.User;

import org.junit.Before;
import org.junit.Test;

public class UserTest {

    private User user;
    private static final int TEST_ID = 1;
    private static final String TEST_USERNAME = "testuser@email.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_FNAME = "John";
    private static final String TEST_LNAME = "Doe";
    private static final int TEST_HEIGHT = 180;
    private static final int TEST_WEIGHT = 75;
    private static final User.Account TEST_ACCOUNT_TYPE = User.Account.USER;

    @Before
    public void setUp() {
        user = new User(
                TEST_ID,
                TEST_USERNAME,
                TEST_PASSWORD,
                TEST_FNAME,
                TEST_LNAME,
                TEST_HEIGHT,
                TEST_WEIGHT,
                TEST_ACCOUNT_TYPE
        );
    }

    @Test
    public void testConstructor() {
        assertNotNull("Constructor should create non-null object", user);
        assertEquals("ID should match constructor parameter", TEST_ID, user.getId());
        assertEquals("Username should match constructor parameter", TEST_USERNAME, user.getUsername());
        assertEquals("Password should match constructor parameter", TEST_PASSWORD, user.getPassword());
        assertEquals("First name should match constructor parameter", TEST_FNAME, user.getFname());
        assertEquals("Last name should match constructor parameter", TEST_LNAME, user.getLname());
        assertEquals("Height should match constructor parameter", TEST_HEIGHT, user.getHeight());
        assertEquals("Weight should match constructor parameter", TEST_WEIGHT, user.getWeight());
        assertEquals("Account type should match constructor parameter", TEST_ACCOUNT_TYPE, user.getAccounttype());
    }

    @Test
    public void testConstructorWithZeroValues() {
        User zeroUser = new User(0, TEST_USERNAME, TEST_PASSWORD, TEST_FNAME, TEST_LNAME, 0, 0, TEST_ACCOUNT_TYPE);

        assertEquals("ID should accept zero", 0, zeroUser.getId());
        assertEquals("Height should accept zero", 0, zeroUser.getHeight());
        assertEquals("Weight should accept zero", 0, zeroUser.getWeight());
    }

    @Test
    public void testConstructorWithNegativeValues() {
        User negativeUser = new User(-1, TEST_USERNAME, TEST_PASSWORD, TEST_FNAME, TEST_LNAME, -180, -75, TEST_ACCOUNT_TYPE);

        assertEquals("ID should accept negative value", -1, negativeUser.getId());
        assertEquals("Height should accept negative value", -180, negativeUser.getHeight());
        assertEquals("Weight should accept negative value", -75, negativeUser.getWeight());
    }

    @Test
    public void testConstructorWithNullStrings() {
        User nullUser = new User(TEST_ID, null, null, null, null, TEST_HEIGHT, TEST_WEIGHT, TEST_ACCOUNT_TYPE);

        assertNull("Username should accept null", nullUser.getUsername());
        assertNull("Password should accept null", nullUser.getPassword());
        assertNull("First name should accept null", nullUser.getFname());
        assertNull("Last name should accept null", nullUser.getLname());
    }

    @Test
    public void testConstructorWithEmptyStrings() {
        User emptyUser = new User(TEST_ID, "", "", "", "", TEST_HEIGHT, TEST_WEIGHT, TEST_ACCOUNT_TYPE);

        assertEquals("Username should accept empty string", "", emptyUser.getUsername());
        assertEquals("Password should accept empty string", "", emptyUser.getPassword());
        assertEquals("First name should accept empty string", "", emptyUser.getFname());
        assertEquals("Last name should accept empty string", "", emptyUser.getLname());
    }

    @Test
    public void testAllAccountTypes() {
        // Test USER account type
        User userTypeUser = new User(TEST_ID, TEST_USERNAME, TEST_PASSWORD, TEST_FNAME, TEST_LNAME,
                TEST_HEIGHT, TEST_WEIGHT, User.Account.USER);
        assertEquals("Account type should be USER", User.Account.USER, userTypeUser.getAccounttype());

        // Test CONTRIBUTOR account type
        User contributorTypeUser = new User(TEST_ID, TEST_USERNAME, TEST_PASSWORD, TEST_FNAME, TEST_LNAME,
                TEST_HEIGHT, TEST_WEIGHT, User.Account.CONTRIBUTOR);
        assertEquals("Account type should be CONTRIBUTOR", User.Account.CONTRIBUTOR, contributorTypeUser.getAccounttype());

        // Test ADMINISTRATOR account type
        User adminTypeUser = new User(TEST_ID, TEST_USERNAME, TEST_PASSWORD, TEST_FNAME, TEST_LNAME,
                TEST_HEIGHT, TEST_WEIGHT, User.Account.ADMINISTRATOR);
        assertEquals("Account type should be ADMINISTRATOR", User.Account.ADMINISTRATOR, adminTypeUser.getAccounttype());
    }

    @Test
    public void testMaxValues() {
        User maxUser = new User(
                Integer.MAX_VALUE,
                TEST_USERNAME,
                TEST_PASSWORD,
                TEST_FNAME,
                TEST_LNAME,
                Integer.MAX_VALUE,
                Integer.MAX_VALUE,
                TEST_ACCOUNT_TYPE
        );

        assertEquals("ID should accept maximum integer value", Integer.MAX_VALUE, maxUser.getId());
        assertEquals("Height should accept maximum integer value", Integer.MAX_VALUE, maxUser.getHeight());
        assertEquals("Weight should accept maximum integer value", Integer.MAX_VALUE, maxUser.getWeight());
    }

    @Test
    public void testLongStrings() {
        String longString = "a".repeat(1000);
        User longStringUser = new User(
                TEST_ID,
                longString,
                longString,
                longString,
                longString,
                TEST_HEIGHT,
                TEST_WEIGHT,
                TEST_ACCOUNT_TYPE
        );

        assertEquals("Username should handle long strings", longString, longStringUser.getUsername());
        assertEquals("Password should handle long strings", longString, longStringUser.getPassword());
        assertEquals("First name should handle long strings", longString, longStringUser.getFname());
        assertEquals("Last name should handle long strings", longString, longStringUser.getLname());
    }

    @Test
    public void testSpecialCharacters() {
        String specialChars = "!@#$%^&*()_+{}[]|\\:;\"'<>,.?/~`";
        User specialCharsUser = new User(
                TEST_ID,
                specialChars,
                specialChars,
                specialChars,
                specialChars,
                TEST_HEIGHT,
                TEST_WEIGHT,
                TEST_ACCOUNT_TYPE
        );

        assertEquals("Username should handle special characters", specialChars, specialCharsUser.getUsername());
        assertEquals("Password should handle special characters", specialChars, specialCharsUser.getPassword());
        assertEquals("First name should handle special characters", specialChars, specialCharsUser.getFname());
        assertEquals("Last name should handle special characters", specialChars, specialCharsUser.getLname());
    }
}
