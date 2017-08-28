package com.sfirsov.kalahgame;

import com.sfirsov.kalahgame.game.Engine;
import com.sfirsov.kalahgame.model.GameState;
import com.sfirsov.kalahgame.model.Player;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EngineTest {

    @Autowired
    Engine engine;

    @Test
    public void addPlayerTest() {
        Player player = new Player();

        player.setName("First");
        player.setPassword("First");

        Player addedPlayer = engine.addPlayer(player);
        Assert.assertNotNull(addedPlayer);
        Assert.assertNotEquals(-1, addedPlayer.getId());
        Assert.assertEquals("First", addedPlayer.getName());
        Assert.assertEquals("First", addedPlayer.getPassword());
    }

    @Test
    public void addExistingPlayerTest() {
        Player player = new Player();

        player.setName("First");
        player.setPassword("First");

        Player addedPlayer = engine.addPlayer(player);
        Assert.assertNotNull(addedPlayer);
        Assert.assertNotEquals(-1, addedPlayer.getId());
        Assert.assertEquals("First", addedPlayer.getName());
        Assert.assertEquals("First", addedPlayer.getPassword());

        addedPlayer = engine.addPlayer(player);
        Assert.assertEquals(-1, addedPlayer.getId());
    }

    @Test
    public void addGameTest() {
        Player player = new Player();
        player.setName("First");
        player.setPassword("First");

        Player addedPlayer = engine.addPlayer(player);

        Assert.assertNotNull(engine.addGame(addedPlayer.getId()));
        Assert.assertNull("Player is already in the game.", engine.addGame(addedPlayer.getId()));
    }

    @Test
    public void joinGameTest() {
        Player firstPlayer = new Player();
        firstPlayer.setName("First");
        firstPlayer.setPassword("First");
        Player addedFirstPlayer = engine.addPlayer(firstPlayer);
        engine.addGame(addedFirstPlayer.getId());

        Player secondPlayer = new Player();
        secondPlayer.setName("Second");
        secondPlayer.setPassword("Second");
        Player addedSecondPlayer = engine.addPlayer(secondPlayer);
        Assert.assertNotNull(engine.joinGame(addedSecondPlayer.getId(), "First"));
    }

    @Test
    public void moveTest() {
        Player firstPlayer = new Player();
        firstPlayer.setName("First");
        firstPlayer.setPassword("First");
        Player addedFirstPlayer = engine.addPlayer(firstPlayer);
        engine.addGame(addedFirstPlayer.getId());

        Player secondPlayer = new Player();
        secondPlayer.setName("Second");
        secondPlayer.setPassword("Second");
        Player addedSecondPlayer = engine.addPlayer(secondPlayer);

        engine.joinGame(secondPlayer.getId(), "First");

        GameState gameState1 = engine.move(addedFirstPlayer.getId(), 0);
        Assert.assertEquals(1, gameState1.getPlayerKalah());
        Assert.assertEquals(0, gameState1.getOpponentKalah());

        GameState gameState2 = engine.move(addedFirstPlayer.getId(), 1);
        Assert.assertEquals(2, gameState2.getPlayerKalah());
        Assert.assertEquals(0, gameState2.getOpponentKalah());

        GameState gameState3 = engine.move(addedSecondPlayer.getId(), 2);
        Assert.assertEquals(1, gameState3.getPlayerKalah());
        Assert.assertEquals(2, gameState3.getOpponentKalah());
    }
}
