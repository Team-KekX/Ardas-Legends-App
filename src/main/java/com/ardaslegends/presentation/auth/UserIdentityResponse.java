package com.ardaslegends.presentation.auth;


/**
 * This object represents the discord user object
 * Quite a few fields have been left out, due to them not being needed at the start
 * For the full list of available fields visit -> https://discord.com/developers/docs/resources/user#user-object
 */
public record UserIdentityResponse(String id, String username) {

