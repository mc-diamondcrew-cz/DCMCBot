package me.pljr.dcmcbot.listeners.discord;

import me.pljr.dcmcbot.DCMCBot;
import me.pljr.dcmcbot.managers.StatsManager;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildJoinListener extends ListenerAdapter {

    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        StatsManager statsManager = DCMCBot.getStatsManager();
        statsManager.updateStats();
        statsManager.updateChannels();
    }
}
