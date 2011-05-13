package com.Cutch.bukkit.ICmds;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class PermissionSupport {
    ItemCommands plugin = null;
    String Version = "";
    private PermissionHandler Permissions=null;
    private HookPermissionHandler hookPermissionHandler=null;
    int permissionsType = 0;
    public PermissionSupport(ItemCommands instance)
    {
        this.plugin = instance;
    }
    public int setupPermissions(int permissionsType) {
        Plugin test = plugin.getServer().getPluginManager().getPlugin("Permissions");
        if (this.Permissions == null) {
            if(test != null) {
                Permissions p = (Permissions)test;
                Version = p.getDescription().getVersion();
                p.Security = hookPermissionHandler = new HookPermissionHandler(p) {};
                this.Permissions = p.getHandler();
                if(permissionsType == 0)
                    System.out.println("ItemCommands: Using Permissions Plugin v" + Version);
                else
                    System.out.println("ItemCommands: Using Basic Permissions");
            } 
            else if(permissionsType == 1)
                System.out.println("ItemCommands: Using Basic Permissions");
            else {
                permissionsType=1;
                System.out.println("ItemCommands: Permission system not detected. Using Basic Permissions.");
            }
        }
        return this.permissionsType = permissionsType;
    }
    public int returnPermissionType(){
        return permissionsType; 
    }
    public boolean has(Player name, String node)
    {
        return this.Permissions.has(name, node);
    }
    public void addSuperAccess(String name)
    {
        if(hookPermissionHandler != null)
            hookPermissionHandler.addSuperAccess(name);
    }
    public void removeSuperAccess(String name)
    {
        if(hookPermissionHandler != null)
            hookPermissionHandler.removeSuperAccess(name);
    }
}
