package com.example.tictactoe.session.client.dto;

public class EngineMoveRequest {
    private String player;
    private Integer row;
    private Integer column;

    public EngineMoveRequest() {
    }

    public EngineMoveRequest(String player, Integer row, Integer column) {
        this.player = player;
        this.row = row;
        this.column = column;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }
}
