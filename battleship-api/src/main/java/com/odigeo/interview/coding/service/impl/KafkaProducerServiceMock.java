package com.odigeo.interview.coding.service.impl;

import com.odigeo.interview.coding.domain.event.GameCreatedEvent;
import com.odigeo.interview.coding.domain.event.GameFireEvent;
import com.odigeo.interview.coding.service.KafkaProducerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile("test")
@Log4j2
public class KafkaProducerServiceMock implements KafkaProducerService {

    @Override
    public void publish(GameCreatedEvent event) {
        log.info("Send GameCreate Event " + event);
    }

    @Override
    public void publish(GameFireEvent event) {
        log.info("Send GameFire Event " + event);
    }

}
