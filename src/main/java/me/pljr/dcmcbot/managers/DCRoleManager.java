package me.pljr.dcmcbot.managers;

import me.pljr.dcmcbot.DCMCBot;
import me.pljr.dcmcbot.objects.DCRole;
import me.pljr.pljrapibungee.config.ConfigManager;
import net.dv8tion.jda.api.entities.Guild;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class DCRoleManager implements Listener {
    private final Guild guild;

    private List<DCRole> roles;

    public DCRoleManager(ConfigManager config, Plugin plugin, Guild guild){
        this.guild = guild;
        this.roles = new ArrayList<>();
        Configuration configuration = config.getConfiguration().getSection("roles");
        if (configuration != null){
            for (String roleName : configuration.getKeys()){
                DCRole role = new DCRole(roleName, config.getString("roles."+roleName+".perm"), config.getLong("roles."+roleName+".role"));
                roles.add(role);
            }
        }

        PluginManager pluginManager = plugin.getProxy().getPluginManager();
        pluginManager.registerListener(plugin, this);
    }

    public List<DCRole> getRoles() {
        return roles;
    }

    @EventHandler
    public void onBungeeJoin(PostLoginEvent event){
        ProxiedPlayer player = event.getPlayer();
        for (DCRole role : getRoles()){
            DCMCBot.getDcAccountManager().setRank(player.getUniqueId(), role, player.hasPermission(role.getPerm()));
        }
    }
}
