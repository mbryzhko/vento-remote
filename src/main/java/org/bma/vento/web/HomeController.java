package org.bma.vento.web;

import lombok.RequiredArgsConstructor;
import org.bma.vento.schedule.ScheduleProperties;
import org.bma.vento.schedule.SchedulingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ScheduleProperties scheduleProperties;

    private final SchedulingService schedulingService;

    @Value("${app.version}")
    private String appVersion;

    @GetMapping({"/", "/index.html"})
    public String home(Model model) {
        model.addAttribute("properties", scheduleProperties);
        model.addAttribute("appVersion", appVersion);
        model.addAttribute("scenarioStates", schedulingService.getScenarioState());
        return "index";
    }
}
