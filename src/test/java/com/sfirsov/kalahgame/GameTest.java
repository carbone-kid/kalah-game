package com.sfirsov.kalahgame;

import com.sfirsov.kalahgame.game.Game;
import com.sfirsov.kalahgame.model.GameState;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameTest {

    @Test
    public void initGameTest() {
        Game game = new Game();
        game.setPlayer1("Nemo");
        GameState gameState = game.getGameState("Nemo");

        Assert.assertNotNull(gameState);
        Arrays.stream(gameState.getPits()).forEach(stones -> Assert.assertEquals(6, stones));
        Assert.assertEquals(0, gameState.getPlayerKalah());
        Assert.assertEquals(0, gameState.getOpponentKalah());
    }

    @Test
    public void getNextPitPlayer1Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Game game = new Game();
        game.setPlayer1("John");

        Method getNextPit = game.getClass().getDeclaredMethod("getNextPit", String.class, int.class);
        getNextPit.setAccessible(true);

        Assert.assertEquals(1, getNextPit.invoke(game, "John", 0));
        Assert.assertEquals(2, getNextPit.invoke(game, "John", 1));
        Assert.assertEquals(3, getNextPit.invoke(game, "John", 2));
        Assert.assertEquals(4, getNextPit.invoke(game, "John", 3));
        Assert.assertEquals(5, getNextPit.invoke(game, "John", 4));
        Assert.assertEquals(6, getNextPit.invoke(game, "John", 5));
        Assert.assertEquals(7, getNextPit.invoke(game, "John", 6));
        Assert.assertEquals(8, getNextPit.invoke(game, "John", 7));
        Assert.assertEquals(9, getNextPit.invoke(game, "John", 8));
        Assert.assertEquals(10, getNextPit.invoke(game, "John", 9));
        Assert.assertEquals(11, getNextPit.invoke(game, "John", 10));
        Assert.assertEquals(12, getNextPit.invoke(game, "John", 11));
        Assert.assertEquals(0, getNextPit.invoke(game, "John", 12));
        Assert.assertEquals(0, getNextPit.invoke(game, "John", 13));
        Assert.assertEquals(1, getNextPit.invoke(game, "John", 14));
        Assert.assertEquals(2, getNextPit.invoke(game, "John", 15));
    }

    @Test
    public void getNextPitPlayer2Test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Game game = new Game();
        game.setPlayer2("Ivan");

        Method getNextPit = Game.class.getDeclaredMethod("getNextPit", String.class, int.class);
        getNextPit.setAccessible(true);

        Assert.assertEquals(1, getNextPit.invoke(game, "Ivan", 0));
        Assert.assertEquals(2, getNextPit.invoke(game, "Ivan", 1));
        Assert.assertEquals(3, getNextPit.invoke(game, "Ivan", 2));
        Assert.assertEquals(4, getNextPit.invoke(game, "Ivan", 3));
        Assert.assertEquals(5, getNextPit.invoke(game, "Ivan", 4));
        Assert.assertEquals(7, getNextPit.invoke(game, "Ivan", 5));
        Assert.assertEquals(7, getNextPit.invoke(game, "Ivan", 6));
        Assert.assertEquals(8, getNextPit.invoke(game, "Ivan", 7));
        Assert.assertEquals(9, getNextPit.invoke(game, "Ivan", 8));
        Assert.assertEquals(10, getNextPit.invoke(game, "Ivan", 9));
        Assert.assertEquals(11, getNextPit.invoke(game, "Ivan", 10));
        Assert.assertEquals(12, getNextPit.invoke(game, "Ivan", 11));
        Assert.assertEquals(13, getNextPit.invoke(game, "Ivan", 12));
        Assert.assertEquals(0, getNextPit.invoke(game, "Ivan", 13));
        Assert.assertEquals(1, getNextPit.invoke(game, "Ivan", 14));
        Assert.assertEquals(2, getNextPit.invoke(game, "Ivan", 15));
    }

    @Test
    public void twoMovesGameTest() {
        Game game = new Game();
        game.init(3);
        game.setPlayer1("Alice");
        game.setPlayer2("Cat");

        GameState gameState1 = game.move("Alice",0);
        GameState gameState2 = game.move("Cat",5);

        Assert.assertNotNull(gameState1);
        Assert.assertNotNull(gameState2);

        // First move
        Assert.assertEquals(0, gameState1.getPits()[0]);
        Assert.assertEquals(4, gameState1.getPits()[1]);
        Assert.assertEquals(4, gameState1.getPits()[2]);
        Assert.assertEquals(4, gameState1.getPits()[3]);
        Assert.assertEquals(3, gameState1.getPits()[4]);
        Assert.assertEquals(3, gameState1.getPits()[5]);
        Assert.assertEquals(3, gameState1.getPits()[6]);
        Assert.assertEquals(3, gameState1.getPits()[7]);
        Assert.assertEquals(3, gameState1.getPits()[8]);
        Assert.assertEquals(3, gameState1.getPits()[9]);
        Assert.assertEquals(3, gameState1.getPits()[10]);
        Assert.assertEquals(3, gameState1.getPits()[11]);
        Assert.assertEquals(0, gameState1.getPlayerKalah());
        Assert.assertEquals(0, gameState1.getOpponentKalah());

        // Second move
        Assert.assertEquals(3, gameState2.getPits()[0]);
        Assert.assertEquals(3, gameState2.getPits()[1]);
        Assert.assertEquals(3, gameState2.getPits()[2]);
        Assert.assertEquals(3, gameState2.getPits()[3]);
        Assert.assertEquals(3, gameState2.getPits()[4]);
        Assert.assertEquals(0, gameState2.getPits()[5]);
        Assert.assertEquals(1, gameState2.getPits()[6]);
        Assert.assertEquals(5, gameState2.getPits()[7]);
        Assert.assertEquals(4, gameState2.getPits()[8]);
        Assert.assertEquals(4, gameState2.getPits()[9]);
        Assert.assertEquals(3, gameState2.getPits()[10]);
        Assert.assertEquals(3, gameState2.getPits()[11]);
        Assert.assertEquals(1, gameState2.getPlayerKalah());
        Assert.assertEquals(0, gameState2.getOpponentKalah());
    }

    @Test
    public void putToEmptyPitGameTest() {
        Game game = new Game();
        game.init(3);
        game.setPlayer1("Alice");
        game.setPlayer2("Cat");

        GameState gameState1 = game.move("Alice", 3);
        Assert.assertEquals(1, gameState1.getPlayerKalah());
        Assert.assertEquals(0, gameState1.getOpponentKalah());

        GameState gameState2 = game.move("Alice", 0);
        Assert.assertEquals(5, gameState2.getPlayerKalah());
        Assert.assertEquals(0, gameState2.getOpponentKalah());
    }

    @Test
    public void gameOverTest() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Game game = new Game();
        game.setPlayer1("Anderson");
        game.setPlayer2("Smith");

        // The game just began it's not finished
        Assert.assertFalse(game.isGameOver());

        Field pitsField = game.getClass().getDeclaredField("pits");
        pitsField.setAccessible(true);
        List<Integer> pits = (List<Integer>) pitsField.get(game);

        // Removing stones form player's one pits
        pits.subList(0, 6).replaceAll(stones -> 0);
        Assert.assertTrue(game.isGameOver());

        // Removing stones form player's two pits
        pits.subList(0, 6).replaceAll(stones -> 6);
        pits.subList(7, 13).replaceAll(stones -> 0);
        Assert.assertTrue(game.isGameOver());
    }

    @Test
    public void collectStonesTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Game game = new Game();
        game.setPlayer1("Anderson");

        Method collectAllStonesMethod = Game.class.getDeclaredMethod("collectAllStones");
        collectAllStonesMethod.setAccessible(true);

        collectAllStonesMethod.invoke(game);

        GameState gameState = game.getGameState("Anderson");
        Arrays.stream(gameState.getPits()).forEach(stones -> Assert.assertEquals(0, stones));
        Assert.assertEquals(36, gameState.getPlayerKalah());
        Assert.assertEquals(36, gameState.getOpponentKalah());
    }
}
