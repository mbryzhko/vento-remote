package org.bma.vento.schedule.durable;

/**
 * Reads / Writes scenario state from storage.
 */
public interface ScenarioStateStore {

    ScenarioState readState(String scenarioName);

    void writeState(String scenarioName, ScenarioState state);
}
