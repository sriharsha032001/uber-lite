package com.uber_lite.uber_lite.core.matching.pricing.ride.state.events;

import java.util.function.Consumer;

public interface EventBus {

    <T extends DomainEvent> void publish(T event);
    <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<T> handler);
}
