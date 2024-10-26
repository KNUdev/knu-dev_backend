package ua.knu.knudev.fileserviceapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/guest")
    public String guest() {
        return "You are GUEST!";
    }

    @GetMapping("/user")
    public String secured() {
        return "You are USER!";
    }

    @GetMapping("/admin")
    public String admin() {
        return "You are ADMIN!";
    }

    @GetMapping("/owner")
    public String owner() {
        return "You are OWNER!";
    }

    @GetMapping(value = "/cyrillic", produces = "text/plain;charset=UTF-8")
    public String testCyrillic() {
        return "Перевірка нашого українського алфавіту!";
    }

}