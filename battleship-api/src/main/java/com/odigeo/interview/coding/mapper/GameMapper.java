package com.odigeo.interview.coding.mapper;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.odigeo.interview.coding.domain.model.Cell;
import com.odigeo.interview.coding.domain.model.Coordinate;
import com.odigeo.interview.coding.domain.model.Game;
import com.odigeo.interview.coding.domain.model.ship.Ship;
import com.odigeo.interview.coding.domain.model.ship.ShipType;
import com.odigeo.interview.coding.exception.WrongCoordinateException;
import com.odigeo.interview.coding.repository.entity.GameEntity;
import org.mapstruct.Mapper;

import java.io.IOException;

@Mapper(componentModel = "spring")
public interface GameMapper {

    GameEntity map(Game source);

    Game map(GameEntity source);

    default String serializeField(Cell[][] source) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(source);
    }

    default Cell[][] deserializeField(String source) {
        SimpleModule module = new SimpleModule();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true); // Remove double quotes
        module.addDeserializer(Ship.class, new ShipJsonDeserializer());
        mapper.registerModule(module);
        try {
            return mapper.readValue(source, Cell[][].class);
        } catch (JsonProcessingException e) {
            throw new WrongCoordinateException(source);
        }
    }

    class ShipJsonDeserializer extends StdDeserializer<Ship> {

        public ShipJsonDeserializer() {
            this(null);
        }

        protected ShipJsonDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Ship deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode json = p.getCodec().readTree(p);
            final String shipType = json.get("shipType").asText();
            Ship ship = ShipType.valueOf(shipType).newInstance();
            for (JsonNode coordinate : json.withArray("coordinates")) {
                ship.getCoordinates().add(new Coordinate(coordinate.get("value").asText(),
                        coordinate.get("column").asInt(), coordinate.get("row").asInt()));
            }
            return ship;
        }

    }
}
