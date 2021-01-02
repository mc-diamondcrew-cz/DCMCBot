package me.pljr.dcmcbot;

import me.pljr.dcmcbot.commands.bungeecord.DiscordCommand;
import me.pljr.dcmcbot.commands.bungeecord.TicketCommand;
import me.pljr.dcmcbot.commands.discord.ClearCommand;
import me.pljr.dcmcbot.commands.discord.LinkAccountCommand;
import me.pljr.dcmcbot.commands.discord.SayCommand;
import me.pljr.dcmcbot.config.CfgSettings;
import me.pljr.dcmcbot.config.CfgTickets;
import me.pljr.dcmcbot.listeners.discord.GuildJoinListener;
import me.pljr.dcmcbot.listeners.discord.GuildLeaveListener;
import me.pljr.dcmcbot.managers.*;
import me.pljr.dcmcbot.objects.ReactForRole;
import me.pljr.dcmcbot.objects.SimpleCommand;
import me.pljr.pljrapibungee.database.DataSource;
import me.pljr.pljrapibungee.managers.ConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class DCMCBot extends Plugin {
    private static DCMCBot instance;
    private static JDA jda;
    private static Guild guild;

    private static ConfigManager configManager;
    private static QueryManager queryManager;

    private static DCAccountManager dcAccountManager;
    private static StatsManager statsManager;
    private static TicketManager ticketManager;
    private static DCRoleManager rolesManager;
    private static SimpleCommandManager simpleCommandManager;
    private static ReactForRoleManager reactForRoleManager;

    @Override
    public void onEnable(){
        instance = this;
        if (!setupMainConfig()) return;
        if (!setupJDA()) return;
    }

    public boolean setupMainConfig() {
        if (!getDataFolder().exists()) getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            configManager = new ConfigManager(configuration, "§cDCMCBot:", "config.yml");
            CfgSettings.load(configManager);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setupConfigs(){
        CfgTickets.load(configManager);
    }

    public boolean setupJDA(){
        try {
            jda = JDABuilder.createDefault(CfgSettings.token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableCache(CacheFlag.EMOTE)
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
            return false;
        }
        jda.getPresence().setActivity(Activity.playing(CfgSettings.activity));
        jda.addEventListener(new JDAReadyListener(this));
        return true;
    }

    public void setupDatabase(){
        queryManager = new QueryManager(DataSource.getFromConfig(configManager), guild);
    }

    public void setupManagers(){
        dcAccountManager = new DCAccountManager(guild);
        statsManager = new StatsManager(guild, getProxy());
        ticketManager = new TicketManager();
        rolesManager = new DCRoleManager(configManager, instance, guild);
        simpleCommandManager = new SimpleCommandManager(configManager);
        reactForRoleManager = new ReactForRoleManager(configManager, guild);
    }

    public void setupCommands(){
        // Bungee
        new DiscordCommand().registerCommand(this);
        new TicketCommand().registerCommand(this);

        // Discord
        new ClearCommand("clear", guild.getRoleById(CfgSettings.adminRole)).registerCommand(jda);
        new LinkAccountCommand("linkaccount").registerCommand(jda);
        new SayCommand("say", guild.getRoleById(CfgSettings.adminRole)).registerCommand(jda);

        for (SimpleCommand simpleCommand : simpleCommandManager.getCommands()){
            simpleCommand.registerCommand(jda);
        }
    }

    public void setupListeners(){
        // Bungee

        // Discord
        jda.addEventListener(ticketManager);
        jda.addEventListener(new GuildJoinListener());
        jda.addEventListener(new GuildLeaveListener());
        for (ReactForRole react : reactForRoleManager.getReacts()){
            jda.addEventListener(react);
        }
    }

    public static DCAccountManager getDcAccountManager() {
        return dcAccountManager;
    }

    public void setGuild(long guild) {
        DCMCBot.guild = jda.getGuildById(guild);
    }

    public static Guild getGuild() {
        return guild;
    }

    public static StatsManager getStatsManager() {
        return statsManager;
    }

    public static TicketManager getTicketManager() {
        return ticketManager;
    }

    public static DCRoleManager getRolesManager() {
        return rolesManager;
    }

    public static QueryManager getQueryManager() {
        return queryManager;
    }

    public static ReactForRoleManager getReactForRoleManager() {
        return reactForRoleManager;
    }
}
