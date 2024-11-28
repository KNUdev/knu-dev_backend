package ua.knu.knudev.rest.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestUIController {

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("giga", "Giga nigga");
        return "admin/test";
    }
}
