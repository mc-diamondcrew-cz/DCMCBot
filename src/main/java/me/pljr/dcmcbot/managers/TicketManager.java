package me.pljr.dcmcbot.managers;

import me.pljr.dcmcbot.DCMCBot;
import me.pljr.dcmcbot.config.CfgTickets;
import me.pljr.dcmcbot.objects.Ticket;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketManager extends ListenerAdapter {
    private final HashMap<Long, Ticket> tickets;
    private final HashMap<Long, Long> owners;
    private final List<Member> members;

    public TicketManager(){
        tickets = DCMCBot.getQueryManager().loadTickets();
        owners = new HashMap<>();
        members = new ArrayList<>();
        for (Map.Entry<Long, Ticket> entry : tickets.entrySet()){
            long channelId = entry.getKey();
            Ticket ticket = entry.getValue();
            Member member = ticket.getMember();
            owners.put(member.getIdLong(), channelId);
            members.add(member);
        }
    }

    @Override
    public void onGenericMessageReaction(GenericMessageReactionEvent event){
        if (event.getMember() == null) return;
        Member member = event.getMember();
        if (member.getUser().isBot()) return;

        TextChannel channel = event.getTextChannel();
        long channelId = channel.getIdLong();
        MessageReaction.ReactionEmote emote = event.getReactionEmote();

        if (event instanceof MessageReactionAddEvent) {
            // Closing Ticket and Opening a Ticket
            if (getTickets().containsKey(channelId)) {
                if (emote.equals(CfgTickets.closeEmote)) {
                    close(channelId);
                }
            } else if (channel == CfgTickets.createChannel) {
                open(member);
                event.getReaction().clearReactions().queue();
            }
        }
    }

    public void inGame(Member member, ProxiedPlayer player){
        if (members.contains(member)) return;
        members.add(member);
        new Ticket(member, player);
    }

    public void open(Member member){
        if (members.contains(member)) return;
        members.add(member);
        new Ticket(member);
    }

    public void created(Ticket ticket){
        long channelId = ticket.getChannelId();
        tickets.put(channelId, ticket);
        owners.put(ticket.getMember().getIdLong(), channelId);
        DCMCBot.getQueryManager().updateTickets(tickets);
    }

    public void close(long channelId){
        Ticket ticket = tickets.get(channelId);
        ticket.close();
        Member member = ticket.getMember();
        members.remove(member);
        owners.remove(member.getIdLong());
        tickets.remove(channelId);
        DCMCBot.getQueryManager().updateTickets(tickets);
    }

    public HashMap<Long, Ticket> getTickets() {
        return tickets;
    }

    public List<Member> getMembers() {
        return members;
    }

    public HashMap<Long, Long> getOwners() {
        return owners;
    }
}
