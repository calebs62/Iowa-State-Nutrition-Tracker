package coms309.controller;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import coms309.repository.ActivityFeedRepository;
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
@ServerEndpoint(value = "activity/{uid}")
public class ActivityFeedWebsocket {

    private static ActivityFeedRepository feedRepo;
    @Autowired
    public void setFeedRepository(ActivityFeedRepository repo) {
        feedRepo = repo;
    }

    private static Map<Session, String> sessionUsernameMap = new Hashtable<>();
    private static Map<String, Session> usernameSessionMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(ActivityFeedWebsocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username)
        throws IOException {
        logger.info("LoggerI");
    }


}
