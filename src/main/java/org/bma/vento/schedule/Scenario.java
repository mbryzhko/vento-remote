package org.bma.vento.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Scenario {

    private String name;
    private String cron;
    private List<CommandProperties> commands;

}
