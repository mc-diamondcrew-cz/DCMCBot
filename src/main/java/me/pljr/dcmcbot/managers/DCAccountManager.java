package me.pljr.dcmcbot.managers;

import me.pljr.dcmcbot.DCMCBot;
import me.pljr.dcmcbot.objects.DCRole;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.*;

public class DCAccountManager {
    private final Guild guild;
    private final List<String> inUseCodes;
    private final HashMap<UUID, String> waitingCodes;
    private final HashMap<UUID, Long> accounts;

    public DCAccountManager(Guild guild){
        this.guild = guild;
        inUseCodes = new ArrayList<>();
        waitingCodes = new HashMap<>();
        accounts = DCMCBot.getQueryManager().loadAccounts();
    }

    public void setRank(UUID uuid, DCRole dcRole, boolean shouldHave){
        Member member = getAccount(uuid);
        if (member == null) return;
        Role role = guild.getRoleById(dcRole.getRoleId());
        if (shouldHave){
            guild.addRoleToMember(member, role).queue();
        }else{
            guild.removeRoleFromMember(member, role).queue();
        }
    }

    public boolean connect(UUID uuid, long id, String code){
        if (!waitingCodes.containsKey(uuid) || !waitingCodes.get(uuid).contains(code)) return false;
        inUseCodes.remove(code);
        waitingCodes.remove(uuid);
        addAccount(uuid, id);
        return true;
    }

    public String generateCode(UUID uuid){
        if (waitingCodes.containsKey(uuid)){
            return waitingCodes.get(uuid);
        }
        Random random = new Random();

        String code = random.ints(48, 122 + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(16)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        if (inUseCodes.contains(code)){
            code = generateCode(uuid);
        }
        inUseCodes.add(code);
        waitingCodes.put(uuid, code);
        return code;
    }

    public void addAccount(UUID uuid, long id){
        accounts.put(uuid, id);
    }

    public Member getAccount(UUID uuid){
        if (!accounts.containsKey(uuid)) return null;
        long id = accounts.get(uuid);
        Member member = guild.getMemberById(id);
        if (member == null){
            accounts.remove(uuid);
            return null;
        }
        return member;
    }

    public List<String> getInUseCodes() {
        return inUseCodes;
    }

    public UUID getIdFromCode(String code) {
        for (Map.Entry<UUID, String> entry : waitingCodes.entrySet()){
            if (entry.getValue().equals(code)){
                return entry.getKey();
            }
        }
        return null;
    }

    public HashMap<UUID, Long> getAccounts() {
        return accounts;
    }
}
