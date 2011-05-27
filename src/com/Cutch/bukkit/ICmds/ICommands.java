package com.Cutch.bukkit.ICmds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
public class ICommands
{
    protected final ItemCommands plugin;
    int click = 0;
    int bindto = 0;
    String key="";
    String player="";
    HashMap<String, Integer> globalLast = new HashMap<String, Integer>();
    HashMap<String, Integer> globalSLast = new HashMap<String, Integer>();
    public Dictionary<Integer, ICommand> cmds = new Hashtable<Integer, ICommand>();
    int cycle = 0;
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
    int putDict(ICommand i, int cycle)
    {
        int id = i.id;
        this.cycle = cycle;
        cmds.put(id, i);
        return id;
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
    public boolean isGlobal()
    {
        return player.isEmpty();
    }
    int slast = -1;
    int shuffle(String player)
    {
        int min = Integer.MAX_VALUE;
        Dictionary<Integer, ArrayList<Integer>> playcount = new Hashtable<Integer, ArrayList<Integer>>();
        Enumeration<ICommand> elements = cmds.elements();
        while(elements.hasMoreElements())
        {
            ICommand nextElement = elements.nextElement();
            Integer count = nextElement.getCount(player);
            min = Math.min(min, count);
            ArrayList<Integer> t = playcount.get(count);
            if(t == null)
                t = new ArrayList<Integer>();
            t.add(nextElement.id);
            playcount.put(count, t);
        }
        if(isGlobal())
            slast = globalSLast.get(player);
        ArrayList<Integer> list = playcount.get(min);
        Random r = new Random();
        int size = list.size();
        Integer id;
        do {
            int n = (int)(r.nextDouble()*size);
            id = list.get(n);
        }while(id == slast && size > 1);
        if(isGlobal())
            globalSLast.put(player, id);
        return slast = id;
    }
    int random()
    {
        Enumeration<ICommand> elements = cmds.elements();
        ArrayList list = Collections.list(elements);
        if(list == null || list.isEmpty())
            return -1;
        Random r = new Random();
        int n = (int)(r.nextDouble()*list.size());
        return ((ICommand)list.get(n)).id;
    }
    int last = -1;
    int next(String player)
    {
        Enumeration<Integer> elements = cmds.keys();
        ArrayList<Integer> list = Collections.list(elements);
        if(list == null || list.isEmpty())
            return -1;
        Collections.sort(list);
        if(isGlobal())
            last = globalLast.get(player);
        int next = ++last;
        if(next >= list.size())
            next = 0;
        if(isGlobal())
            globalLast.put(player, next);
        return list.get(last = next);
    }
}