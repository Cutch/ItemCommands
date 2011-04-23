package com.Cutch.bukkit.ICmds;
 
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import me.taylorkelly.help.Help;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
public class ItemCommands extends JavaPlugin {
    public static PermissionHandler Permissions;
    private final PlayerEvents playerListener = new PlayerEvents(this);

    public void onDisable() {
        System.out.println("ItemCommands is Disabled");
    }

    public void onEnable() {
        PluginManager plmgr = getServer().getPluginManager();
        plmgr.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Monitor, this);
        plmgr.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Monitor, this);

        PluginDescriptionFile desc = this.getDescription();
        System.out.println("ItemCommands: v" + desc.getVersion() + " is Enabled");
        playerListener.readDB();
        playerListener.readPref();
        setupPermissions();
        setupHelp();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ic") && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player) {
                Player player = (Player)sender;
                if (!playerListener.checkPermissions(player, "ICmds.admin", playerListener.adminNeedOP)) {
                    return false;
                }
                player.sendRawMessage(ChatColor.RED+"ItemCommands: Has been Reloaded");
            }
            playerListener.readDB();
            playerListener.readPref();
            System.out.println("ItemCommands: Has been Reloaded");
            return true;
        }
        return false;
    }
    private void setupPermissions() {
        Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
        if (this.Permissions == null) {
            if(playerListener.permissions == 1)
                System.out.println("ItemCommands: Using Basic Permissions");
            else if(test != null) {
                Permissions p = (Permissions)test;
                this.Permissions = p.getHandler();
                System.out.println("ItemCommands: Using Permissions Plugin v" + p.version);
            } else {
                playerListener.permissions=1;
                System.out.println("ItemCommands: Permission system not detected. Using Basic Permissions.");
            }
        }
    }
    private void setupHelp()
    {
        Plugin test = this.getServer().getPluginManager().getPlugin("Help");
        if (test != null) {
            String[] permissions = new String[]{"ICmds.create", "ICmds.use", "ICmds.admin"};
            Help help = ((Help)test);
            help.registerCommand("ic", "Help for Item Commands", this, true, permissions);
        }
    }
}
