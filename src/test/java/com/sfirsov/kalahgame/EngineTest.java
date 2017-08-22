package com.sfirsov.kalahgame;

import com.sfirsov.kalahgame.game.Engine;
import com.sfirsov.kalahgame.model.GameState;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EngineTest {

    @Test
    public void addPlayerTest() {
        Engine engine = new Engine();

        int playerUid = engine.addPlayer("First");
        Assert.assertNotEquals(-1, playerUid);

        playerUid = engine.addPlayer("Second");
        Assert.assertNotEquals(-1, playerUid);
    }

    @Test
    public void addExistingPlayerTest() {
        Engine engine = new Engine();

        int playerUid = engine.addPlayer("First");
        Assert.assertNotEquals(-1, playerUid);

        playerUid = engine.addPlayer("First");
        Assert.assertEquals(-1, playerUid);
    }

    @Test
    public void addGameTest() {
        Engine engine = new Engine();
        int playerUid = engine.addPlayer("First");

        Assert.assertNotNull(engine.addGame(playerUid));
        Assert.assertNull("Player is already in the game.", engine.addGame(playerUid));
    }

    @Test
    public void joinGameTest() {
        Engine engine = new Engine();
        int firstPlayerUid = engine.addPlayer("First");
        engine.addGame(firstPlayerUid);

        int secondPlayerUid = engine.addPlayer("Second");
        Assert.assertNotNull(engine.joinGame(secondPlayerUid, "First"));
    }

    @Test
    public void moveTest() {
        Engine engine = new Engine();
        int firstPlayerUid = engine.addPlayer("First");
        engine.addGame(firstPlayerUid);
        int secondPlayerUid = engine.addPlayer("Second");
        engine.joinGame(secondPlayerUid, "First");

        GameState gameState1 = engine.move(firstPlayerUid, 0);
        Assert.assertEquals(1, gameState1.getPlayerKalah());
        Assert.assertEquals(0, gameState1.getOpponentKalah());

        GameState gameState2 = engine.move(firstPlayerUid, 1);
        Assert.assertEquals(2, gameState2.getPlayerKalah());
        Assert.assertEquals(0, gameState2.getOpponentKalah());

        GameState gameState3 = engine.move(secondPlayerUid, 2);
        Assert.assertEquals(1, gameState3.getPlayerKalah());
        Assert.assertEquals(2, gameState3.getOpponentKalah());
    }
}
