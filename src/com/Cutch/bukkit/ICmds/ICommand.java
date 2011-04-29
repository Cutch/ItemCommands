package com.Cutch.bukkit.ICmds;

public class ICommand
{
    public int click;
    public int clickevent;
    public String cmd;
    public ICommand(String cmd)
    {
        cmd = cmd.trim();
        if(cmd.length()>= 1)
        this.click = cmd.length()>= 1?Character.getNumericValue(cmd.charAt(0)):0;
        if(cmd.length()>= 2)
        this.clickevent = cmd.length()>= 2?Character.getNumericValue(cmd.charAt(1)):0;
        this.cmd = cmd.length()>= 3?cmd.substring(2):"";
    }
    public ICommand(String cmd, int click, int clickevent)
    {
        this.cmd = cmd;
        this.click = click;
        this.clickevent = clickevent;
    }
    @Override
    public String toString()
    {
        return String.valueOf(click) + String.valueOf(clickevent) + cmd;
    }
    public boolean runEvent()
    {
        return clickevent == 1;
    }
}
