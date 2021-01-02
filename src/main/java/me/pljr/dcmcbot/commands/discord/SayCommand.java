package me.pljr.dcmcbot.commands.discord;

import me.pljr.dcmcbot.utils.DiscordCommandUtil;
import me.pljr.pljrapibungee.utils.FormatUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SayCommand extends DiscordCommandUtil {

    public SayCommand(String command, Role role){
        super(command, role);
    }

    @Override
    public void onCommand(Command command) {
        String[] args = command.getMessage().split("///");

        String hex = args[1];
        Color color = Color.decode(hex);

        String title = args[2];

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(color);
        //embed.setAuthor(title);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        LocalDateTime now = LocalDateTime.now();

        embed.setFooter("DCMC ▪ " + dtf.format(now), "https://diamondcrew.cz/img/logo.png");

        embed.addField(title, args[3], false);
        if (args[3].length() > 1024){
            sendErrorEmbed(command.getChannel(), "Zpráva nemůže být delší, než 1024 charakterů!");
        }

        command.getChannel().sendMessage(embed.build()).queue();
        embed.clear();
    }
}
