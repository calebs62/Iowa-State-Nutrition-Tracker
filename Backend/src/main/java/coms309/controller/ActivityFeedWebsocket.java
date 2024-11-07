package coms309.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import coms309.entity.*;
import coms309.repository.*;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(value = "/activity/{uid}")
public class ActivityFeedWebsocket {
    private static ActivityFeedRepository feedRepo;
    private static UserRepository userRepo;
    private static GroupRepository groupRepo;
    private static GroupMemberRepository memberRepo;
    private static PrivacySettingRepository privacyRepo;

    @Autowired
    public void setRepositories(ActivityFeedRepository afrepo, UserRepository urepo,
                                GroupRepository grepo, GroupMemberRepository memRepo,
                                PrivacySettingRepository privRepo) {
        feedRepo = afrepo;
        userRepo = urepo;
        groupRepo = grepo;
        memberRepo = memRepo;
        privacyRepo = privRepo;
    }

    private static Map<Session, Integer> sessionUserMap = new Hashtable<>();
    private static Map<Integer, Session> userSessionMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(ActivityFeedWebsocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") Integer uid) throws IOException {
        logger.info("New activity feed connection: User " + uid);

        sessionUserMap.put(session, uid);
        userSessionMap.put(uid, session);
        logger.info("Session mappings updated for user: " + uid);

        // Send activity history
        List<ActivityFeed> history = retrieveHistory(uid);
        logger.info("Retrieved " + history.size() + " history items for user: " + uid);

        for (ActivityFeed activity : history) {
            sendActivityToUser(activity, session);
            logger.info("Sent activity to user: " + activity.getMessage());
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        logger.info("Received message from user " + sessionUserMap.get(session) + ": " + message);
    }

    @OnClose
    public void onClose(Session session) {
        Integer uid = sessionUserMap.get(session);
        logger.info("Connection closed: User " + uid);

        sessionUserMap.remove(session);
        userSessionMap.remove(uid);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("WebSocket error for user " + sessionUserMap.get(session), throwable);
    }

    private List<ActivityFeed> retrieveHistory(Integer uid) {
        User user = fetchUserFromId(uid);
        if (user == null) return new ArrayList<>();

        Set<GroupMember> memberships = user.getMembered();
        List<Integer> groupIds = memberships.stream()
                .map(member -> member.getGroup().getId())
                .toList();

        Timestamp oneWeekAgo = new Timestamp(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000);
        return feedRepo.findRecentActivitiesForGroups(groupIds, oneWeekAgo);
    }

    private User fetchUserFromId(Integer id) {
        return userRepo.findByIdWithMemberships(id);
    }

    public void broadcastActivity(ActivityFeed activity) {
        User activityUser = activity.getUser();
        Group group = activity.getGroup();

        if (group == null || activityUser == null) return;

        // Check privacy settings
        PrivacySettings privacy = privacyRepo.findById(activityUser.getUid()).orElse(null);
        if (privacy != null) {
            boolean canShare = switch (activity.getType()) {
                case FOOD_EATEN -> privacy.getFood();
                case GOAL_UPDATE -> privacy.getGoal();
                case ACHIEVEMENT -> privacy.getAchievement();
                default -> true;
            };
            if (!canShare) return;
        }

        // Broadcast to all group members
        for (GroupMember member : group.getMembers()) {
            Session userSession = userSessionMap.get(member.getUser().getUid());
            if (userSession != null && userSession.isOpen()) {
                sendActivityToUser(activity, userSession);
            }
        }
    }

    private void sendActivityToUser(ActivityFeed activity, Session session) {
        try {
            String json = String.format(
                    "{\"type\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\",\"userId\":%d,\"userName\":\"%s\",\"additionalData\":\"%s\"}",
                    activity.getType(),
                    activity.getMessage(),
                    activity.getTimestamp(),
                    activity.getUser().getUid(),
                    activity.getUser().getFName() + " " + activity.getUser().getLName(),
                    activity.getAdditionalData()
            );
            logger.info("Sending message: " + json);
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            logger.error("Error sending activity to user", e);
        }
    }

    public ActivityFeed createAndBroadcastActivity(String message, String type, User user,
                                                   String additionalData, Group group) {
        ActivityFeed activity = new ActivityFeed(
                message,
                type,
                user,
                new Timestamp(System.currentTimeMillis()),
                additionalData,
                group
        );
        feedRepo.save(activity);
        broadcastActivity(activity);
        return activity;
    }
}
