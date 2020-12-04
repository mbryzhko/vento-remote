package org.bma.vento.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bma.vento.cmd.CommandType;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommandProperties {

    private CommandType type;
    private String host;
    private int port = 4000;
    private Map<String, Object> params = new HashMap<>();
}
