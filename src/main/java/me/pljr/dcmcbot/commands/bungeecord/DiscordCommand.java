package me.pljr.dcmcbot.commands.bungeecord;

import me.pljr.dcmcbot.DCMCBot;
import me.pljr.dcmcbot.managers.DCAccountManager;
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

public class DiscordCommand extends BungeeCommand {

    public DiscordCommand(){
        super("discord");
    }

    @Override
    public void execute(CommandSender sender, String[] args){
        if (!(sender instanceof ProxiedPlayer)){
            sender.sendMessage("Tento příkaz je pouze pro hráče.");
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        UUID playerId = player.getUniqueId();

        DCAccountManager manager = DCMCBot.getDcAccountManager();
        String code = manager.generateCode(playerId);

        BaseComponent[] header = new ComponentBuilder()
                .append("§8╔════════ §aDISCORD §8════════╗")
                .create();

        BaseComponent[] join = new ComponentBuilder()
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§dKlikni pro otevření.")))
                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://mc.diamondcrew.cz/discord"))
                .append("§8║" + "\n")
                .append("§8╠═§d§lPřipojení" + "\n")
                .append("§8║ §fmc.diamondcrew.cz/discord")
                .create();

        BaseComponent[] account;
        if (manager.getAccount(playerId) != null) {
            Member member = manager.getAccount(playerId);

            account = new ComponentBuilder()
                    .append("§8║" + "\n")
                    .append("§8╠═§a§lÚčet" + "\n")
                    .append("§8║ §f" + member.getUser().getName())
                    .create();
        }else{
            account = new ComponentBuilder()
                    .append("§8║" + "\n")
                    .append("§8╠═§c§lÚčet" + "\n")
                    .append("§8║ §cNemáte propojený účet!")
                    .create();
        }

        BaseComponent[] ticket = new ComponentBuilder()
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§eKlikni pro vytvoření.")))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ticket"))
                .append("§8║" + "\n")
                .append("§8╠═§e§lVytvoření ticketu" + "\n")
                .append("§8║ §f/ticket")
                .create();

        BaseComponent[] connect = new ComponentBuilder()
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§bKlikni pro zkopírování.")))
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "!linkaccount " + code))
                .append("§8║" + "\n")
                .append("§8╠═§b§lPříkaz pro propojení účtu" + "\n")
                .append("§8║ §f!linkaccount " + code + " §7(Najeďte myškou)\n")
                .append("§8╠═§bVložte do Discord místnosti #příkazy")
                .create();

        BaseComponent[] footer = new ComponentBuilder()
                .append("§8║" + "\n")
                .append("§8╚════════ §aDISCORD §8════════╝")
                .create();

        player.sendMessage(header);
        player.sendMessage(join);
        player.sendMessage(account);
        player.sendMessage(ticket);
        player.sendMessage(connect);
        player.sendMessage(footer);
    }
}
