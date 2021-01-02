package me.pljr.dcmcbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DiscordCommandUtil extends ListenerAdapter {
    private String command;
    private List<String> aliases;
    private Role role;

    public DiscordCommandUtil(String command, List<String> aliases, Role role){
        this.command = command;
        this.aliases = aliases;
        this.role = role;
    }
    public DiscordCommandUtil(String command, Role role, String... aliases){
        this(command, Arrays.asList(aliases), role);
    }
    public DiscordCommandUtil(String command, String... aliases){
        this(command, Arrays.asList(aliases), null);
    }
    public DiscordCommandUtil(String command, List<String> aliases){
        this(command, aliases, null);
    }
    public DiscordCommandUtil(String command){
        this(command, new ArrayList<>(), null);
    }

    public void registerCommand(JDA jda){
        jda.addEventListener(this);
    }

    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        String message = event.getMessage().getContentRaw();
        if (message.startsWith("!")){
            String[] args = getArgs(message);
            String cmd = args[0];
            boolean isCmd = false;
            if (!cmd.equalsIgnoreCase(command)){
                for (String alias : aliases){
                    if (cmd.equalsIgnoreCase(alias)){
                        isCmd = true;
                        break;
                    }
                }
            }else isCmd = true;
            if (!isCmd) return;
            Member member = event.getMember();
            if (role != null && !member.getRoles().contains(role)) return;
            Command command = new Command(member, event.getChannel(), cmd, Arrays.copyOfRange(args, 1, args.length), message.substring(1));
            onCommand(command);
        }
    }

    public void onCommand(Command command){}

    public String[] getArgs(String message){
        return message.substring(1).split("\\s+");
    }

    public void sendErrorEmbed(TextChannel channel, String errorMessage){
        EmbedBuilder embed = new EmbedBuilder();

        embed.setColor(Color.RED);
        embed.setTitle("DCMC | Chyba");
        embed.setDescription(errorMessage);

        channel.sendMessage(embed.build()).queue();
        embed.clear();
    }

    public void sendSuccessEmbed(TextChannel channel, String successMessage){
        EmbedBuilder embed = new EmbedBuilder();

        embed.setColor(Color.GREEN);
        embed.setTitle("DCMC | Úspěch");
        embed.setDescription(successMessage);

        channel.sendMessage(embed.build()).queue();
        embed.clear();
    }

    public class Command {
        private final Member member;
        private final TextChannel channel;
        private final String command;
        private final String[] args;
        private final String message;

        public Command(Member member, TextChannel channel, String command, String[] args, String message){
            this.member = member;
            this.channel = channel;
            this.command = command;
            this.args = args;
            this.message = message;
        }

        public Member getMember() {
            return member;
        }

        public TextChannel getChannel() {
            return channel;
        }

        public String getCommand() {
            return command;
        }

        public String[] getArgs() {
            return args;
        }

        public String getMessage() {
            return message;
        }
    }
}

