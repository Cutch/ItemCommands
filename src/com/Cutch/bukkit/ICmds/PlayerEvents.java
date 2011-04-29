package com.Cutch.bukkit.ICmds;

import java.util.Dictionary;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

public class PlayerEvents extends PlayerListener {
    private final ItemCommands plugin;
    public PlayerEvents(ItemCommands instance) {
        plugin = instance;
    }
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(plugin.checkPermissions(player, "ICmds.use", plugin.useNeedOP))
        {
            Dictionary<String, ICommand> dict = plugin.getDict(player);
            ItemStack i = player.getItemInHand();
            String[] keys = new String[2];
            int bindto = dict.get("bindto").click;
            keys[0] = String.valueOf(i.getTypeId()) + ":" + String.valueOf(i.getDurability());
            keys[1] = String.valueOf(player.getInventory().getHeldItemSlot());
            ICommand cmd = dict.get(keys[bindto]);
            if (cmd == null)
                cmd = dict.get(keys[1-bindto]);
            if (cmd != null)  {
                if((cmd.click == 0 && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) ||
                (cmd.click == 1 && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)))
                {
                    if(cmd.cmd.startsWith("/"))
                    {
                        String[] bcmd = cmd.cmd.split(" ");
                        String[] args = new String[bcmd.length - 1];
                        for(int i2 = 1; i2 < bcmd.length; i2++)
                            args[i2-1] = bcmd[i2];
                        PluginCommand p = plugin.getServer().getPluginCommand(bcmd[0].replaceFirst("/", ""));
                        if(p != null)
                            p.execute(player, bcmd[0].replaceFirst("/", ""), args);
                    }
                    else
                        player.sendMessage(ChatColor.BLUE+cmd.cmd);
                    event.setCancelled(cmd.clickevent == 0);
                    if(cmd.clickevent == 1)
                        event.setUseItemInHand(Result.ALLOW);
                    else
                        event.setUseItemInHand(Result.DENY);
                }
            }
        }
    }
}
