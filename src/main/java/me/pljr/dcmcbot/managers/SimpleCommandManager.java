package me.pljr.dcmcbot.managers;

import me.pljr.dcmcbot.objects.SimpleCommand;
import me.pljr.dcmcbot.utils.EmbedUtils;
import me.pljr.pljrapibungee.config.ConfigManager;
import net.md_5.bungee.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class SimpleCommandManager {
    private final List<SimpleCommand> commands;

    public SimpleCommandManager(ConfigManager config){
        this.commands = new ArrayList<>();
        Configuration configuration = config.getConfiguration().getSection("simple-commands");
        if (configuration != null){
            for (String command : configuration.getKeys()){
                SimpleCommand simpleCommand = new SimpleCommand(command, EmbedUtils.getEmbedFromConfig(configuration.getSection(command+".embed")), config.getStringList("simple-commands."+command+".aliases"));
                commands.add(simpleCommand);
            }
        }
    }

    public List<SimpleCommand> getCommands() {
        return commands;
    }
}
