package com.ardaslegends.presentation.discord.exception;

import lombok.Getter;

@Getter

public class BotException extends RuntimeException{

    private final String title;

    public BotException(String title, Throwable root) {
        super(root.getMessage(), root);
        this.title = title;
    }
}
