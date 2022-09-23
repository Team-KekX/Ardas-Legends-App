package com.ardaslegends.data.presentation.discord.utils;

public enum Thumbnails {

    MOVE_CHARACTER("https://cdn.discordapp.com/attachments/993179687956258867/993202523475292290/move_character.jpg");

    private final String url;

    Thumbnails(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
