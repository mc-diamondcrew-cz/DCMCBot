package me.pljr.dcmcbot.objects;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactForRole extends ListenerAdapter {
    private final Role role;
    private final TextChannel channel;
    private final MessageReaction.ReactionEmote emote;

    public ReactForRole(Role role, TextChannel channel, MessageReaction.ReactionEmote emote){
        this.role = role;
        this.channel = channel;
        this.emote = emote;
    }

    @Override
    public void onGenericMessageReaction(GenericMessageReactionEvent event){
        if (event.getMember() == null) return;
        Member member = event.getMember();
        if (member.getUser().isBot()) return;

        TextChannel channel = event.getTextChannel();
        if (this.channel != channel) return;

        MessageReaction.ReactionEmote emote = event.getReactionEmote();
        if (this.emote != emote) return;

        if (event instanceof MessageReactionAddEvent){
            member.getGuild().addRoleToMember(member, this.role).queue();
        }else if (event instanceof MessageReactionRemoveEvent){
            member.getGuild().removeRoleFromMember(member, this.role).queue();
        }
    }
}
