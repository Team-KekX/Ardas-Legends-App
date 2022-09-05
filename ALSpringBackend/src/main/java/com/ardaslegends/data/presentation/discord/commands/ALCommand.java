package com.ardaslegends.data.presentation.discord.commands;

import java.util.Map;

public interface ALCommand {

    public void init(Map<String, ALCommandExecutor> commands);
}
