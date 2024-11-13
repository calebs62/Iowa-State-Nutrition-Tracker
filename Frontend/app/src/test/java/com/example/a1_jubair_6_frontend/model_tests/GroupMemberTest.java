package com.example.a1_jubair_6_frontend.model_tests;


import static org.junit.Assert.*;

import com.example.a1_jubair_6_frontend.models.Group;
import com.example.a1_jubair_6_frontend.models.GroupMember;
import com.example.a1_jubair_6_frontend.models.User;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.HashSet;

public class GroupMemberTest {

    private GroupMember groupMember;
    private Group testGroup;
    private User testUser;
    private Timestamp testTimestamp;

    @Before
    public void setUp() {
        // Initialize the main test object
        groupMember = new GroupMember();

        // Create test timestamp
        testTimestamp = new Timestamp(System.currentTimeMillis());

        // Create test user
        testUser = new User(1, "test@email.com", "password", "John", "Doe",
                170, 70, User.Account.USER);

        // Create test group
        testGroup = new Group();
        testGroup.setName("Test Group");
        testGroup.setMembers(new HashSet<>());
    }

    @Test
    public void testDefaultConstructor() {
        GroupMember newMember = new GroupMember();
        assertNotNull("Default constructor should create non-null object", newMember);
        assertNull("Default group should be null", newMember.getGroup());
        assertNull("Default user should be null", newMember.getUser());
        assertNull("Default join date should be null", newMember.getJoinDate());
        assertEquals("Default id should be 0", 0, newMember.getId());
    }

    @Test
    public void testGetId() {
        int id = groupMember.getId();
        assertEquals("Default ID should be 0", 0, id);
    }

    @Test
    public void testGroupGetterAndSetter() {
        // Test initial null value
        assertNull("Initial group should be null", groupMember.getGroup());

        // Test setting value
        groupMember.setGroup(testGroup);
        assertSame("Group should match the set value",
                testGroup,
                groupMember.getGroup());

        // Test setting null
        groupMember.setGroup(null);
        assertNull("Group should be able to be set to null",
                groupMember.getGroup());

        // Test setting different group
        Group newGroup = new Group();
        newGroup.setName("New Test Group");
        groupMember.setGroup(newGroup);
        assertSame("Group should be updated to new value",
                newGroup,
                groupMember.getGroup());
    }

    @Test
    public void testUserGetterAndSetter() {
        // Test initial null value
        assertNull("Initial user should be null", groupMember.getUser());

        // Test setting value
        groupMember.setUser(testUser);
        assertSame("User should match the set value",
                testUser,
                groupMember.getUser());

        // Test setting null
        groupMember.setUser(null);
        assertNull("User should be able to be set to null",
                groupMember.getUser());

        // Test setting different user
        User newUser = new User(2, "new@email.com", "newpass", "Jane", "Smith",
                165, 65, User.Account.USER);
        groupMember.setUser(newUser);
        assertSame("User should be updated to new value",
                newUser,
                groupMember.getUser());
    }

    @Test
    public void testJoinDateGetterAndSetter() {
        // Test initial null value
        assertNull("Initial join date should be null", groupMember.getJoinDate());

        // Test setting value
        groupMember.setJoinDate(testTimestamp);
        assertEquals("Join date should match the set value",
                testTimestamp,
                groupMember.getJoinDate());

        // Test setting null
        groupMember.setJoinDate(null);
        assertNull("Join date should be able to be set to null",
                groupMember.getJoinDate());

        // Test setting different timestamp
        Timestamp newTimestamp = new Timestamp(System.currentTimeMillis() + 1000);
        groupMember.setJoinDate(newTimestamp);
        assertEquals("Join date should be updated to new value",
                newTimestamp,
                groupMember.getJoinDate());
    }

    @Test
    public void testCompleteGroupMemberSetup() {
        // Test setting all values
        groupMember.setGroup(testGroup);
        groupMember.setUser(testUser);
        groupMember.setJoinDate(testTimestamp);

        // Verify all values are set correctly
        assertSame("Group should be set correctly", testGroup, groupMember.getGroup());
        assertSame("User should be set correctly", testUser, groupMember.getUser());
        assertEquals("Join date should be set correctly", testTimestamp, groupMember.getJoinDate());
    }

    @Test
    public void testTimestampPrecision() {
        // Test timestamp with specific time
        long specificTime = 1699900800000L;
        Timestamp preciseTimestamp = new Timestamp(specificTime);

        groupMember.setJoinDate(preciseTimestamp);
        assertEquals("Timestamp should maintain precision",
                preciseTimestamp,
                groupMember.getJoinDate());
        assertEquals("Timestamp milliseconds should be preserved",
                specificTime,
                groupMember.getJoinDate().getTime());
    }

    @Test
    public void testSettersWithRepeatedValues() {
        // Test setting same values multiple times
        groupMember.setGroup(testGroup);
        groupMember.setGroup(testGroup);
        assertSame("Group should remain the same after setting same value",
                testGroup,
                groupMember.getGroup());

        groupMember.setUser(testUser);
        groupMember.setUser(testUser);
        assertSame("User should remain the same after setting same value",
                testUser,
                groupMember.getUser());

        groupMember.setJoinDate(testTimestamp);
        groupMember.setJoinDate(testTimestamp);
        assertEquals("Join date should remain the same after setting same value",
                testTimestamp,
                groupMember.getJoinDate());
    }
}
