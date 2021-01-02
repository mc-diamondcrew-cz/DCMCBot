package me.pljr.dcmcbot.commands.discord;

import me.pljr.dcmcbot.utils.DiscordCommandUtil;
import me.pljr.pljrapibungee.utils.NumberUtil;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class ClearCommand extends DiscordCommandUtil {

    public ClearCommand(String command, Role role){
        super(command, role);
    }

    @Override
    public void onCommand(Command command){
        String[] args = command.getArgs();
        TextChannel channel = command.getChannel();

        if (args.length != 1){
            sendErrorEmbed(channel, "> Použití: `!clear <množství>`");
            return;
        }
        if (!NumberUtil.isInt(args[0])){
            sendErrorEmbed(channel, "Množství musí být číslo!");
            return;
        }

        int amount = Integer.parseInt(args[0]);

        if (amount > 100 || amount < 2){
            sendErrorEmbed(channel, "Množství musí být 2-100!");
            return;
        }

        List<Message> messages = channel.getHistory().retrievePast(amount).complete();
        channel.deleteMessages(messages).queue();

        sendSuccessEmbed(channel, "Úspěšně vymazáno " + args[0] + " zpráv.");
    }
}
