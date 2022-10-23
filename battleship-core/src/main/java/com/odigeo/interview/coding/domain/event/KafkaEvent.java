package com.odigeo.interview.coding.domain.event;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class KafkaEvent {

    public String json() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(this);
    }

}
