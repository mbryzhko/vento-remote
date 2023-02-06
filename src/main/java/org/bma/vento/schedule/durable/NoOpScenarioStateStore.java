package org.bma.vento.schedule.durable;

/**
 * Implementation of store that does nothing.
 * This is used when durability settings were not configured.
 */
public class NoOpScenarioStateStore implements ScenarioStateStore {
    @Override
    public ScenarioState readState(String scenarioName) {
        return null;
    }

    @Override
    public void writeState(String scenarioName, ScenarioState state) {
        // no op
    }
}
