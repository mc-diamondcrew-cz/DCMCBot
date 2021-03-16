package me.pljr.dcmcbot.commands.bungeecord;

import me.pljr.pljrapibungee.commands.BungeeCommand;
import me.pljr.pljrapibungee.config.ConfigManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BuyCommand extends BungeeCommand {

    private final String message;

    public BuyCommand(ConfigManager config){
        super("buy");
        message = config.getString("buy");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer player, String[] args) {
        sendMessage(player, message);
    }

    @Override
    public void onConsoleCommand(CommandSender sender, String[] args) {
        sendMessage(sender, message);
    }
}
