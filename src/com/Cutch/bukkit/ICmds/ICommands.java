package com.Cutch.bukkit.ICmds;

import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
public class ICommands
{
    protected final ItemCommands plugin;
    int click = 0;
    int bindto = 0;
    String key="";
    String player="";
    public Dictionary<Integer, ICommand> cmds = new Hashtable<Integer, ICommand>();
    public ICommands(ItemCommands instance)
    {
        plugin = instance;
    }
    public ICommands(String player, String key, ItemCommands instance)
    {
        this.key = key;
        this.player = player;
        plugin = instance;
    }
    public ICommands(String player, String key, ItemCommands instance, ICommand i)
    {
        this.key = key;
        this.player = player;
        plugin = instance;
        putDict(i);
    }
    Integer[] getIDList()
    {
        Enumeration<Integer> keys = cmds.keys();
        Integer[] ids = new Integer[cmds.size()];
        int i = 0;
        while(keys.hasMoreElements()){
            ids[i] = keys.nextElement();
            i++;
        }
        Arrays.sort(ids);
        return ids;
    }
    Enumeration<ICommand> getElements()
    {
        return cmds.elements();
    }
    int putDict(ICommand i)
    {
        int id = i.id;
        cmds.put(id, i);
        return id;
    }
    ICommand get(int id)
    {
        return cmds.get(id);
    }
    ICommand remove(int id)
    {
        return cmds.remove(id);
    }
    ICommand findByID(int id)
    {
        return cmds.get(id);
    }
    boolean isEmpty()
    {
        return cmds.isEmpty();
    }
    int size()
    {
        return cmds.size();
    }
}