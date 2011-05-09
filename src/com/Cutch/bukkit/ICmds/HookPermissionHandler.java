package com.Cutch.bukkit.ICmds;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

public class HookPermissionHandler extends PermissionHandler {
    public static PermissionHandler PermissionsHandler;
    static List<String> superL = new ArrayList<String>();
    public void addSuperAccess(String name)
    {
        if(!superL.contains(name))
            superL.add(name);
    }
    public void removeSuperAccess(String name)
    {
        superL.remove(name);
    }
    public HookPermissionHandler(Permissions Permissions)
    {
        PermissionsHandler = Permissions.getHandler();
    }
    @Override
    public boolean permission(Player player, String string) {
        if(player != null)
            if(!superL.contains(player.getName()))
                return PermissionsHandler.permission(player, string);
        return true;
    }

    @Override
    public boolean has(Player player, String string) {
        if(player != null)
            if(!superL.contains(player.getName()))
                return PermissionsHandler.has(player, string);
        return true;
    }

    @Override
    public void setDefaultWorld(String string) {
        PermissionsHandler.setDefaultWorld(string);
    }

    @Override
    public boolean loadWorld(String string) {
        return PermissionsHandler.loadWorld(string);
    }

    @Override
    public void forceLoadWorld(String string) {
        PermissionsHandler.forceLoadWorld(string);
    }

    @Override
    public boolean checkWorld(String string) {
        return PermissionsHandler.checkWorld(string);
    }

    @Override
    public void load() {
        PermissionsHandler.load();
    }

    @Override
    public void load(String string, Configuration c) {
        PermissionsHandler.load(string, c);
    }

    @Override
    public void reload() {
        PermissionsHandler.reload();
    }

    @Override
    public boolean reload(String string) {
        return PermissionsHandler.reload(string);
    }

    @Override
    public void setCache(String string, Map<String, Boolean> map) {
        PermissionsHandler.setCache(string, map);
    }

    @Override
    public void setCacheItem(String string, String string1, String string2, boolean bln) {
        PermissionsHandler.setCacheItem(string, string1, string2, bln);
    }

    @Override
    public Map<String, Boolean> getCache(String string) {
        return PermissionsHandler.getCache(string);
    }

    @Override
    public boolean getCacheItem(String string, String string1, String string2) {
        return PermissionsHandler.getCacheItem(string, string1, string2);
    }

    @Override
    public void removeCachedItem(String string, String string1, String string2) {
        PermissionsHandler.removeCachedItem(string, string1, string2);
    }

    @Override
    public void clearCache(String string) {
        PermissionsHandler.clearCache(string);
    }

    @Override
    public void clearAllCache() {
        PermissionsHandler.clearAllCache();
    }

    @Override
    public String getGroup(String string, String string1) {
        return PermissionsHandler.getGroup(string, string1);
    }

    @Override
    public String[] getGroups(String string, String string1) {
        return PermissionsHandler.getGroups(string, string1);
    }

    @Override
    public boolean inGroup(String string, String string1, String string2) {
        return PermissionsHandler.inGroup(string, string1, string2);
    }

    @Override
    public boolean inSingleGroup(String string, String string1, String string2) {
        return PermissionsHandler.inSingleGroup(string, string1, string2);
    }

    @Override
    public String getGroupPrefix(String string, String string1) {
        return PermissionsHandler.getGroupPrefix(string, string1);
    }

    @Override
    public String getGroupSuffix(String string, String string1) {
        return PermissionsHandler.getGroupSuffix(string, string1);
    }

    @Override
    public boolean canGroupBuild(String string, String string1) {
        return PermissionsHandler.canGroupBuild(string, string1);
    }

    @Override
    public String getGroupPermissionString(String string, String string1, String string2) {
        return PermissionsHandler.getGroupPermissionString(string, string1, string2);
    }

    @Override
    public int getGroupPermissionInteger(String string, String string1, String string2) {
        return PermissionsHandler.getGroupPermissionInteger(string, string1, string2);
    }

    @Override
    public boolean getGroupPermissionBoolean(String string, String string1, String string2) {
        return PermissionsHandler.getGroupPermissionBoolean(string, string1, string2);
    }

    @Override
    public double getGroupPermissionDouble(String string, String string1, String string2) {
        return PermissionsHandler.getGroupPermissionDouble(string, string1, string2);
    }

    @Override
    public String getUserPermissionString(String string, String string1, String string2) {
        return PermissionsHandler.getUserPermissionString(string, string1, string2);
    }

    @Override
    public int getUserPermissionInteger(String string, String string1, String string2) {
        return PermissionsHandler.getUserPermissionInteger(string, string1, string2);
    }

    @Override
    public boolean getUserPermissionBoolean(String string, String string1, String string2) {
        return PermissionsHandler.getUserPermissionBoolean(string, string1, string2);
    }

    @Override
    public double getUserPermissionDouble(String string, String string1, String string2) {
        return PermissionsHandler.getUserPermissionDouble(string, string1, string2);
    }

    @Override
    public String getPermissionString(String string, String string1, String string2) {
        return PermissionsHandler.getGroupPermissionString(string, string1, string2);
    }

    @Override
    public int getPermissionInteger(String string, String string1, String string2) {
        return PermissionsHandler.getPermissionInteger(string, string1, string2);
    }

    @Override
    public boolean getPermissionBoolean(String string, String string1, String string2) {
        return PermissionsHandler.getPermissionBoolean(string, string1, string2);
    }

    @Override
    public double getPermissionDouble(String string, String string1, String string2) {
        return PermissionsHandler.getPermissionDouble(string, string1, string2);
    }

    @Override
    public void addGroupInfo(String string, String string1, String string2, Object o) {
        PermissionsHandler.addGroupInfo(string, string1, string2, o);
    }

    @Override
    public void removeGroupInfo(String string, String string1, String string2) {
        PermissionsHandler.removeGroupInfo(string, string1, string2);
    }

    @Override
    public void addUserPermission(String string, String string1, String string2) {
        PermissionsHandler.addUserPermission(string, string1, string2);
    }

    @Override
    public void removeUserPermission(String string, String string1, String string2) {
        PermissionsHandler.removeUserPermission(string, string1, string2);
    }

    @Override
    public void save(String string) {
        PermissionsHandler.save(string);
    }

    @Override
    public void saveAll() {
        PermissionsHandler.saveAll();
    }
}
