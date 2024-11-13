package com.example.a1_jubair_6_frontend.model_tests;

import static org.junit.Assert.*;

import com.example.a1_jubair_6_frontend.models.Notification;

import org.junit.Before;
import org.junit.Test;

public class NotificationTest {

    private Notification notification;
    private static final int TEST_ID = 1;
    private static final String TEST_TYPE = "Alert";
    private static final String TEST_HEADER = "Test Header";
    private static final String TEST_BODY = "Test Body Message";

    @Before
    public void setUp() {
        notification = new Notification();
    }

    @Test
    public void testDefaultConstructor() {
        assertNotNull("Default constructor should create non-null object", notification);
        assertEquals("Default id should be 0", 0, notification.getId());
        assertNull("Default type should be null", notification.getType());
        assertNull("Default header should be null", notification.getHeader());
        assertNull("Default body should be null", notification.getBody());
    }

    @Test
    public void testIdGetterAndSetter() {
        // Test setting positive value
        notification.setId(TEST_ID);
        assertEquals("ID should match the set value",
                TEST_ID,
                notification.getId());

        // Test setting zero
        notification.setId(0);
        assertEquals("ID should be able to be set to 0",
                0,
                notification.getId());

        // Test setting negative value
        notification.setId(-1);
        assertEquals("ID should be able to be set to negative value",
                -1,
                notification.getId());

        // Test setting maximum value
        notification.setId(Integer.MAX_VALUE);
        assertEquals("ID should be able to be set to maximum integer value",
                Integer.MAX_VALUE,
                notification.getId());
    }

    @Test
    public void testTypeGetterAndSetter() {
        // Test setting normal value
        notification.setType(TEST_TYPE);
        assertEquals("Type should match the set value",
                TEST_TYPE,
                notification.getType());

        // Test setting null
        notification.setType(null);
        assertNull("Type should be able to be set to null",
                notification.getType());

        // Test setting empty string
        notification.setType("");
        assertEquals("Type should be able to be set to empty string",
                "",
                notification.getType());

        // Test setting long string
        String longType = "A".repeat(1000);
        notification.setType(longType);
        assertEquals("Type should handle long strings",
                longType,
                notification.getType());
    }

    @Test
    public void testHeaderGetterAndSetter() {
        // Test setting normal value
        notification.setHeader(TEST_HEADER);
        assertEquals("Header should match the set value",
                TEST_HEADER,
                notification.getHeader());

        // Test setting null
        notification.setHeader(null);
        assertNull("Header should be able to be set to null",
                notification.getHeader());

        // Test setting empty string
        notification.setHeader("");
        assertEquals("Header should be able to be set to empty string",
                "",
                notification.getHeader());

        // Test setting long string
        String longHeader = "A".repeat(1000);
        notification.setHeader(longHeader);
        assertEquals("Header should handle long strings",
                longHeader,
                notification.getHeader());
    }

    @Test
    public void testBodyGetterAndSetter() {
        // Test setting normal value
        notification.setBody(TEST_BODY);
        assertEquals("Body should match the set value",
                TEST_BODY,
                notification.getBody());

        // Test setting null
        notification.setBody(null);
        assertNull("Body should be able to be set to null",
                notification.getBody());

        // Test setting empty string
        notification.setBody("");
        assertEquals("Body should be able to be set to empty string",
                "",
                notification.getBody());

        // Test setting long string
        String longBody = "A".repeat(1000);
        notification.setBody(longBody);
        assertEquals("Body should handle long strings",
                longBody,
                notification.getBody());
    }

    @Test
    public void testCompleteNotificationSetup() {
        // Test setting all values
        notification.setId(TEST_ID);
        notification.setType(TEST_TYPE);
        notification.setHeader(TEST_HEADER);
        notification.setBody(TEST_BODY);

        // Verify all values are set correctly
        assertEquals("ID should be set correctly", TEST_ID, notification.getId());
        assertEquals("Type should be set correctly", TEST_TYPE, notification.getType());
        assertEquals("Header should be set correctly", TEST_HEADER, notification.getHeader());
        assertEquals("Body should be set correctly", TEST_BODY, notification.getBody());
    }

    @Test
    public void testSpecialCharacters() {
        String specialChars = "!@#$%^&*()_+{}[]|\\:;\"'<>,.?/~`";

        // Test type with special characters
        notification.setType(specialChars);
        assertEquals("Type should handle special characters",
                specialChars,
                notification.getType());

        // Test header with special characters
        notification.setHeader(specialChars);
        assertEquals("Header should handle special characters",
                specialChars,
                notification.getHeader());

        // Test body with special characters
        notification.setBody(specialChars);
        assertEquals("Body should handle special characters",
                specialChars,
                notification.getBody());
    }

    @Test
    public void testMultiLineStrings() {
        String multiLine = "Line 1\nLine 2\rLine 3\r\nLine 4";

        // Test header with multi-line string
        notification.setHeader(multiLine);
        assertEquals("Header should handle multi-line strings",
                multiLine,
                notification.getHeader());

        // Test body with multi-line string
        notification.setBody(multiLine);
        assertEquals("Body should handle multi-line strings",
                multiLine,
                notification.getBody());
    }
}
