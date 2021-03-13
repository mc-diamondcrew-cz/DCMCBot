package me.pljr.dcmcbot.commands.bungeecord;

import me.pljr.dcmcbot.DCMCBot;
import me.pljr.dcmcbot.managers.DCAccountManager;
import me.pljr.dcmcbot.managers.TicketManager;
import me.pljr.dcmcbot.objects.Ticket;
import me.pljr.pljrapibungee.commands.BungeeCommand;
import net.dv8tion.jda.api.entities.Member;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class TicketCommand extends BungeeCommand {

    public TicketCommand(){
        super("ticket");
    }

    @Override
    public void execute(CommandSender sender, String[] args){
        if (!(sender instanceof ProxiedPlayer)){
            sender.sendMessage("Tento příkaz je pouze pro hráče.");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        UUID playerId = player.getUniqueId();

        DCAccountManager accountManager = DCMCBot.getDcAccountManager();

        Member member = accountManager.getAccount(playerId);
        if (member == null) {
            ComponentBuilder error = new ComponentBuilder();
            error.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§fKlikni pro použití příkazu.")));
            error.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "discord"));
            error.append("§cNemáte propojený účet!" + "\n");
            error.append("§cPoužijte příkaz /discord pro propojení účtů.");
            player.sendMessage(error.create());
            return;
        }
        long memberId = member.getIdLong();

        TicketManager ticketManager = DCMCBot.getTicketManager();
        if (!ticketManager.getMembers().contains(member)){
            ticketManager.inGame(member, player);
            return;
        }
        long channelId = ticketManager.getOwners().get(memberId);
        Ticket ticket = ticketManager.getTickets().get(channelId);

        BaseComponent[] header = new ComponentBuilder()
                .append("§8╔════════ §aTICKET §8════════╗")
                .create();

        BaseComponent[] footer = new ComponentBuilder()
                .append("§8║" + "\n")
                .append("§8╚════════ §aTICKET §8════════╝")
                .create();

        BaseComponent[] content = new ComponentBuilder()
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§bKlikni pro otevření.")))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, ticket.getInvite().getUrl()))
                .append("§8║" + "\n")
                .append("§8╠═§b§l" + member.getUser().getName() + "\n")
                .append("§8║ §f" + ticket.getInvite().getUrl())
                .create();

        player.sendMessage(header);
        player.sendMessage(content);
        player.sendMessage(footer);
    }
}
