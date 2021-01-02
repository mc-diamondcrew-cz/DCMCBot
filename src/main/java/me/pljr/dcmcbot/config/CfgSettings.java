package me.pljr.dcmcbot.config;

import me.pljr.pljrapibungee.managers.ConfigManager;

public class CfgSettings {
    public static String token;
    public static String activity;
    public static long guildId;
    public static long adminRole;

    public static void load(ConfigManager config){
        token = config.getString("settings.token");
        activity = config.getString("settings.activity");
        guildId = config.getLong("settings.guild-id");
        adminRole = config.getLong("settings.admin-role");
    }
}
