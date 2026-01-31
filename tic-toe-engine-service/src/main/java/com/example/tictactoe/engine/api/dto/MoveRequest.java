package com.example.tictactoe.engine.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for move request.
 */
public class MoveRequest {
    @NotBlank(message = "Player symbol is required")
    private String player;

    @NotNull(message = "Row is required")
    @Min(value = 0, message = "Row must be between 0 and 2")
    @Max(value = 2, message = "Row must be between 0 and 2")
    private Integer row;

    @NotNull(message = "Column is required")
    @Min(value = 0, message = "Column must be between 0 and 2")
    @Max(value = 2, message = "Column must be between 0 and 2")
    private Integer column;

    public MoveRequest() {
    }

    public MoveRequest(String player, Integer row, Integer column) {
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

    public char getPlayerSymbol() {
        if (player == null || player.isEmpty()) {
            return ' ';
        }
        return player.toUpperCase().charAt(0);
    }
}
