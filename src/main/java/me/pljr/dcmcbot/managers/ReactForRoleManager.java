package me.pljr.dcmcbot.managers;

import me.pljr.dcmcbot.objects.ReactForRole;
import me.pljr.pljrapibungee.managers.ConfigManager;
import net.dv8tion.jda.api.entities.*;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class ReactForRoleManager {
    private List<ReactForRole> reacts;

    public ReactForRoleManager(ConfigManager config, Guild guild){
        this.reacts = new ArrayList<>();
        Configuration configuration = config.getConfiguration().getSection("react");
        if (configuration != null){
            for (String react : configuration.getKeys()){
                TextChannel channel = guild.getTextChannelById(config.getLong("react."+react+".channel"));
                MessageReaction.ReactionEmote emote = MessageReaction.ReactionEmote.fromUnicode(config.getString("react."+react+".emote"), guild.getJDA());
                Role role = guild.getRoleById(config.getLong("react."+react+".role"));
                reacts.add(new ReactForRole(role, channel, emote));
            }
        }
    }

    public List<ReactForRole> getReacts() {
        return reacts;
    }
}
