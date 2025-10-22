package org.baeldung.web.controller.projet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/faysal/")
public class AuthController {
    @GetMapping("/all_hotels_list")
    public String login() {
        return "Hebergement/Hebergement1/all_hotels_listt";
    }
}
