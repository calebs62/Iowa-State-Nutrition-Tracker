package com.example.a1_jubair_6_frontend.model_tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.example.a1_jubair_6_frontend.models.ActivityFeedItem;
import com.example.a1_jubair_6_frontend.models.Group;
import com.example.a1_jubair_6_frontend.models.User;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.HashSet;

public class ActivityFeedItemTest {

    private ActivityFeedItem activityFeedItem;
    private User testUser;
    private Group testGroup;
    private static final int TEST_ID = 1;
    private static final String TEST_MESSAGE = "Test activity message";
    private static final String TEST_ADDITIONAL_DATA = "{'key': 'value'}";
    private static final Timestamp TEST_TIMESTAMP = new Timestamp(System.currentTimeMillis());

    @Before
    public void setUp() {
        testUser = new User(1, "test@email.com", "password", "John", "Doe",
                170, 70, User.Account.USER);

        testGroup = new Group();
        testGroup.setName("Test Group");
        testGroup.setMembers(new HashSet<>());

        activityFeedItem = new ActivityFeedItem();
        activityFeedItem.setId(TEST_ID);
        activityFeedItem.setUser(testUser);
        activityFeedItem.setType(ActivityFeedItem.ActivityType.FOOD_EATEN);
        activityFeedItem.setMessage(TEST_MESSAGE);
        activityFeedItem.setTimestamp(TEST_TIMESTAMP);
        activityFeedItem.setAdditionalData(TEST_ADDITIONAL_DATA);
        activityFeedItem.setGroup(testGroup);
    }

    @Test
    public void testDefaultConstructor() {
        ActivityFeedItem emptyItem = new ActivityFeedItem();
        assertNotNull("Default constructor should create non-null object", emptyItem);
    }

    @Test
    public void testIdGetterAndSetter() {
        assertEquals("ID should match the set value", TEST_ID, activityFeedItem.getId());

        int newId = 2;
        activityFeedItem.setId(newId);
        assertEquals("ID should be updated to new value", newId, activityFeedItem.getId());
    }

    @Test
    public void testUserGetterAndSetter() {
        assertEquals("User should match the set value", testUser, activityFeedItem.getUser());

        User newUser = new User(2, "new@email.com", "password", "Jane", "Smith",
                165, 65, User.Account.USER);
        activityFeedItem.setUser(newUser);
        assertEquals("User should be updated to new value", newUser, activityFeedItem.getUser());
    }

    @Test
    public void testTypeGetterAndSetter() {
        assertEquals("Type should match the set value",
                ActivityFeedItem.ActivityType.FOOD_EATEN,
                activityFeedItem.getType());

        activityFeedItem.setType(ActivityFeedItem.ActivityType.ACHIEVEMENT);
        assertEquals("Type should be updated to new value",
                ActivityFeedItem.ActivityType.ACHIEVEMENT,
                activityFeedItem.getType());
    }

    @Test
    public void testMessageGetterAndSetter() {
        assertEquals("Message should match the set value",
                TEST_MESSAGE,
                activityFeedItem.getMessage());

        String newMessage = "New test message";
        activityFeedItem.setMessage(newMessage);
        assertEquals("Message should be updated to new value",
                newMessage,
                activityFeedItem.getMessage());
    }

    @Test
    public void testTimestampGetterAndSetter() {
        assertEquals("Timestamp should match the set value",
                TEST_TIMESTAMP,
                activityFeedItem.getTimestamp());

        Timestamp newTimestamp = new Timestamp(System.currentTimeMillis() + 1000);
        activityFeedItem.setTimestamp(newTimestamp);
        assertEquals("Timestamp should be updated to new value",
                newTimestamp,
                activityFeedItem.getTimestamp());
    }

    @Test
    public void testAdditionalDataGetterAndSetter() {
        assertEquals("Additional data should match the set value",
                TEST_ADDITIONAL_DATA,
                activityFeedItem.getAdditionalData());

        String newData = "{'newKey': 'newValue'}";
        activityFeedItem.setAdditionalData(newData);
        assertEquals("Additional data should be updated to new value",
                newData,
                activityFeedItem.getAdditionalData());
    }

    @Test
    public void testGroupGetterAndSetter() {
        assertEquals("Group should match the set value", testGroup, activityFeedItem.getGroup());

        Group newGroup = new Group();
        newGroup.setName("New Test Group");
        newGroup.setMembers(new HashSet<>());

        activityFeedItem.setGroup(newGroup);
        assertEquals("Group should be updated to new value", newGroup, activityFeedItem.getGroup());
    }

    @Test
    public void testNullValues() {
        ActivityFeedItem nullItem = new ActivityFeedItem();

        assertNull("User should be null by default", nullItem.getUser());
        assertNull("Type should be null by default", nullItem.getType());
        assertNull("Message should be null by default", nullItem.getMessage());
        assertNull("Timestamp should be null by default", nullItem.getTimestamp());
        assertNull("Additional data should be null by default", nullItem.getAdditionalData());
        assertNull("Group should be null by default", nullItem.getGroup());
    }

    @Test
    public void testAllActivityTypes() {
        // Test all enum values
        for (ActivityFeedItem.ActivityType type : ActivityFeedItem.ActivityType.values()) {
            activityFeedItem.setType(type);
            assertEquals("Activity type should be set correctly",
                    type,
                    activityFeedItem.getType());
        }
    }
}
