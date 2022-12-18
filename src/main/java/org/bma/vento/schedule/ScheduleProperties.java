package org.bma.vento.schedule;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class ScheduleProperties {

    private final DurabilityProperties durabilityProperties = new DurabilityProperties();
    private final Collection<Scenario> scenario = new ArrayList<>();
}
