package com.sfirsov.kalahgame.game;

import com.sfirsov.kalahgame.dao.PlayerDao;
import com.sfirsov.kalahgame.model.AvailableGame;
import com.sfirsov.kalahgame.model.GameState;
import com.sfirsov.kalahgame.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// The game Engine is managing games and players.

@Component
public class Engine {

    @Autowired
    PlayerDao playerDao;

    private List<Game> games = Collections.synchronizedList(new ArrayList<Game>());
    private Map<Game, Long> lastGameActivity = Collections.synchronizedMap(new HashMap<Game, Long>());

    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    private void Init() {
        executorService.scheduleAtFixedRate(() -> runGarbageCollector(), 0, 1, TimeUnit.MINUTES);
    }

    // This is to delete inactive games
    private void runGarbageCollector() {
        List<Game> inactiveGames = lastGameActivity.entrySet().stream()
                .filter(entry -> (new Date()).getTime() - entry.getValue() > 1000 * 60 * 10)
                .map(entry -> entry.getKey())
                .collect(Collectors.toList());

        inactiveGames.forEach(game -> {
            removeGame(game);
        });
    }

    private void updateLastGameActivity(Game game) {
        lastGameActivity.put(game, (new Date()).getTime());
    }

    private void removeGame(Game game) {
        games.remove(game);
        lastGameActivity.remove(game);
    }

    public Player addPlayer(Player player) {
        Player existingPlayer = playerDao.findByName(player.getName());

        if(existingPlayer != null) {
            player.setId(-1);
            return player;
        }

        if(player.getName().equals("") || player.getPassword().equals("")) {
            player.setId(-1);
            return player;
        }

        return playerDao.save(player);
    }

    public Player login(Player player) {
        Player existingPlayer = playerDao.findByName(player.getName());

        if(existingPlayer == null) {
            player.setId(-1);
            return player;
        }

        return existingPlayer;
    }

    public boolean leaveGame(Player player) {
        Optional<Game> game = findPlayerGame(player.getId());
        if (game.isPresent()) {
            removeGame(game.get());
            return true;
        }

        return false;
    }

    public GameState addGame(long playerId) {
        if (findPlayerGame(playerId).isPresent()) {
            return null;
        }

        Game game = new Game();
        Player player = playerDao.findOne(playerId);

        String playerName = player.getName();
        game.setPlayer1(playerName);
        games.add(game);

        updateLastGameActivity(game);

        return game.getGameState(playerName);
    }

    public GameState joinGame(long playerId, String opponentName) {
        Optional<Game> gameToJoin = games.stream().filter(game -> game.getPlayer1().equals(opponentName) && "".equals(game.getPlayer2())).findFirst();
        if (gameToJoin.isPresent()) {
            Player player = playerDao.findOne(playerId);
            gameToJoin.get().setPlayer2(player.getName());

            updateLastGameActivity(gameToJoin.get());

            return gameToJoin.get().getGameState(player.getName());
        }

        return null;
    }

    private Optional<Game> findPlayerGame(long playerId) {
        Player player = playerDao.findOne(playerId);
        return games.stream().filter(g ->
                g.getPlayer1().equals(player.getName()) || g.getPlayer2().equals(player.getName())).findFirst();
    }

    public GameState move(long playerId, int pit) {
        Player player = playerDao.findOne(playerId);
        Optional<Game> game = findPlayerGame(playerId);

        if (game.isPresent()) {
            // The game is over, or wrong player's move
            String whosTurn = game.get().whosTurn();
            if (game.get().isGameOver() || !player.getName().equals(whosTurn)) {
                return game.get().getGameState(player.getName());
            }
            else {
                updateLastGameActivity(game.get());
                return game.get().move(player.getName(), pit);
            }
        }

        return null;
    }

    public GameState getGameState(long playerId) {
        Player player = playerDao.findOne(playerId);
        Optional<Game> game = findPlayerGame(playerId);

        if (game.isPresent()) {
            return game.get().getGameState(player.getName());
        }

        return null;
    }

    public List<AvailableGame> getAvailableGames() {
        // Looking for games with only 1 player
        List<Game> pendingGames = games.stream().filter(game -> !game.isStarted()).collect(Collectors.toList());

        // Constructing desired type to return
        return pendingGames.stream().map(game -> {
            String opponentName = "".equals(game.getPlayer1()) ? game.getPlayer2() : game.getPlayer1();
            AvailableGame availableGame = new AvailableGame();
            availableGame.setOpponent(opponentName);
            return availableGame;
        }).collect(Collectors.toList());
    }
}
