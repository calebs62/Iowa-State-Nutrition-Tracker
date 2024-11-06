package coms309.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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

    @Autowired
    public void setRepository(ActivityFeedRepository afrepo, UserRepository urepo) {
        userRepo = urepo;
        feedRepo = afrepo;
    }

    private static Map<Session, Integer> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(ActivityFeedWebsocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") Integer uid)
        throws IOException {
        logger.info("LoggerI");

        retrieveHistory(uid);
    }

    private List<ActivityFeed> retrieveHistory(Integer uid) {
        List<ActivityFeed> history = new ArrayList<ActivityFeed>();
        return history;
    }

    private User fetchUserFromId(Integer id) {
        return userRepo.findById(id).orElse(null);
    }

    private void broadcastActivity(ActivityFeed activity) {
        User user = activity.getUser();
        Group group = activity.getGroup();

    }

    private void feedUpdate(ActivityFeed item, String user) {
        try {
            usernameSessionMap.get(user).getBasicRemote().sendText(item.getMessage());
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



}
