package com.sfirsov.kalahgame;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sfirsov.kalahgame.model.GameState;
import com.sfirsov.kalahgame.model.JoinGameRequest;
import com.sfirsov.kalahgame.model.MoveRequest;
import com.sfirsov.kalahgame.model.Player;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    private ObjectMapper mapper = new ObjectMapper();
    private List<Player> playersToLogout = new ArrayList<Player>();

    Player player1;
    Player player2;
    Player player3;
    Player player4;

    @Before
    public void setUp() throws Exception {
        player1 = new Player();
        player1.setName("Sergey");

        player2 = new Player();
        player2.setName("Gina");

        player3 = new Player();
        player3.setName("Smith");

        player4 = new Player();
        player4.setName("Gordon");
    }

    @After
    public void tearDown() {
        playersToLogout.forEach(player -> {
            try {
                logout(player);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        playersToLogout.clear();
    }

    private Player login(Player player) throws Exception {
        MvcResult result = mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(player)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Player playerLoggedIn = mapper.readValue(content, Player.class);

        playersToLogout.add(playerLoggedIn);

        return playerLoggedIn;
    }

    private Player logout(Player player) throws Exception {
        MvcResult result = mvc.perform(post("/logout").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(player)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        return mapper.readValue(content, Player.class);
    }

    private GameState createGame(Player player) throws Exception {
        MvcResult result = mvc.perform(put("/createGame").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(player)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        return mapper.readValue(content, GameState.class);
    }

    private GameState joinGame(JoinGameRequest joinGameRequest) throws Exception {
        MvcResult result = mvc.perform(post("/joinGame").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(joinGameRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        return mapper.readValue(content, GameState.class);
    }

    @Test
    public void loginLogoutTest() throws Exception {
        Assert.assertEquals(0, player1.getUid());

        Player playerLoggedIn = login(player1);
        Assert.assertNotEquals(0, playerLoggedIn.getUid());

        // Trying to login with the same name
        playerLoggedIn = login(player1);
        Assert.assertEquals(-1, playerLoggedIn.getUid());

        // Trying to logout
        Player playerLoggedOut = logout(playerLoggedIn);
        Assert.assertEquals(-1, playerLoggedOut.getUid());
    }

    @Test
    public void createGameTest() throws Exception {
        // Add player
        Player playerLoggedIn = login(player1);

        // Create game
        GameState gameState = createGame(playerLoggedIn);

        Arrays.stream(gameState.getPits()).forEach(stones -> Assert.assertEquals(6, stones));
        Assert.assertEquals(0, gameState.getPlayerKalah());
        Assert.assertEquals(0, gameState.getOpponentKalah());
        Assert.assertEquals(false, gameState.isGameOver());
    }

    @Test
    public void joinGameTest() throws Exception {
        Player playerLoggedIn1 = login(player1);
        createGame(playerLoggedIn1);

        Player playerLoggedIn2 = login(player2);

        JoinGameRequest joinGameRequest = new JoinGameRequest();
        joinGameRequest.setPlayerUid(playerLoggedIn2.getUid());
        joinGameRequest.setOpponentName(player1.getName());
        GameState gameState = joinGame(joinGameRequest);

        Arrays.stream(gameState.getPits()).forEach(stones -> Assert.assertEquals(6, stones));
        Assert.assertEquals(0, gameState.getPlayerKalah());
        Assert.assertEquals(0, gameState.getOpponentKalah());
        Assert.assertEquals(false, gameState.isGameOver());
    }

    @Test
    public void moveTest() throws Exception {
        Player playerLoggedIn1 = login(player1);
        createGame(playerLoggedIn1);

        Player playerLoggedIn2 = login(player2);
        JoinGameRequest joinGameRequest = new JoinGameRequest();
        joinGameRequest.setPlayerUid(playerLoggedIn2.getUid());
        joinGameRequest.setOpponentName(player1.getName());
        joinGame(joinGameRequest);

        MoveRequest moveRequest = new MoveRequest();
        moveRequest.setPlayerUid(playerLoggedIn1.getUid());
        moveRequest.setPit(3);
        MvcResult result = mvc.perform(post("/move").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(moveRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        GameState gameState = mapper.readValue(content, GameState.class);

        Assert.assertEquals(6, gameState.getPits()[0]);
        Assert.assertEquals(6, gameState.getPits()[1]);
        Assert.assertEquals(6, gameState.getPits()[2]);
        Assert.assertEquals(0, gameState.getPits()[3]);
        Assert.assertEquals(7, gameState.getPits()[4]);
        Assert.assertEquals(7, gameState.getPits()[5]);
        Assert.assertEquals(7, gameState.getPits()[6]);
        Assert.assertEquals(7, gameState.getPits()[7]);
        Assert.assertEquals(7, gameState.getPits()[8]);
        Assert.assertEquals(6, gameState.getPits()[9]);
        Assert.assertEquals(6, gameState.getPits()[10]);
        Assert.assertEquals(6, gameState.getPits()[11]);
        Assert.assertEquals(1, gameState.getPlayerKalah());
        Assert.assertEquals(0, gameState.getOpponentKalah());
        Assert.assertEquals(false, gameState.isGameOver());
    }

    @Test
    public void getGameState() throws Exception {
        Player playerLoggedIn = login(player1);
        createGame(playerLoggedIn);

        MvcResult result = mvc.perform(post("/getGameState").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(playerLoggedIn)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        GameState currentGameState = mapper.readValue(content, GameState.class);

        Arrays.stream(currentGameState.getPits()).forEach(stones -> Assert.assertEquals(6, stones));
        Assert.assertEquals(0, currentGameState.getPlayerKalah());
        Assert.assertEquals(0, currentGameState.getOpponentKalah());
        Assert.assertEquals(false, currentGameState.isGameOver());
    }
}
