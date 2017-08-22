package com.sfirsov.kalahgame.game;

import com.sfirsov.kalahgame.model.AvailableGame;
import com.sfirsov.kalahgame.model.GameState;
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

    private Map<Integer, String> players = new HashMap<Integer, String>();
    private List<Game> games = new ArrayList<Game>();
    private Map<Integer, Game> playersInGames = new HashMap<Integer, Game>();
    private Map<Integer, Long> playersLastActivity = new HashMap<Integer, Long>();
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    private void Init() {
        executorService.scheduleAtFixedRate(() -> runGarbageCollector(), 0, 1, TimeUnit.MINUTES);
    }

    // This is to delete inactive players and games
    private void runGarbageCollector() {
        // Get list of players inactive for 5 minutes
        List<Integer> afkPlayers = playersLastActivity.entrySet().stream().filter(entry -> {
            return ((new Date()).getTime() - entry.getValue()) > 1000 * 60 * 5;
        }).map(entry -> entry.getKey()).collect(Collectors.toList());

        afkPlayers.forEach(player -> {
            if(playersInGames.containsKey(player)) {
                Game game = playersInGames.get(player);
                games.remove(game);
            }

            playersInGames.remove(player);
            players.remove(player);
            playersLastActivity.remove(player);
        });
    }

    private void updatePlayerActivity(Integer playerUid) {
        playersLastActivity.put(playerUid, (new Date()).getTime());
    }

    private int generatePlayerUid(String player) {
        return player.hashCode();
    }

    public int addPlayer(String player) {
        synchronized (players) {
            if (players.containsValue(player)) {
                return -1;
            }

            int playerUid = generatePlayerUid(player);
            players.put(playerUid, player);
            updatePlayerActivity(playerUid);

            return playerUid;
        }
    }

    public boolean deletePlayer(int uid) {
        synchronized (players) {
            if (!players.containsKey(uid)) {
                return false;
            }

            players.remove(uid);
        }
        synchronized (playersInGames) {
            playersInGames.remove(uid);
        }

        return true;
    }

    public GameState addGame(int playerUid) {
        updatePlayerActivity(playerUid);

        synchronized (playersInGames) {
            Game game = new Game();
            if (!players.containsKey(playerUid)) {
                return null;
            }

            if (playersInGames.containsKey(playerUid)) {
                return null;
            }

            String playerName = players.get(playerUid);
            game.setPlayer1(playerName);

            playersInGames.put(playerUid, game);
            games.add(game);

            return game.getGameState(playerName);
        }
    }

    public GameState joinGame(int playerUid, String opponentName) {
        updatePlayerActivity(playerUid);

        synchronized (playersInGames) {
            for (Map.Entry<Integer, String> nameToUid : players.entrySet()) {
                if (opponentName.equals(nameToUid.getValue())) {
                    int opponentUid = nameToUid.getKey();
                    String playerName = players.get(playerUid);
                    Game game = playersInGames.get(opponentUid);
                    game.setPlayer2(playerName);
                    playersInGames.put(playerUid, game);

                    return game.getGameState(playerName);
                }
            }

            return null;
        }
    }

    public GameState move(int playerUid, int pit) {
        updatePlayerActivity(playerUid);

        if(!playersInGames.containsKey(playerUid)) {
            return null;
        }

        Game game = playersInGames.get(playerUid);
        String player = players.get(playerUid);

        synchronized (game) {
            // The game is over, or wrong player's move
            if (game.isGameOver() || !player.equals(game.whosTurn())) {
                return game.getGameState(player);
            }
            else {
                return game.move(player, pit);
            }
        }
    }

    public GameState getGameState(int playerUid) {
        if(!playersInGames.containsKey(playerUid)) {
            return null;
        }

        Game game = playersInGames.get(playerUid);
        synchronized (game) {
            String player = players.get(playerUid);
            return game.getGameState(player);
        }
    }

    public List<AvailableGame> getAvailableGames() {
        synchronized (games) {
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
}
