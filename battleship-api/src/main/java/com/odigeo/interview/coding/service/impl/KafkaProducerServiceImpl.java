package com.odigeo.interview.coding.service.impl;

import com.odigeo.interview.coding.domain.event.GameCreatedEvent;
import com.odigeo.interview.coding.domain.event.GameFireEvent;
import com.odigeo.interview.coding.exception.KafkaProducerException;
import com.odigeo.interview.coding.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Primary
@Profile("prod")
public class KafkaProducerServiceImpl implements KafkaProducerService {

    @Value("${topics.gamenew}")
    private String topicNewGame;
    @Value("${topics.gamefieldfire}")
    private String topicGameFire;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publish(GameCreatedEvent event) {
        try {
            kafkaTemplate.send(topicNewGame, event.json());
        } catch (Exception e) {
            throw new KafkaProducerException(e);
        }
    }

    @Override
    public void publish(GameFireEvent event) {
        try {
            kafkaTemplate.send(topicGameFire, event.json());
        } catch (Exception e) {
            throw new KafkaProducerException(e);
        }
    }

}
