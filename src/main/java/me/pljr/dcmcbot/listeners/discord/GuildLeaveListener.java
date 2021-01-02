package me.pljr.dcmcbot.listeners.discord;

import me.pljr.dcmcbot.DCMCBot;
import me.pljr.dcmcbot.managers.StatsManager;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class GuildLeaveListener extends ListenerAdapter {

    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        StatsManager statsManager = DCMCBot.getStatsManager();
        statsManager.updateStats();
        statsManager.updateChannels();
    }
}
