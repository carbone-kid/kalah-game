package com.sfirsov.kalahgame.model;

public class MoveRequest {
    private long playerId;
    private int pit;

    public long getPlayerUid() {
        return playerId;
    }

    public void setPlayerUid(long playerUid) {
        this.playerId = playerUid;
    }

    public int getPit() {
        return pit;
    }

    public void setPit(int pit) {
        this.pit = pit;
    }
}
