package com.odigeo.interview.coding.computer.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.odigeo.interview.coding.computer.service.BattleshipService;
import com.odigeo.interview.coding.domain.event.GameCreatedEvent;
import com.odigeo.interview.coding.domain.event.GameFireEvent;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class KafkaMDB {
    @Autowired
    private BattleshipService battleshipService;
    @Autowired
    private ObjectMapper mapper;

    @SuppressWarnings("rawtypes")
    @KafkaListener(topics = "${topics.gamenew}")
    public void onGameNew(ConsumerRecord record) throws JsonProcessingException {
        log.info("Handled message on topic battleship.game.new: {}", record);
        GameCreatedEvent gameCreated = mapper.readValue(record.value().toString(), GameCreatedEvent.class);
        battleshipService.joinGame(gameCreated.getGameId());
        battleshipService.deployShips(gameCreated.getGameId());
    }

    @SuppressWarnings("rawtypes")
    @KafkaListener(topics = "${topics.gamefieldfire}")
    public void onGameFieldFire(ConsumerRecord record) throws JsonProcessingException {
        log.debug("Handled message on topic battleship.game.field.fire: {}", record);
        GameFireEvent gameFire = mapper.readValue(record.value().toString(), GameFireEvent.class);
        battleshipService.fire(gameFire.getGameId());
    }

}