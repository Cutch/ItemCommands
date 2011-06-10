package com.Cutch.bukkit.ICmds;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PermissionSupport {
    ItemCommands plugin = null;
    String Version = "";
    private PermissionHandler Permissions=null;
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
//                if(compareVersions(Version, "3.1.5") >= 0 || compareVersions(Version, "3.0") == -1)
//                {
//                    HookPermissionHandler.PermissionsHandler = p.getHandler();
//                    hookPermissionHandler = new HookPermissionHandler(p) { };
//                }
//                else
//                    System.out.println("ItemCommands: Permissions Super Support is Disabled");
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
        if(this.Permissions != null)
            return this.Permissions.has(name, node);
        return false;
    }
    public void addSuperAccess(String world, String name)
    {
        if(this.Permissions != null)
            this.Permissions.addUserPermission(world, name, "*");
    }
    public void removeSuperAccess(String world, String name)
    {
        if(this.Permissions != null)
            this.Permissions.removeUserPermission(world, name, "*");
    }
    int compareVersions(String versionStr1, String versionStr2)
    {
        String[] split1 = versionStr1.split("\\.");
        Integer[] v1 = new Integer[split1.length];
        String[] split2 = versionStr2.split("\\.");
        Integer[] v2 = new Integer[split2.length];
        for(int i = 0; i < split1.length; i++)
            v1[i] = Integer.parseInt(split1[i]);
        for(int i = 0; i < split2.length; i++)
            v2[i] = Integer.parseInt(split2[i]);
        int minL = Math.min(v1.length, v2.length);
        
        for(int i = 0; i < minL; i++)
        {
            if(v1[i] == v2[i])
                continue;
            else if(v1[i] > v2[i])
                return 1;
            else
                return -1;
        }
        return 0;
    }
}
