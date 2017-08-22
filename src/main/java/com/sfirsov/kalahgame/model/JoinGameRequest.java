package com.sfirsov.kalahgame.model;

public class JoinGameRequest {
    int playerUid;
    String opponentName;

    public int getPlayerUid() {
        return playerUid;
    }

    public void setPlayerUid(int playerUid) {
        this.playerUid = playerUid;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }
}
