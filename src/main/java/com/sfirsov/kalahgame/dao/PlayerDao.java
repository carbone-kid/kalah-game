package com.sfirsov.kalahgame.dao;

import com.sfirsov.kalahgame.model.Player;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

@Transactional
public interface PlayerDao extends CrudRepository<Player, Long> {
    Player findByName(String name);
}
