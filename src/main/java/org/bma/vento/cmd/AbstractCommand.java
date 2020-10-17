package org.bma.vento.cmd;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bma.vento.client.VentoClient;
import org.bma.vento.client.VentoClientException;
import org.bma.vento.schedule.CommandProperties;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractCommand implements Command {

    private final VentoClient client;
    @Getter
    private final CommandType type;
    @Getter
    private final String host;
    @Getter
    private final int port;
    @Getter
    private final Map<String, Object> params;

    protected AbstractCommand(VentoClient client, CommandProperties commandProperties) {
        this.client = client;
        this.type = commandProperties.getType();
        this.host = commandProperties.getHost();
        this.port = commandProperties.getPort();
        this.params = Collections.unmodifiableMap(commandProperties.getParams());
    }

    @Override
    public final void run() {
        try {
            log.debug("Running command {} with params {}", getType(),  paramsToString());
            this.doRun(client);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error executing command: " + this.getClass().getSimpleName()
                    + ". " + e.getMessage(), e);
        }
    }

    private String paramsToString() {
        return getParams().keySet().stream()
                .map(key -> key + "=" + getParams().get(key))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    protected abstract void doRun(VentoClient client) throws VentoClientException;
}
