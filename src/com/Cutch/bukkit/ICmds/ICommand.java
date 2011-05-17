package com.Cutch.bukkit.ICmds;

import java.util.ArrayList;

public class ICommand
{
    protected final ItemCommands plugin;
    public int id;
    public int click;
    public int clickevent;
    public String cmd;
    public int global = 0;
    public int bindto = 0;
    public ArrayList<Item> consume = new ArrayList<Item>();;
    public String player;
    public String key;
    public double cooldown = 0;
    private long lastrun = 0;
    protected ICommands parent = null;
    int count = 0;
    public ICommand(String player, String key, String cmd, ItemCommands plugin)
    {
        cmd = cmd.trim();
        this.player = player;
        this.key = key;
        this.id = Integer.parseInt(cmd.substring(0, 4));
        this.cooldown = Double.parseDouble(cmd.substring(4, 10))*0.1;
        this.click = Character.getNumericValue(cmd.charAt(10));
        this.clickevent = Character.getNumericValue(cmd.charAt(11));
        cmd = cmd.substring(13);
        int i = cmd.split(" ")[0].lastIndexOf(";");
        if(i != -1)
            this.consume = parseConsume(cmd.substring(0, i));
        this.cmd = cmd.substring(i+1);
        if(key.contains(":"))
            bindto = 0;
        else
            bindto = 1;
        if(isGlobal())
            global = 0;
        else
            global = 1;
        this.plugin = plugin;
    }
    public ICommand(String player, String key, int id, String cmd, int click, int clickevent, double cooldown, ArrayList<Item> consume, ItemCommands plugin)
    {
        this.player = player;
        this.key = key;
        this.cmd = cmd;
        this.click = click;
        this.clickevent = clickevent;
        this.id = id;
        this.consume = consume;
        this.cooldown = cooldown;
        if(key.contains(":"))
            bindto = 0;
        else
            bindto = 1;
        if(isGlobal())
            global = 0;
        else
            global = 1;
        this.plugin = plugin;
    }
    @Override
    public String toString()
    {
        parent = plugin.findByKey(player, key);
        String c = "";
        for(Item i : consume)
            c += i.toString();
        String id = plugin.lspace(String.valueOf(this.id), "0", 4);
        String cooldown = plugin.lspace(String.valueOf((int)(this.cooldown*10)), "0", 6);
        return id + cooldown + String.valueOf(click) + String.valueOf(clickevent) + String.valueOf(parent.cycle) + c + cmd;
    }
    public boolean shouldRunOriginalEvent()
    {
        return clickevent == 1;
    }
    public boolean canRunCommand()
    {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastrun > cooldown*1000)
        {
            lastrun = System.currentTimeMillis();
            return true;
        }
        return false;
    }
    public boolean isGlobal()
    {
        return player.isEmpty();
    }
    public static ArrayList<Item> parseConsume(String c)
    {
        ArrayList<Item> list = new ArrayList<Item>();
        String[] ss = c.split(";");
        for(String s : ss)
            list.add(new Item(s));
        return list;
    }
    public void count(){
        count++;
    }
}