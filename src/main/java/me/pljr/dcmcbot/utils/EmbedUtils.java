package me.pljr.dcmcbot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.md_5.bungee.config.Configuration;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EmbedUtils {

    public static EmbedBuilder getEmbedFromConfig(Configuration config){
        EmbedBuilder embedBuilder = new EmbedBuilder();

        // Title
        if (config.contains("title")){
            embedBuilder.setTitle(config.getString("title"));
        }
        // Description
        if (config.contains("description")){
            embedBuilder.setDescription(config.getString("description"));
        }
        // Color
        if (config.contains("color")){
            embedBuilder.setColor(Color.decode(config.getString( "color")));
        }else{
            // Default Color
            embedBuilder.setColor(Color.YELLOW);
        }
        // Author
        if (config.contains("author.name")){
            if (config.contains("author.iconUrl")){
                embedBuilder.setAuthor(config.getString("author.name", config.getString("author.url")));
            }else{
                embedBuilder.setAuthor(config.getString("author.name"));
            }
        }
        // Footer
        if (config.contains("footer.name")){
            if (config.contains("footer.iconUrl")){
                embedBuilder.setFooter(config.getString("footer.name", config.getString("footer.iconUrl")));
            }else{
                embedBuilder.setFooter(config.getString("footer.name"));
            }
        }else{
            // Default Footer
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            LocalDateTime now = LocalDateTime.now();
            embedBuilder.setFooter("DCMC ▪ " + dtf.format(now), "https://diamondcrew.cz/img/logo.png");
        }
        // Fields
        if (config.contains("fields")){
            Configuration fields = config.getSection("fields");
            for (String fieldName : fields.getKeys()){
                Configuration field = fields.getSection(fieldName);
                if (field.contains("name") && field.contains("value")){
                    embedBuilder.addField(field.getString("name"), field.getString("value"), false);
                }else{
                    embedBuilder.addBlankField(false);
                }
            }
        }

        return embedBuilder;
    }
}
