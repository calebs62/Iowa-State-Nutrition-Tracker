package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to COMS 309. (/Help for mapped strings)";
    }

    @GetMapping("/help")
    public String help() {
        return "Mapped strings: help, test, team";
    }

    @GetMapping("/test")
    public String test() {return "This is a test";}

    @GetMapping("/team")
    public String team() {return "Our team is 1_Jubair_6";}

    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 309: " + name;
    }
}
