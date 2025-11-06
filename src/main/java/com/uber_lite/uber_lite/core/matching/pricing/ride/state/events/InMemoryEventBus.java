package com.uber_lite.uber_lite.core.matching.pricing.ride.state.events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class InMemoryEventBus implements EventBus{
    
        private final Map<Class<?>, CopyOnWriteArrayList<Consumer<?>>> subscribers = new ConcurrentHashMap<>();
        private final Executor executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setName("eventbus-exec");
        t.setDaemon(true);
        return t;
    });

    @Override
    public <T extends DomainEvent> void publish(T event) {
        List<Consumer<?>> handlers = subscribers.getOrDefault(event.getClass(), new CopyOnWriteArrayList<>());
        if (handlers.isEmpty()) return;

        for (Consumer<?> raw : handlers) {
            @SuppressWarnings("unchecked")
            Consumer<T> handler = (Consumer<T>) raw;
            executor.execute(() -> handler.accept(event));
        }
    }
    @Override
    public <T extends DomainEvent> void subscribe(Class<T> eventType, Consumer<T> handler) {
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

}
