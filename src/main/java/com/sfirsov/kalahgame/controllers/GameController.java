package com.sfirsov.kalahgame.controllers;

import com.sfirsov.kalahgame.game.Engine;
import com.sfirsov.kalahgame.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GameController {

    @Autowired
    Engine engine;

    @RequestMapping(method = RequestMethod.POST, value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public Player register(@RequestBody Player player) {
        return engine.addPlayer(player);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Player login(@RequestBody Player player) {
        return engine.login(player);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/createGame", produces = MediaType.APPLICATION_JSON_VALUE)
    public GameState createGame(@RequestBody Player player) {
        return engine.addGame(player.getId());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/joinGame", produces = MediaType.APPLICATION_JSON_VALUE)
    public GameState joinGame(@RequestBody JoinGameRequest joinGameRequest) {
        return engine.joinGame(joinGameRequest.getPlayerUid(), joinGameRequest.getOpponentName());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/move", produces = MediaType.APPLICATION_JSON_VALUE)
    public GameState move(@RequestBody MoveRequest moveRequest) {
        return engine.move(moveRequest.getPlayerUid(), moveRequest.getPit());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/getGameState", produces = MediaType.APPLICATION_JSON_VALUE)
    public GameState getGameState(@RequestBody Player player) {
        return engine.getGameState(player.getId());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/leaveGame", produces = MediaType.APPLICATION_JSON_VALUE)
    public Player leaveGame(@RequestBody Player player) {
        engine.leaveGame(player);
        return player;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAvailableGames", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AvailableGame> getAvailableGames() {
        return engine.getAvailableGames();
    }
}
