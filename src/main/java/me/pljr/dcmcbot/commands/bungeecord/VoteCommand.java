package me.pljr.dcmcbot.commands.bungeecord;

import me.pljr.pljrapibungee.commands.BungeeCommand;
import me.pljr.pljrapibungee.config.ConfigManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;

public class VoteCommand extends BungeeCommand {
    private final static String URL_1 = "https://craftlist.org/diamondcrew-cz?user=";
    private final static String URL_2 = "https://czech-craft.eu/server/diamondcrew/vote/?user=";

    private final List<String> response;

    public VoteCommand(ConfigManager config){
        super("vote");
        response = config.getStringList("vote.response");
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer player, String[] args) {
        String playerName = player.getName();
        response.forEach(line -> sendMessage(player, line
                .replace("{url1}", URL_1+playerName)
                .replace("{url2}", URL_2+playerName)));
    }
}
