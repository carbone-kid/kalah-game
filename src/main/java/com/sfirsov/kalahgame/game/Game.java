package com.sfirsov.kalahgame.game;

import com.sfirsov.kalahgame.model.GameState;

import java.util.ArrayList;
import java.util.List;

// This class represents one game, there could be many games at once.
// One player creates a game, another joins it.
public class Game {
    private static final int PITS_COUNT = 14;
    private static final int KALAH_1 = 6;
    private static final int KALAH_2 = 13;
    private List<Integer> pits;

    private String player1 = "";
    private String player2 = "";
    private String lastMovedPlayer = "";
    private boolean moveAgain = false;
    private boolean started = false;

    public Game() {
        init(6);
    }

    public void init(int stones) {
        pits = new ArrayList<Integer>();
        for(int i=0; i<PITS_COUNT; i++){
            pits.add(stones);
        }

        pits.set(KALAH_1, 0);
        pits.set(KALAH_2, 0);
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public String getPlayer1() {
        return player1;
    }

    public void setPlayer1(String player1) {
        this.player1 = player1;
        if(isPlayer2Joined()) {
            setStarted(true);
        }
    }

    public String getPlayer2() {
        return player2;
    }

    public void setPlayer2(String player2) {
        this.player2 = player2;
        if(isPlayer1Joined()) {
            setStarted(true);
        }
    }

    private boolean isPlayer1Joined() {
        return !"".equals(player1);
    }

    private boolean isPlayer2Joined() {
        return !"".equals(player2);
    }

    private boolean isPlayer1(String name) {
        return name.equals(player1);
    }

    private boolean isPlayer2(String name) {
        return name.equals(player2);
    }


    private int getKalah1() {
        return pits.get(KALAH_1);
    }

    private int getKalah2() {
        return pits.get(KALAH_2);
    }

    private void putToKalah1(int stones) {
        pits.set(KALAH_1, pits.get(KALAH_1) + stones);
    }

    private void putToKalah2(int stones) {
        pits.set(KALAH_2, pits.get(KALAH_2) + stones);
    }

    private int getNextPit(String player, int pit) {
        pit++;

        // Loop pits
        pit = (pit > KALAH_2) ? pit % pits.size() : pit;

        // Bypass opponent's kalah
        if(isPlayer1(player) && pit == KALAH_2) {
            pit = 0;
        }
        else if(isPlayer2(player) && pit == KALAH_1) {
            pit++;
        }

        return pit;
    }

    public GameState getGameState(String player) {
        GameState gameState = new GameState();

        // Turn the board so that the player always be at the bottom of the board,
        // to make the experience closer to the actual board game.
        List<Integer> playablePits = new ArrayList<Integer>();
        if(isPlayer1(player)) {
            gameState.setPlayerKalah(getKalah1());
            gameState.setOpponentKalah(getKalah2());

            playablePits.addAll(this.pits.subList(0, KALAH_1));
            playablePits.addAll(this.pits.subList(KALAH_1 + 1, KALAH_2));
        }
        else {
            gameState.setPlayerKalah(getKalah2());
            gameState.setOpponentKalah(getKalah1());

            playablePits.addAll(this.pits.subList(KALAH_1 + 1, KALAH_2));
            playablePits.addAll(this.pits.subList(0, KALAH_1));
        }
        gameState.setPits(playablePits.stream().mapToInt(stones -> stones).toArray());

        gameState.setGameOver(isGameOver());
        gameState.setWhosTurn(whosTurn());
        gameState.setStarted(isStarted());

        return gameState;
    }

    private boolean isPitBelongToPlayer1(String player, int pit) {
        return pit >= 0 && pit <= 5 && isPlayer1(player);
    }

    private boolean isPitBelongToPlayer2(String player, int pit) {
        return pit >= 7 && pit <= 12 && isPlayer2(player);
    }

    private void captureStonesIfLastPlayerPitWasEmpty(String player, int lastPit) {
        if(isPitBelongToPlayer1(player, lastPit) && pits.get(lastPit) == 1) {
            int capturedStones = pits.get(lastPit) + pits.get(12 - lastPit);
            putToKalah1(capturedStones);
            pits.set(lastPit, 0);
            pits.set(12 - lastPit, 0);
        }
        else if(isPitBelongToPlayer2(player, lastPit) && pits.get(lastPit) == 1) {
            int capturedStones = pits.get(lastPit) + pits.get(12 - lastPit);
            putToKalah2(capturedStones);
            pits.set(lastPit, 0);
            pits.set(12 - lastPit, 0);
        }
    }

    public boolean isGameOver() {
        // Check if all pits of the first players are empty
        if(pits.subList(0, 6).stream().filter(stones -> stones == 0).count() == 6) {
            return true;
        }

        // Check if all pits of the second players are empty
        if(pits.subList(7, 13).stream().filter(stones -> stones == 0).count() == 6) {
            return true;
        }

        return false;
    }

    private void collectAllStones() {
        pits.subList(0, 6).forEach(stones -> putToKalah1(stones));
        pits.subList(7, 13).forEach(stones -> putToKalah2(stones));
        pits.subList(0, 6).replaceAll(stones -> 0);
        pits.subList(7, 13).replaceAll(stones -> 0);
    }

    public GameState move(String player, int pit) {
        if(!whosTurn().equals(player)) {
            return getGameState(player);
        }

        moveAgain = false;

        // Shift index of the first pit in array if the player is second
        if(isPlayer2(player)) {
            pit = pit + 7;
        }

        // Cant make a zero move
        int stonesInHand = pits.get(pit);
        if(stonesInHand == 0) {
            moveAgain = true;
        }

        // Take stones from pit
        pits.set(pit, 0);

        int lastPit = pit;

        // Spread stones to next pits
        while(stonesInHand > 0) {
            lastPit = getNextPit(player, lastPit);
            int stonesInNextPit = pits.get(lastPit);
            pits.set(lastPit, stonesInNextPit + 1);
            stonesInHand--;
        }

        // Check if the last stone put to the player's empty pit or Kalah
        if((isPlayer1(player) && lastPit == KALAH_1) || (isPlayer2(player) && lastPit == KALAH_2)) {
            moveAgain = true;
        }
        else {
            captureStonesIfLastPlayerPitWasEmpty(player, lastPit);
        }

        if(isGameOver()) {
            collectAllStones();
        }

        lastMovedPlayer = player;

        return getGameState(player);
    }

    public String whosTurn() {
        if(moveAgain) {
            return lastMovedPlayer;
        }

        return player1.equals(lastMovedPlayer) ? player2 : player1;
    }
}
