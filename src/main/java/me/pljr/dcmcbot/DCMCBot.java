package me.pljr.dcmcbot;

import me.pljr.dcmcbot.commands.bungeecord.*;
import me.pljr.dcmcbot.commands.discord.ClearCommand;
import me.pljr.dcmcbot.commands.discord.LinkAccountCommand;
import me.pljr.dcmcbot.commands.discord.SayCommand;
import me.pljr.dcmcbot.config.CfgSettings;
import me.pljr.dcmcbot.config.CfgTickets;
import me.pljr.dcmcbot.listeners.bungeecord.PreLoginListener;
import me.pljr.dcmcbot.listeners.discord.GuildJoinListener;
import me.pljr.dcmcbot.listeners.discord.GuildLeaveListener;
import me.pljr.dcmcbot.managers.*;
import me.pljr.dcmcbot.objects.ReactForRole;
import me.pljr.dcmcbot.objects.SimpleCommand;
import me.pljr.pljrapibungee.PLJRApiBungee;
import me.pljr.pljrapibungee.config.ConfigManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

import javax.security.auth.login.LoginException;

public class DCMCBot extends Plugin {
    private static DCMCBot instance;
    private static JDA jda;
    private static Guild guild;

    private static PLJRApiBungee pljrApiBungee;

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
        if (!setupPLJRApi()) return;
        instance = this;
        setupMainConfig();
        setupJDA();
    }

    public boolean setupPLJRApi(){
        if (PLJRApiBungee.get() == null){
            getLogger().warning("PLJRApi-Bungee is not enabled!");
            return false;
        }
        pljrApiBungee = PLJRApiBungee.get();
        return true;
    }

    public void setupMainConfig() {
        configManager = new ConfigManager(this, "config.yml", true);
        CfgSettings.load(configManager);
    }

    public void setupConfigs(){
        CfgTickets.load(configManager);
    }

    public void setupJDA(){
        try {
            jda = JDABuilder.createDefault(CfgSettings.token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .enableCache(CacheFlag.EMOTE)
                    .build();
        } catch (LoginException e) {
            e.printStackTrace();
            return;
        }
        jda.getPresence().setActivity(Activity.playing(CfgSettings.activity));
        jda.addEventListener(new JDAReadyListener(this));
    }

    public void setupDatabase(){
        queryManager = new QueryManager(pljrApiBungee.getDataSource(configManager), guild);
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
        new VoteCommand(configManager).registerCommand(this);
        new BuyCommand(configManager).registerCommand(this);
        new HubCommand(getProxy()).registerCommand(this);

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
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerListener(this, new PreLoginListener(statsManager));

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
