package org.bma.vento.cmd;

import java.util.Map;

public interface Command extends Runnable {

    CommandType getType();

    String getHost();

    int getPort();

    Map<String, Object> getParams();
}
