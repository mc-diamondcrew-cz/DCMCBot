package me.pljr.dcmcbot.listeners.bungeecord;

import me.pljr.dcmcbot.managers.StatsManager;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PreLoginListener implements Listener {

    private final StatsManager statsManager;

    public PreLoginListener(StatsManager statsManager){
        this.statsManager = statsManager;
    }

    @EventHandler
    public void onLogin(PostLoginEvent event){
        statsManager.updateStats();
        statsManager.updateChannels();
    }
}
