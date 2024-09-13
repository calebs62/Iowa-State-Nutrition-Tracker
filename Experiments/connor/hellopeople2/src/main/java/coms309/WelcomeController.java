package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple Hello World Controller to display the string returned
 *
 * @author Vivek Bengre
 */

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Welcome to Connor's Webpage. Try /info.";
    }

    @GetMapping("/info")
    public String info(){
        return "This is a website for connecting characters and places in stories.";
    }
}
