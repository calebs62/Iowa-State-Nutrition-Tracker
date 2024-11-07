package coms309.controller;

import coms309.entity.Group;
import coms309.entity.GroupMember;
import coms309.entity.GroupMemberKey;
import coms309.repository.GroupMemberRepository;
import coms309.repository.MessageRepository;

import jakarta.websocket.*;
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
@ServerEndpoint(value = "/chat/{username}/{uid}/{gid}")
public class MessageWebsocket {
    private static GroupMemberRepository memberRepo;
    private static MessageRepository msgRepo;
    @Autowired
    public void setMsgRepo(MessageRepository repo){
        msgRepo = repo;
    }
    @Autowired
    public void setMemberRepo(GroupMemberRepository repo){
        memberRepo = repo;
    }

    private static Map<Session, GroupMemberKey> sessionMemberKeyMap = new Hashtable<>();
    private static Map<GroupMemberKey, Session> memberKeySessionMap = new Hashtable<>();
    private final Logger logger = LoggerFactory.getLogger(MessageWebsocket.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("uid") int uid, @PathParam("gid") int gid)
            throws IOException {
        logger.info("Entered into Open");
        GroupMemberKey memberKey = new GroupMemberKey(gid, uid);
        GroupMember member = memberRepo.findById(memberKey).orElse(null);
        if (member == null){
            logger.info("member not found: " + uid + ", " + gid);
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
        if (key == null){
            return;
        }
        memberKeySessionMap.remove(key);

        GroupMember member = memberRepo.findById(key).orElse(null);

        if (member != null) {
            String message = member.getUser().getFName() + " disconnected";
            sendMessageToGroup(member.getGroup(), message);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException{
        logger.info("Entered into Message: Got Message: " + message);
        GroupMemberKey memberKey= sessionMemberKeyMap.get(session);
        GroupMember member = memberRepo.findById(memberKey).orElse(null);
        if (member == null){
            return;
        }
        if (message.startsWith("@")){
            //TODO
        } else {
          sendMessageToGroup(member.getGroup(), member.getUser().getFName() + ": " + message);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable){
        logger.info("Entered into Error");
        throwable.printStackTrace();
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
