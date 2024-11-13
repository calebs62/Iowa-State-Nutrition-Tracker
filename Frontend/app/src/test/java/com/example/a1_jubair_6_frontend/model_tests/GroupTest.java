package com.example.a1_jubair_6_frontend.model_tests;

import static org.junit.Assert.*;

import com.example.a1_jubair_6_frontend.models.FoodPlan;
import com.example.a1_jubair_6_frontend.models.Group;
import com.example.a1_jubair_6_frontend.models.GroupMember;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class GroupTest {

    private Group group;
    private static final String TEST_GROUP_NAME = "Test Group";
    private static final int TEST_ID = 1;

    @Before
    public void setUp() {
        group = new Group();
        group.setName(TEST_GROUP_NAME);
        group.setMembers(new HashSet<>());
    }

    @Test
    public void testDefaultConstructor() {
        Group newGroup = new Group();
        assertNotNull("Default constructor should create non-null object", newGroup);
        assertNull("New group should have null members", newGroup.getMembers());
        assertNull("New group should have null plan", newGroup.getPlan());
        assertNull("New group should have null name", newGroup.getGroupName());
    }

    @Test
    public void testGetId() {
        // Testing the getId method
        int id = group.getId();
        assertEquals("Default ID should be 0 or the default value", 0, id);
    }

    @Test
    public void testGroupNameGetterAndSetter() {
        assertEquals("Group name should match the set value",
                TEST_GROUP_NAME,
                group.getGroupName());

        // Test updating to new value
        String newName = "New Test Group";
        group.setName(newName);
        assertEquals("Group name should be updated to new value",
                newName,
                group.getGroupName());

        // Test setting to null
        group.setName(null);
        assertNull("Group name should be able to be set to null",
                group.getGroupName());
    }

    @Test
    public void testMembersGetterAndSetter() {
        // Test initial set from setUp
        assertNotNull("Initial members set should not be null",
                group.getMembers());
        assertTrue("Initial members set should be empty",
                group.getMembers().isEmpty());

        // Test setting new empty set
        Set<GroupMember> newMembers = new HashSet<>();
        group.setMembers(newMembers);
        assertSame("Members should be the same set that was set",
                newMembers,
                group.getMembers());

        // Test setting null
        group.setMembers(null);
        assertNull("Members should be able to be set to null",
                group.getMembers());
    }

    @Test
    public void testPlanGetterAndSetter() {
        // Test initial null plan
        assertNull("Initial plan should be null",
                group.getPlan());

        // Test setting plan
        FoodPlan plan = new FoodPlan();
        group.setPlan(plan);
        assertSame("Plan should be the same object that was set",
                plan,
                group.getPlan());

        // Test setting null
        group.setPlan(null);
        assertNull("Plan should be able to be set to null",
                group.getPlan());
    }

    @Test
    public void testAddMemberToEmptySet() {
        // Ensure we have an empty set
        Set<GroupMember> members = new HashSet<>();
        group.setMembers(members);

        // Add a member
        GroupMember newMember = new GroupMember();
        Set<GroupMember> returnedSet = group.addMember(newMember);

        // Verify return value
        assertNotNull("Returned set should not be null", returnedSet);
        assertEquals("Returned set should have size 1", 1, returnedSet.size());
        assertTrue("Returned set should contain the added member",
                returnedSet.contains(newMember));

        // Verify internal state
        assertEquals("Group members should have size 1", 1, group.getMembers().size());
        assertTrue("Group members should contain the added member",
                group.getMembers().contains(newMember));
    }

    @Test
    public void testAddMemberToExistingSet() {
        // Create a set with an existing member
        Set<GroupMember> members = new HashSet<>();
        GroupMember existingMember = new GroupMember();
        members.add(existingMember);
        group.setMembers(members);

        // Add new member
        GroupMember newMember = new GroupMember();
        Set<GroupMember> returnedSet = group.addMember(newMember);

        // Verify return value
        assertEquals("Returned set should have size 2", 2, returnedSet.size());
        assertTrue("Returned set should contain both members",
                returnedSet.containsAll(Set.of(existingMember, newMember)));

        // Verify internal state
        assertEquals("Group members should have size 2", 2, group.getMembers().size());
        assertTrue("Group members should contain both members",
                group.getMembers().containsAll(Set.of(existingMember, newMember)));
    }

    @Test(expected = NullPointerException.class)
    public void testAddMemberWhenMembersIsNull() {
        // Set members to null
        group.setMembers(null);

        // This should throw NullPointerException
        group.addMember(new GroupMember());
    }

    @Test
    public void testAddDuplicateMember() {
        // Create a set and add a member
        Set<GroupMember> members = new HashSet<>();
        group.setMembers(members);

        GroupMember member = new GroupMember();
        group.addMember(member);

        // Try to add the same member again
        Set<GroupMember> returnedSet = group.addMember(member);

        // Verify that the set still only contains one instance
        assertEquals("Set should still have size 1", 1, returnedSet.size());
        assertEquals("Group members should still have size 1",
                1,
                group.getMembers().size());
    }
}