package me.pljr.dcmcbot.managers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.md_5.bungee.api.ProxyServer;

public class StatsManager {
    private final Guild guild;
    private final VoiceChannel membersChannel;
    private int members;

    private final ProxyServer proxyServer;
    private final VoiceChannel serverPlayersChannel;
    private int serverPlayers;

    public StatsManager(Guild guild, ProxyServer proxyServer){
        this.guild = guild;
        this.proxyServer = proxyServer;
        membersChannel = this.guild
                .getVoiceChannelById("752803950528299019");
        serverPlayersChannel = this.guild.getVoiceChannelById("775146910545739807");
        updateStats();
        updateChannels();
    }

    public void updateStats(){
        members = guild.getMemberCount();
        serverPlayers = proxyServer.getPlayers().size();
    }

    public void updateChannels(){
        membersChannel.getManager().setName("\uD83D\uDCAA  | " + members).queue();
        serverPlayersChannel.getManager().setName("\uD83C\uDFAE | " + serverPlayers).queue();
    }
}
