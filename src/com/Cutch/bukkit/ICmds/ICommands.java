package com.Cutch.bukkit.ICmds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
public class ICommands
{
    protected final ItemCommands plugin;
    int click = 0;
    int bindto = 0;
    String key="";
    String player="";
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
    int slast = -1;
    int shuffle()
    {
        int min = Integer.MAX_VALUE;
        Dictionary<Integer, ArrayList<Integer>> playcount = new Hashtable<Integer, ArrayList<Integer>>();
        Enumeration<ICommand> elements = cmds.elements();
        while(elements.hasMoreElements())
        {
            ICommand nextElement = elements.nextElement();
            min = Math.min(min, nextElement.count);
            ArrayList<Integer> t = playcount.get(nextElement.count);
            if(t == null)
                t = new ArrayList<Integer>();
            t.add(nextElement.id);
            playcount.put(nextElement.count, t);
        }
        ArrayList<Integer> list = playcount.get(min);
        Random r = new Random();
        int size = list.size();
        Integer id;
        do {
            int n = (int)(r.nextDouble()*size);
            id = list.get(n);
        }while(id == slast && size > 1);
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
    int next()
    {
        Enumeration<Integer> elements = cmds.keys();
        ArrayList<Integer> list = Collections.list(elements);
        if(list == null || list.isEmpty())
            return -1;
        Collections.sort(list);
        int next = ++last;
        if(next >= list.size())
            next = 0;
        return list.get(last = next);
    }
}