package org.bma.vento.schedule;

import lombok.Data;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

@Data
public class ScheduleProperties {

    private DurabilityProperties durabilityProperties = new DurabilityProperties();
    private Collection<Scenario> scenario = new ArrayList<>();

    public static ScheduleProperties createFrom(InputStream stream) {
        return new Yaml().loadAs(stream, ScheduleProperties.class);
    }

    public boolean isDurabilityEnabled() {
        return durabilityProperties != null && durabilityProperties.isEnable();
    }
}
