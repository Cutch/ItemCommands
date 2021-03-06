package com.Cutch.bukkit.ICmds;

public abstract class iConomySupport {
    ItemCommands plugin = null;
    String Version = "";
    boolean isV5 = false;
    public iConomySupport(ItemCommands instance)
    {
        this.plugin = instance;
        Version = this.plugin.getServer().getVersion();
        isV5 = Version.startsWith("5");
    }
    public void add(String name, double amount)
    {
        if(isV5)
            com.iConomy.iConomy.getAccount(name).getHoldings().add(amount);
        else
            com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(name).add(amount);
            
    }
    public void subtract(String name, double amount)
    {
        if(isV5)
            com.iConomy.iConomy.getAccount(name).getHoldings().subtract(amount);
        else
            com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(name).subtract(amount);
    }
    public boolean hasEnough(String name, double amount)
    {
        if(isV5)
            return com.iConomy.iConomy.getAccount(name).getHoldings().hasEnough(amount);
        else
            return com.nijiko.coelho.iConomy.iConomy.getBank().getAccount(name).hasEnough(amount);
    }
}
