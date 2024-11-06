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
    private static PrivacySettingRepository privRepo;
    private static UserRepository userRepo;
    private static GroupRepository groupRepo;
    private static GroupMemberRepository gmRepo;

    @Autowired
    public void setRepository(ActivityFeedRepository afrepo,
                              UserRepository urepo,
                              GroupRepository gr,
                              GroupMemberRepository gm,
                              PrivacySettingRepository pr) {
        userRepo = urepo;
        feedRepo = afrepo;
        groupRepo = gr;
        gmRepo = gm;
        privRepo = pr;
    }

    private static Map<Session, User> sessionUserMap = new Hashtable<>();
    private static Map<User, Session> userSessionMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(ActivityFeedWebsocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") Integer uid)
        throws IOException {
        logger.info("LoggerI");

        User userLogin = userRepo.findById(uid).orElse(null);
        if  (userLogin != null) {
            sessionUserMap.put(session, userLogin);
            userSessionMap.put(userLogin, session);
        }
        retrieveHistory(userLogin);

        broadcastActivity(new ActivityFeed(userLogin.getFName() + "is online.",
                "group update",
                userLogin,
                null,
                null,
                null));
    }

    /*
    Type will be a string that is :  "food eaten" "group update" "achievement" "goal update"
     */
    @OnMessage
    public void onMessage(Session session, String message, String type) {
        ActivityFeed feedItem = new ActivityFeed();
        feedItem.setMessage(message);
        User u = sessionUserMap.get(session);
        feedItem.setUser(u);
        if (type.equals("food eaten") ||
                type.equals("group update") ||
                type.equals("achievement") ||
                type.equals("goal update")) {
            feedItem.setType(type);
        }
        if (checkSetting(u, type)) {
            broadcastActivity(feedItem);
            feedRepo.save(feedItem);
        }
    }

    private boolean checkSetting(User u, String setting) {
        PrivacySettings set = privRepo.getReferenceById(u.getUid());
        if (setting.equals("food eaten")) {
            return set.getFood();
        }
        else if (setting.equals("group update")) {
            return true;
        }
        else if (setting.equals("achievement")) {
            return set.getAchievement();
        }
        else if (setting.equals("goal update")) {
            return set.getGoal();
        }
        else {
            return false;
        }
    }
    private List<ActivityFeed> retrieveHistory(User u) {
        List<ActivityFeed> history = new ArrayList<ActivityFeed>();
        for (GroupMember gm : u.getMembered()) {
            history.addAll(feedRepo.findByGroup(gm.getGroup()));
        }
        Collections.sort(history);
        for (ActivityFeed f : history) {
            feedUpdate(f, u);
        }
        return history;
    }

    private User fetchUserFromId(Integer id) {
        return userRepo.findById(id).orElse(null);
    }

    private void broadcastActivity(ActivityFeed activity) {
        Set<Group> groups = getActivityGroups(activity);
    }

    private void feedUpdate(ActivityFeed item, User user) {
        try {
            userSessionMap.get(user).getBasicRemote().sendText(item.toString());
        }
        catch (IOException e) {
            logger.info("Exception: " + e.getMessage().toString());
            e.printStackTrace();
        }
    }


    private ActivityFeed createFeedItem(String m, String t, User u, Timestamp time,String ad, Group g) {
        ActivityFeed create = new ActivityFeed(m, t, u, time, ad, g);
        feedRepo.save(create);
        return create;
    }

    private boolean userInGroup(User u, Group g) {
        for(GroupMember m : u.getMembered()) {
            if (m.getGroup().equals(g)) {
                return true;
            }
        }
        return false;
    }

    private Set<Group> getActivityGroups (ActivityFeed act) {
        Set<Group> groups = new HashSet<Group>();
        if (act.getUser() != null) {
            for (GroupMember m : act.getUser().getMembered()) {
                groups.add(m.getGroup());
            }
        }
        if (act.getGroup() != null) {
            groups.add(act.getGroup());
        }
        return groups;
    }



}
