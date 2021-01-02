package me.pljr.dcmcbot;

import me.pljr.dcmcbot.config.CfgSettings;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JDAReadyListener extends ListenerAdapter {
    private final DCMCBot dcmcBot;

    public JDAReadyListener(DCMCBot dcmcBot){
        this.dcmcBot = dcmcBot;
    }

    public void onReady(@NotNull ReadyEvent event){
        dcmcBot.setGuild(CfgSettings.guildId);
        dcmcBot.setupConfigs();
        dcmcBot.setupDatabase();
        dcmcBot.setupManagers();
        dcmcBot.setupCommands();
        dcmcBot.setupListeners();
    }
}
