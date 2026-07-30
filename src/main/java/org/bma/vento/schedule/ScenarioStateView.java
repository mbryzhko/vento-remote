package org.bma.vento.schedule;

import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ScenarioStateView {
    String name;
    LocalDateTime lastExecution;
}
