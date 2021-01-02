package me.pljr.dcmcbot.objects;

import me.pljr.dcmcbot.utils.DiscordCommandUtil;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.List;

public class SimpleCommand extends DiscordCommandUtil {
    private final EmbedBuilder embed;

    public SimpleCommand(String command, EmbedBuilder embed, List<String> aliases){
        super(command, aliases);
        this.embed = embed;
    }

    @Override
    public void onCommand(Command command){
        command.getChannel().sendMessage(embed.build()).queue();
    }
}
