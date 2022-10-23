package com.odigeo.interview.coding.repository;

import com.odigeo.interview.coding.domain.model.Game;

import java.util.List;
import java.util.Optional;


public interface GameRepository {

    void saveOrUpdateGame(Game game);

    Optional<Game> getGame(String id);

    List<Game> getGames();

}
