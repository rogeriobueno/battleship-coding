package com.odigeo.interview.coding.service;

import com.odigeo.interview.coding.domain.event.GameCreatedEvent;
import com.odigeo.interview.coding.domain.event.GameFireEvent;

public interface KafkaProducerService {
    void publish(GameCreatedEvent event);

    void publish(GameFireEvent event);
}
