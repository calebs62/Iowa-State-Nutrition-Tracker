package coms309.controller;

import coms309.entity.Group;
import coms309.entity.GroupMember;
import coms309.entity.GroupMemberKey;
import coms309.repository.GroupMemberRepository;
import coms309.repository.GroupRepository;
import coms309.repository.MessageRepository;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

@Controller
@ServerEndpoint(value = "/chat/{username}")
public class MessageWebsocket {
    private static MessageRepository msgRepo;
    @Autowired
    public void setMsgRepo(MessageRepository repo){
        msgRepo = repo;
    }
    @Autowired
    GroupMemberRepository memberRepo;

    private static Map<Session, GroupMemberKey> sessionMemberKeyMap = new Hashtable<>(); //TODO - should I make string->groupmemberkey?
    private static Map<GroupMemberKey, Session> memberKeySessionMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(MessageWebsocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("memeberKey") GroupMemberKey memberKey)
            throws IOException {
        logger.info("Entered into Open");
        GroupMember member = memberRepo.findById(memberKey).orElse(null);
        if (member == null){
            return;
        }

        sessionMemberKeyMap.put(session, memberKey);
        memberKeySessionMap.put(memberKey, session);

        String message = "User: " + member.getUser().getFName() + " has joined the Chat";
        sendMessageToGroup(member.getGroup(), message);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        logger.info("Entered into Close");

        GroupMemberKey key = sessionMemberKeyMap.get(session);
        sessionMemberKeyMap.remove(session);
        memberKeySessionMap.remove(key);

        GroupMember member = memberRepo.findById(key).orElse(null);

        if (member != null) {
            String message = member.getUser().getFName() + " disconnected";
            sendMessageToGroup(member.getGroup(), message);
        }
    }

    private void sendMessageToUser(){}

    private void broadcast(String message){}

    private void sendMessageToGroup(Group group, String message){
        Set<GroupMember> groupMembers = group.getMembers();
        Set<GroupMemberKey> memberKeys = new HashSet<>();
        for(GroupMember mem : groupMembers){
            memberKeys.add(mem.getId());
        }
        sessionMemberKeyMap.forEach((session, groupMemberKey)->{
            if (memberKeys.contains(groupMemberKey)){
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e){
                    logger.info("[Broadcast Exception] " + e.getMessage());
                }
            }
        });
    }
}
