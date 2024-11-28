package ua.knu.knudev.rest.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/publications")
public class UIController {

    @GetMapping
    public String test(Model model) {
        String testData = "Giga Nigga";
        model.addAttribute("gigaNigga", testData);
        return "test";
    }
}
