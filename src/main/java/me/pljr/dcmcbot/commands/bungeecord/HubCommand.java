package me.pljr.dcmcbot.commands.bungeecord;

import me.pljr.pljrapibungee.commands.BungeeCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class HubCommand extends BungeeCommand {

    private final ProxyServer proxyServer;

    public HubCommand(ProxyServer proxyServer){
        super("hub");
        this.proxyServer = proxyServer;
    }

    @Override
    public void onPlayerCommand(ProxiedPlayer player, String[] args) {
        player.connect(proxyServer.getServerInfo("lobby1"));
    }
}
