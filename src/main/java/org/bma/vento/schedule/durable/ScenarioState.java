package org.bma.vento.schedule.durable;

import lombok.NonNull;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
public class ScenarioState implements Serializable {
    static final long serialVersionUID = 1;

    @NonNull
    LocalDateTime lastExecution;
}
