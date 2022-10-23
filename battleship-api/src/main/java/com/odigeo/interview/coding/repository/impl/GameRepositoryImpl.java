package com.odigeo.interview.coding.repository.impl;


import com.odigeo.interview.coding.domain.model.Game;
import com.odigeo.interview.coding.mapper.GameMapper;
import com.odigeo.interview.coding.repository.GameRepository;
import com.odigeo.interview.coding.repository.entity.GameEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@Component
@Primary
public class GameRepositoryImpl implements GameRepository {

    private final Map<String, GameEntity> dataSource = new ConcurrentHashMap<>();

    @Autowired
    private GameMapper mapper;
    @Override
    public void saveOrUpdateGame(Game game) {
        GameEntity gameEntity = mapper.map(game);
        dataSource.put(gameEntity.getId(), gameEntity);
    }

    @Override
    public Optional<Game> getGame(String id) {
        GameEntity gameEntity = dataSource.get(id);
        Game game = mapper.map(gameEntity);
        return Optional.ofNullable(game);
    }

    @Override
    public List<Game> getGames() {
        Collection<GameEntity> gameEntities = dataSource.values();
        return gameEntities.stream().map(mapper::map).collect(toList());
    }

}
