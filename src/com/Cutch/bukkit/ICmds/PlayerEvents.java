package com.Cutch.bukkit.ICmds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerEvents extends PlayerListener {
    private final ItemCommands plugin;
    public PlayerEvents(ItemCommands instance) {
        plugin = instance;
    }
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        ICPlayer player = new ICPlayer(event.getPlayer()) { };
        if(plugin.checkPermissions(player, "ICmds.use", plugin.useNeedOP))
        {
            int click = -1;
            if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                click = 0;
            else if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                click = 1;
            if(click != -1)
            {
                ItemStack i = player.getItemInHand();
                String[] keys = new String[2];
                int bindto = 1;
                boolean slot = bindto == 1;
                keys[0] = String.valueOf(i.getTypeId()) + ":" + String.valueOf(i.getDurability());
                keys[1] = String.valueOf(player.getInventory().getHeldItemSlot()+1);
                boolean global = false;
                Item item;
                ICommands cmds = plugin.getDict(player.getName(), keys[bindto]).get(keys[bindto]);
                if (!containsCommand(cmds.getElements(), click)){
                    cmds = plugin.getDict(player.getName(), keys[1-bindto]).get(keys[1-bindto]);
                    slot = (1-bindto) == 1;}
                if (!containsCommand(cmds.getElements(), click)){
                    global = true;
                    cmds = plugin.getDict("", keys[bindto]).get(keys[bindto]);
                    slot = bindto == 1;}
                if (!containsCommand(cmds.getElements(), click)){
                    cmds = plugin.getDict("", keys[1-bindto]).get(keys[1-bindto]);
                    slot = (1-bindto) == 1;}
                if (containsCommand(cmds.getElements(), click)) {
                    item = new Item(keys[slot?1:0]);
                    Integer[] ids = cmds.getIDList();
                    if(cmds.cycle == 0)
                    {
                        for(Integer id : ids)
                        {
                            runCommand(event, id, player, cmds, click, global, slot, item);
                        }
                    }
                    else if(cmds.cycle == 1)
                    {
                        int id = cmds.shuffle(player.getName());
                        runCommand(event, id, player, cmds, click, global, slot, item);
                    }
                    else if(cmds.cycle == 2)
                    {
                        int id = cmds.next(player.getName());
                        runCommand(event, id, player, cmds, click, global, slot, item);
                    }
                    else if(cmds.cycle == 3)
                    {
                        int id = cmds.random();
                        runCommand(event, id, player, cmds, click, global, slot, item);
                    }
                }
            }
        }
    }
    void runCommand(PlayerInteractEvent event, int id, ICPlayer player, ICommands cmds, int click, boolean global, boolean slot, Item item)
    {
        if(id == -1)
            return;
        ICommand cmd = cmds.findByID(id);
        boolean listed = plugin.isListed(cmd, item, slot);
        if(listed)
        {
            if(cmd != null && cmd.click == click && cmd.canRunCommand(player.getName()))
            {
                String items = "";
                if((items = checkReagents(player, cmd.consume)).isEmpty())
                {
                    consume(player, cmd.consume);
                    String cmdStr = plugin.stringReplacer(cmd.cmd, player, null);
                    if(cmdStr.startsWith("/"))
                    {
                        String[] bcmd = cmdStr.split(" ");
                        boolean wait;
                        if(wait=bcmd[0].startsWith("/wait"))
                        {
                            try
                            {
                                double time = Double.parseDouble(bcmd[1]);
                                Thread.sleep((long)time*1000);
                            }catch(Exception e)
                            {
                                plugin.sendMessage(player, plugin.errc + "Argument 2 expects an ID (Remove is Arg 1)");
                            }
                        }
                        if(!wait || wait && bcmd.length > 2) 
                        {
                            String[] args;
                            if(wait)
                            {
                                args = new String[bcmd.length - 2];
                                for(int i2 = 2; i2 < bcmd.length; i2++)
                                    args[i2-2] = bcmd[i2];
                            }
                            else
                            {
                                args = new String[bcmd.length - 1];
                                for(int i2 = 1; i2 < bcmd.length; i2++)
                                    args[i2-1] = bcmd[i2];
                            }
                            PluginCommand p = plugin.getServer().getPluginCommand(bcmd[0].replaceFirst("/", ""));
                            boolean p1 = plugin.checkPermissions(player, "ICmds.super", plugin.superNeedOP);
                            boolean p2 = plugin.checkPermissions(player, "ICmds.super.global", plugin.superGlobalNeedOP);
                            if(p1 || (p2 && global))
                            {
                                player.addSuperAccess();
                                if(plugin.pms != null)
                                    plugin.pms.addSuperAccess(player.getWorld().getName(), player.getName());
                            }
                            if(p != null)
                            {
                                p.execute(player, bcmd[0].replaceFirst("/", ""), args);
                                cmd.count(player.getName());
                            }
                            if(p1 || (p2 && global))
                            {
                                player.removeSuperAccess();
                                if(plugin.pms != null)
                                    plugin.pms.removeSuperAccess(player.getWorld().getName(), player.getName());
                            }
                        }
                    }
                    else {
                        player.sendMessage(ChatColor.BLUE+cmdStr);
                        cmd.count(player.getName());
                    }
                    event.setCancelled(cmd.clickevent == 0);
                    if(cmd.clickevent == 1)
                        event.setUseItemInHand(Result.ALLOW);
                    else
                        event.setUseItemInHand(Result.DENY);
                }
                else
                    player.sendMessage(plugin.errc+"Could not run command missing "+items);
            }
        }
        else{
            player.sendMessage(plugin.errc+"Using command \"" + cmd.getRunningCommand() + "\" with "+(cmd.bindto==0?Material.getMaterial(item.id):"slot "+cmd.key)+" is forbidden.");
        }
    }
    boolean containsCommand(Enumeration<ICommand> elements, int click)
    {
        while(elements.hasMoreElements())
        {
            ICommand cmd = elements.nextElement();
            if(cmd.click == click)
                return true;
        }
        return false;
    }
    String checkReagents(Player player, ArrayList<Item> list)
    {
        boolean isFree = plugin.checkPermissions(player, "ICmd.Free", plugin.freeNeedOP);
        PlayerInventory inventory = player.getInventory();
        String name = "";
        if(plugin.iConomyA == 1)
        {
            for(Item item : list)
            {
                int abs = Math.abs(item.amount);
                if(item.id == -2)
                {
                    if(item.amount < 0 && !isFree){
                        if(!plugin.ics.hasEnough(player.getName(), abs))
                            return "$"+abs;
                    }
                } 
                else if(item.id == -3)
                {
                    if(item.amount < 0){
                        if(item.amount > inventory.getItemInHand().getAmount())
                            return "any " + abs + " item"+(abs > 1 ? "s" : "")+" from Hand";
                    }
                } else {
                    if(item.amount < 0)
                        if(!inventory.contains(item.id, abs))
                            return item.getName();
                }
            }
        } else {
            for(Item item : list)
            {
                int abs = Math.abs(item.amount);
                if(item.id != -2){
                    if(item.amount < 0)
                        if(!inventory.contains(item.id, abs))
                            return item.getName();
                } 
                else if(item.id == -3)
                {
                    if(item.amount < 0){
                        if(item.amount > inventory.getItemInHand().getAmount())
                            return "any " + abs + " item"+(abs > 1 ? "s" : "")+" from Hand";
                    }
                }
            }
        }
        return name;
    }
    void consume(Player player, ArrayList<Item> list)
    {
        boolean isFree = plugin.checkPermissions(player, "ICmd.Free", plugin.freeNeedOP);
        PlayerInventory inventory = player.getInventory();
        if(plugin.iConomyA == 1)
        {
            for(Item item : list)
            {
                if(item.id == -2)
                {
                    if(item.amount < 0 && !isFree){
                        int abs = Math.abs(item.amount);
                        plugin.ics.subtract(player.getName(), abs);
                    }
                    else if(item.amount > 0)
                        plugin.ics.add(player.getName(), item.amount);
                }
                else if(item.id == -3)
                {
                    if(item.amount < 0){
                        int i = inventory.getHeldItemSlot();
                        ItemStack get = inventory.getItem(i);
                        int amount = get.getAmount();
                        int diff = amount+item.amount;
                        if(diff > 0)
                        {
                            get.setAmount(amount+item.amount);
                            inventory.setItem(i, get);
                        }
                        else if(diff == 0)
                            inventory.clear(i);
                    }
                } else {
                    if(item.amount < 0 && !isFree)
                    {
                        HashMap<Integer, ? extends ItemStack> all = inventory.all(item.id);
                        Iterator<Integer> iterator = all.keySet().iterator();
                        while(iterator.hasNext())
                        {
                            int i = iterator.next();
                            ItemStack get = all.get(i);
                            int amount = get.getAmount();
                            int diff = amount+item.amount;
                            if(diff > 0)
                            {
                                get.setAmount(amount+item.amount);
                                inventory.setItem(i, get);
                                break;
                            }
                            else if(diff == 0){
                                inventory.clear(i);
                                break;}
                        }
                    }
                    else
                        inventory.addItem(new ItemStack(item.id, item.amount, (short)item.damage));
                }
            }
        } else {
            for(Item item : list)
            {
                if(item.id != -2) {
                    if(item.amount < 0 && !isFree)
                    {
                        HashMap<Integer, ? extends ItemStack> all = inventory.all(item.id);
                        Iterator<Integer> iterator = all.keySet().iterator();
                        while(iterator.hasNext())
                        {
                            int i = iterator.next();
                            ItemStack get = all.get(i);
                            int amount = get.getAmount();
                            int diff = amount+item.amount;
                            if(diff > 0)
                            {
                                get.setAmount(amount+item.amount);
                                inventory.setItem(i, get);
                                break;
                            }
                            else if(diff == 0){
                                inventory.clear(i);
                                break;}
                        }
                    }
                    else
                        inventory.addItem(new ItemStack(item.id, item.amount, (short)item.damage));
                }
                else if(item.id == -3)
                {
                    if(item.amount < 0){
                        int i = inventory.getHeldItemSlot();
                        ItemStack get = inventory.getItem(i);
                        int amount = get.getAmount();
                        int diff = amount+item.amount;
                        if(diff > 0)
                        {
                            get.setAmount(amount+item.amount);
                            inventory.setItem(i, get);
                        }
                        else if(diff == 0){
                            inventory.clear(i);}
                    }
                }
            }
        }
        player.updateInventory();
    }
}
