package com.ardaslegends.data.presentation.discord.utils;

public enum Thumbnails {

    MOVE_CHARACTER("https://cdn.discordapp.com/attachments/993179687956258867/993202523475292290/move_character.jpg"),
    INJURE_CHARACTER("https://cdn.discordapp.com/attachments/1022950091214041108/1022951678598721597/unknown.png");

    private final String url;

    Thumbnails(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
