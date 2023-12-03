package org.bma.vento.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DurabilityProperties {

    /**
     * Enabling durability mode for Scheduling Service.
     * The service will store date time of last scenario run.
     */
    private boolean enable = false;
    private String storeFolderPath = "schedule-store";
}
