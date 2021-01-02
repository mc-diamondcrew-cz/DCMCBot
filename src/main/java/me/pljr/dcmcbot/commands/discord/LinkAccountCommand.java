package me.pljr.dcmcbot.commands.discord;

import me.pljr.dcmcbot.DCMCBot;
import me.pljr.dcmcbot.managers.DCAccountManager;
import me.pljr.dcmcbot.utils.DiscordCommandUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.UUID;

public class LinkAccountCommand extends DiscordCommandUtil {

    public LinkAccountCommand(String command){
        super(command);
    }

    @Override
    public void onCommand(Command command) {
        String[] args = command.getArgs();
        TextChannel channel = command.getChannel();
        Member member = command.getMember();

        if (args.length != 1) {
            sendErrorEmbed(channel, "> Použití: `!linkaccount <kód>`");
            return;
        }
        String code = args[0];

        DCAccountManager manager = DCMCBot.getDcAccountManager();
        if (!manager.getInUseCodes().contains(code)) {
            sendErrorEmbed(channel, "> Kód nebyl nalezen.");
            return;
        }

        UUID uuid = manager.getIdFromCode(code);
        if (uuid == null) {
            sendErrorEmbed(channel, "> Při ověřování nastala chyba. Kontaktuje administrátora.");
            return;
        }
        manager.connect(uuid, member.getIdLong(), code);
        sendSuccessEmbed(channel, "> Účet úspěšně propojen.");
    }
}
