package com.sfirsov.kalahgame.model;

public class GameState {
    private int     playerKalah;
    private int     opponentKalah;
    private int[]   pits;
    private boolean gameOver;
    private String  whosTurn;
    private boolean started;

    public int getPlayerKalah() {
        return playerKalah;
    }

    public void setPlayerKalah(int playerKalah) {
        this.playerKalah = playerKalah;
    }

    public int getOpponentKalah() {
        return opponentKalah;
    }

    public void setOpponentKalah(int opponentKalah) {
        this.opponentKalah = opponentKalah;
    }

    public int[] getPits() {
        return pits;
    }

    public void setPits(int[] pits) {
        this.pits = pits;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String getWhosTurn() {
        return whosTurn;
    }

    public void setWhosTurn(String whosTurn) {
        this.whosTurn = whosTurn;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
