package com.cellwize.hson.eventbroker.api;

import java.util.concurrent.Future;

public interface EventPublisher<RESULTS_TYPE> {
    Future publishEvent(RESULTS_TYPE measResults);
}
