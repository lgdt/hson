package com.cellwize.hson.eventbroker.api;

public interface EventPublisher<RESULTS_TYPE> {
    void publishEvent(RESULTS_TYPE measResults);
}
