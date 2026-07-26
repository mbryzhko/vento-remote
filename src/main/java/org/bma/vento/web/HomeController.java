package org.bma.vento.web;

import lombok.RequiredArgsConstructor;
import org.bma.vento.schedule.ScheduleProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ScheduleProperties scheduleProperties;

    @Value("${app.version}")
    private String appVersion;

    @GetMapping({"/", "/index.html"})
    public String home(Model model) {
        model.addAttribute("properties", scheduleProperties);
        model.addAttribute("appVersion", appVersion);
        return "index";
    }
}
