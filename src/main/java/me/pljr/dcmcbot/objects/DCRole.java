package me.pljr.dcmcbot.objects;

public class DCRole {
    private final String name;
    private final String perm;
    private final long roleId;

    public DCRole(String name, String perm, long roleId){
        this.name = name;
        this.perm = perm;
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public String getPerm() {
        return perm;
    }

    public long getRoleId() {
        return roleId;
    }
}
