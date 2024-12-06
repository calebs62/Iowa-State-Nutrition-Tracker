package coms309.controller;

import com.fasterxml.jackson.annotation.JsonView;
import coms309.entity.User;
import coms309.repository.UserRepository;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@ServerEndpoint(value="/bmi")
public class BodyTypeController {
    @Autowired
    UserRepository userRepo;
}
