package me.pljr.dcmcbot.config;

import me.pljr.dcmcbot.DCMCBot;
import me.pljr.dcmcbot.utils.EmbedUtils;
import me.pljr.pljrapibungee.config.ConfigManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class CfgTickets {
    public static Category openCategory;
    public static Category closedCategory;
    public static TextChannel createChannel;
    public static String createReaction;
    public static EmbedBuilder openEmbed;
    public static List<String> openMessage;
    public static MessageReaction.ReactionEmote closeEmote;

    public static void load(ConfigManager config){
        Guild guild = DCMCBot.getGuild();
        openCategory = guild.getCategoryById(config.getLong("tickets.open-category"));
        closedCategory = guild.getCategoryById(config.getLong("tickets.closed-category"));
        createChannel = guild.getTextChannelById(config.getLong("tickets.create-channel"));
        createReaction = config.getString("tickets.create-reaction");
        openEmbed = EmbedUtils.getEmbedFromConfig(config.getConfiguration().getSection("tickets.open-embed"));
        openMessage = config.getStringList("tickets.open-message");
        closeEmote = MessageReaction.ReactionEmote.fromUnicode(config.getString("tickets.close-emote"), guild.getJDA());
    }
}
