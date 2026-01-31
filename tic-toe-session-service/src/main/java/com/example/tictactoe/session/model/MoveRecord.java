package com.example.tictactoe.session.model;

public class MoveRecord {
    private String player;
    private int row;
    private int column;

    public MoveRecord() {
    }

    public MoveRecord(String player, int row, int column) {
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

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
