package me.pljr.dcmcbot.objects;

import me.pljr.dcmcbot.DCMCBot;
import me.pljr.dcmcbot.config.CfgTickets;
import me.pljr.pljrapibungee.utils.ChatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

public class Ticket {
    private final Guild guild;
    private final Member member;
    private long channelId;
    private Invite invite;

    public Ticket(Member member, long channelId){
        this.guild = DCMCBot.getGuild();
        this.member = member;
        this.channelId = channelId;
        this.invite = guild.getTextChannelById(channelId).createInvite().setMaxAge(1L, TimeUnit.HOURS).complete();
    }

    public Ticket(Member member){
        this.guild = DCMCBot.getGuild();
        this.member = member;
        guild.createTextChannel("t-"+member.getUser().getName(), CfgTickets.openCategory).addPermissionOverride(guild.getPublicRole(), null,  EnumSet.of(Permission.MESSAGE_READ))
                .addMemberPermissionOverride(member.getIdLong(), EnumSet.of(Permission.MESSAGE_READ), null).queue(channel -> {
                    channelId = channel.getIdLong();
                    channel = guild.getTextChannelById(channelId);
                    channel.sendMessage(member.getAsMention()).queue();

                    EmbedBuilder embed = new EmbedBuilder(CfgTickets.openEmbed);

                    TextChannel finalChannel = channel;
                    channel.sendMessage(embed.build()).queue(message -> {
                        finalChannel.addReactionById(finalChannel.getLatestMessageId(), "\uD83D\uDD12").queue();
                    });
                    embed.clear();
                    DCMCBot.getTicketManager().created(this);
            channel.createInvite().setMaxAge(1L, TimeUnit.HOURS).queue(createdInvite -> {
                invite = createdInvite;
            });
        });
    }

    /*

    In-Game creation

     */
    public Ticket(Member member, ProxiedPlayer player){
        this.guild = DCMCBot.getGuild();
        this.member = member;
        guild.createTextChannel("t-"+member.getUser().getName(), CfgTickets.openCategory).addPermissionOverride(guild.getPublicRole(), null,  EnumSet.of(Permission.MESSAGE_READ))
                .addMemberPermissionOverride(member.getIdLong(), EnumSet.of(Permission.MESSAGE_READ), null).queue(channel -> {
            channelId = channel.getIdLong();
            channel = guild.getTextChannelById(channelId);
            channel.sendMessage(member.getAsMention()).queue();

            EmbedBuilder embed = new EmbedBuilder(CfgTickets.openEmbed);

            TextChannel finalChannel = channel;
            channel.sendMessage(embed.build()).queue(message -> {
                finalChannel.addReactionById(finalChannel.getLatestMessageId(), "\uD83D\uDD12").queue();
            });
            embed.clear();
            DCMCBot.getTicketManager().created(this);

            channel.createInvite().setMaxAge(1L, TimeUnit.HOURS).queue(createdInvite -> {
                invite = createdInvite;

                String userName = member.getUser().getName();
                String url = invite.getUrl();
                for (String line : CfgTickets.openMessage){
                    ChatUtil.sendMessage(player, line
                            .replace("{userName}", userName)
                            .replace("{url}", url));
                }
            });
        });
    }

    public Invite getInvite(){
        return invite;
    }

    public void close(){
        TextChannel channel = guild.getTextChannelById(channelId);
        PermissionOverride manager = channel.getPermissionOverride(member).getManager().complete();
        manager.delete().complete();
        channel.getManager().setParent(CfgTickets.closedCategory).complete();
    }

    public long getChannelId() {
        return channelId;
    }

    public Member getMember() {
        return member;
    }
}
