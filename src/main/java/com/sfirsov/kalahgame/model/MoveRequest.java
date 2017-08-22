package com.sfirsov.kalahgame.model;

public class MoveRequest {
    private int playerUid;
    private int pit;

    public int getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(int playerUid) {
        this.playerUid = playerUid;
    }

    public int getPit() {
        return pit;
    }

    public void setPit(int pit) {
        this.pit = pit;
    }
}
