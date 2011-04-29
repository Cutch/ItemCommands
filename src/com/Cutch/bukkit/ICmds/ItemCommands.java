package com.Cutch.bukkit.ICmds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import me.taylorkelly.help.Help;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
public class ItemCommands extends JavaPlugin {
    public boolean createNeedOP = true;
    public boolean useNeedOP = false;
    public boolean adminNeedOP = true;
    ChatColor cmdc = ChatColor.BLUE;
    ChatColor descc = ChatColor.AQUA;
    ChatColor errc = ChatColor.RED;
    ChatColor infoc = ChatColor.YELLOW;
    String properties = "ItemCommands.properties";
    String database = "Commands.db";
    int bclick = 0;
    public static PermissionHandler Permissions;
    private final PlayerEvents playerListener = new PlayerEvents(this);
    public Dictionary<String, Dictionary<String, ICommand>> players = new Hashtable<String, Dictionary<String, ICommand>>();

    public void onDisable() {
        System.out.println("ItemCommands is Disabled");
    }

    public void onEnable() {
        PluginManager plmgr = getServer().getPluginManager();
        plmgr.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Highest, this);

        PluginDescriptionFile desc = this.getDescription();
        System.out.println("ItemCommands: v" + desc.getVersion() + " is Enabled");
        readDB();
        readPref();
        setupPermissions();
        setupHelp();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd1, String commandLabel, String[] args) {
        String cmdmsg = cmd1.getName();
        Player player = null;
        if (sender instanceof Player)
            player = (Player)sender;
        if(player != null || cmdmsg.equalsIgnoreCase("icmd"))
        {
            if(args.length >= 1)
            {
                if(args[0].equalsIgnoreCase("add"))
                {
                    if(checkPermissions(player, "ICmds.create", createNeedOP))
                    {
                        if(args.length >= 2)
                        {
                            String key;
                            Dictionary<String, ICommand> dict = getDict(player);
                            int bindto = dict.get("bindto").click;
                            if(bindto == 1)
                                key = String.valueOf(player.getInventory().getHeldItemSlot());
                            else
                                key = String.valueOf(player.getItemInHand().getTypeId()) + ":" + String.valueOf(player.getItemInHand().getDurability());
                            String a = "";
                            for(int i = 1; i < args.length; i++)
                                a += (i == 1?"":" ") + args[i];
                            ICommand cmd = new ICommand(a, bclick, 0);
                            dict.put(key, cmd);
                            String pcmd = cmd.cmd;
                            if(pcmd.contains(":0"))
                                pcmd = pcmd.replaceFirst(":0", "");
                            player.sendMessage(cmdc + "Command: " + pcmd + " added to "+(bindto == 0 ? "item" : "slot") + " " + key + " for " + (permissions == 0?player.getName():"Everyone"));
                            saveDB();
                        }
                        else
                            player.sendMessage(cmdc + "Usage: /ic add [command] "+descc+"#Add command to the selected item");
                    }
                    else
                        player.sendMessage(errc + "You do not have the required permissions for this.");
                }
                else if(args[0].equalsIgnoreCase("remove"))
                {
                    if(checkPermissions(player, "ICmds.create", createNeedOP))
                    {
                        if(args.length == 1){
                            String cmd = "";
                            Dictionary<String, ICommand> dict = getDict(player);
                            String key;
                            int bindto = dict.get("bindto").click;
                            if(bindto == 1)
                                key = String.valueOf(player.getInventory().getHeldItemSlot());
                            else
                                key = String.valueOf(player.getItemInHand().getTypeId()) + ":" + String.valueOf(player.getItemInHand().getDurability());

                            if(player != null)
                            {
                                if(dict == null || (cmd = dict.remove(key).cmd) == null)
                                    player.sendMessage(cmdc + "Command not mapped to item " + key);
                                else
                                    player.sendMessage(cmdc + "Command: "+ cmd +" removed from "+(bindto == 0 ? "item" : "slot")+" " + key + " for " + (permissions == 0?player.getName():"Everyone"));
                            } else {
                                if(dict == null || (cmd = dict.remove(key).cmd) == null)
                                    System.out.println("Command not mapped to item " + key);
                                else
                                    System.out.println("Command: "+ cmd +" removed from "+(bindto == 0 ? "item" : "slot")+" " + key + " for " + (permissions == 0?player.getName():"Everyone"));
                            }
                            saveDB();
                        }
                    }
                    else
                        player.sendMessage(errc + "You do not have the required permissions for this.");
                }
                else if(args[0].equalsIgnoreCase("click"))
                {
                    if(args.length >= 2)
                    {
                        String key;
                        Dictionary<String, ICommand> dict = getDict(player);
                        int bindto = dict.get("bindto").click;
                        if(bindto == 1)
                            key = String.valueOf(player.getInventory().getHeldItemSlot());
                        else
                            key = String.valueOf(player.getItemInHand().getTypeId()) + ":" + String.valueOf(player.getItemInHand().getDurability());

                        if(checkPermissions(player, "ICmds.create", createNeedOP))
                        {
                            ICommand cmd = dict.get(key);
                            if(args[1].startsWith("r"))
                            {
                                cmd.click = 0;
                                dict.put(key, cmd);
                                player.sendMessage(cmdc + (bindto == 0 ? "Item" : "Slot") + " " + key + " is triggered with a right click");
                                saveDB();
                            }
                            else if(args[1].startsWith("l"))
                            {
                                cmd.click = 1;
                                dict.put(key, cmd);
                                player.sendMessage(cmdc + (bindto == 0 ? "Item" : "Slot") + " " + key + " is triggered with a left click");
                                saveDB();
                            }
                            else
                                player.sendMessage(errc + "Parameter " + args[1] + " not valid.");
                        }
                        else
                            player.sendMessage(errc + "You do not have the required permissions for this.");
                    }
                    else
                        player.sendMessage(cmdc + "Usage: /ic click [left l|right r] "+descc+"#Change trigger of the selected item");
                }
                else if(args[0].equalsIgnoreCase("clickevent"))
                {
                    if(args.length >= 2)
                    {
                        String key;
                        Dictionary<String, ICommand> dict = getDict(player);
                        int bindto = dict.get("bindto").click;
                        if(bindto == 1)
                            key = String.valueOf(player.getInventory().getHeldItemSlot());
                        else
                            key = String.valueOf(player.getItemInHand().getTypeId()) + ":" + String.valueOf(player.getItemInHand().getDurability());

                        if(checkPermissions(player, "ICmds.create", createNeedOP))
                        {
                            ICommand cmd = dict.get(key);
                            if(args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("t"))
                            {
                                cmd.clickevent = 1;
                                dict.put(key, cmd);
                                player.sendMessage(cmdc + (bindto == 0 ? "Item" : "Slot") + " " + key + " runs click events normally");
                                saveDB();
                            }
                            else if(args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("f"))
                            {
                                cmd.clickevent = 0;
                                dict.put(key, cmd);
                                player.sendMessage(cmdc + (bindto == 0 ? "Item" : "Slot") + " " + key + " cancels click events");
                                saveDB();
                            }
                            else
                                player.sendMessage(errc + "Parameter " + args[1] + " not valid.");
                        }
                        else
                            player.sendMessage(errc + "You do not have the required permissions for this.");
                    }
                    else
                        player.sendMessage(cmdc + "Usage: /ic clickevent [on t| off f] "+descc+"#Change whether the normal click functionality is performed");
                }
                else if(args[0].equalsIgnoreCase("per"))
                {
                    if(args.length >= 2)
                    {
                        if(checkPermissions(player, "ICmds.admin", adminNeedOP))
                        {
                            if(args[1].startsWith("pl"))
                            {
                                per = 1;
                                player.sendMessage(cmdc + "Per Player command system initialized");
                                savePref();
                            }
                            else if(args[1].startsWith("gl"))
                            {
                                per = 0;
                                player.sendMessage(cmdc + "Global command system initialized");
                                savePref();
                            }
                            else if(args[1].startsWith("gr"))
                            {
                                per = 2;
                                player.sendMessage(cmdc + "Group command system initialized (NOT WORKING)");
                                savePref();
                            }
                            else
                                player.sendMessage(errc + "Parameter " + args[1] + " not valid.");
                        }
                        else
                            player.sendMessage(errc + "You do not have the required permissions for this.");
                    }
                    else
                        player.sendMessage(cmdc + "Usage: /ic per [player in|global gl] "+descc+"#Set commands globally per player");
                }
                else if(args[0].equalsIgnoreCase("bindto"))
                {
                    Dictionary<String, ICommand> dict = getDict(player);
                    if((per == 0 && checkPermissions(player, "ICmds.create", createNeedOP))
                            || (per == 1 && checkPermissions(player, "ICmds.use", useNeedOP)))
                    {
                        if(args.length >= 2)
                        {
                            if(args[1].startsWith("s"))
                            {
                                player.sendMessage(cmdc + "Binded to slots");
                                dict.put("bindto", new ICommand("", 1, 0));
                                saveDB();
                            }
                            else if(args[1].startsWith("i"))
                            {
                                dict.put("bindto", new ICommand("", 0, 0));
                                player.sendMessage(cmdc + "Binded to items");
                                saveDB();
                            }
                            else
                                player.sendMessage(errc + "Parameter " + args[1] + " not valid.");
                        }
                        else
                            player.sendMessage(cmdc + "Usage: /ic bindto [item i|slot s] "+descc+"#Binds commands to slots or items");
                    }
                    else
                        player.sendMessage(errc + "You do not have the required permissions for this.");
                }
                else if(args[0].equalsIgnoreCase("list"))
                {
                    if(checkPermissions(player, "ICmds.use", useNeedOP))
                    {
                        Dictionary<String, ICommand> dict = getDict(player);
                        int bindto = dict.get("bindto").click;

                        String sbindto = bindto == 0 ? "Item" : "Slot";
                        String key = "";
                        if(bindto == 1)
                            key = String.valueOf(player.getInventory().getHeldItemSlot());
                        else
                            key = String.valueOf(player.getItemInHand().getTypeId()) + ":" + String.valueOf(player.getItemInHand().getDurability());
                        ICommand cmd2 = dict.get(key);
                        player.sendMessage(infoc + "Commands are set "+(this.per == 1 ? "per player" : "globally") + " and Binded to "+sbindto+"s");
                        if(cmd2 != null)
                        {
                            String a = (cmd2.click == 0 ? "right" : "left");
                            player.sendMessage(infoc + "Current Command: "+descc+cmd2.cmd);
                            player.sendMessage(infoc + "Triggered by a " + a + " click, " + a + " click events are "+(cmd2.clickevent == 1 ? "enabled" : "disable"));
                        }

                        String str = "Commands (" + (this.per == 1 ? player.getName() : "Global") + ")";
                        player.sendMessage(infoc + (bindto == 0 ? "Item" : "Slot") + "     Click Trigger     " + str);
                        player.sendMessage(infoc + "-----------------------------------------------");
                        ArrayList<String> list = Collections.list(dict.keys());
                        int linei=0;
                        if(list != null) {
                            for (int i2 = 0; i2 < list.size(); i2++)
                            {
                                String i = list.get(i2);
                                if(!i.equalsIgnoreCase("bindto") && !i.equalsIgnoreCase("click")){
                                    if((bindto == 0 && i.contains(":")) || (bindto == 1 && !i.contains(":")))
                                    {
                                        ICommand cmd = dict.get(i);
                                        String click = (cmd.click == 1 ? "Left" : "Right");
                                        player.sendMessage((linei%2==0 ? cmdc : descc) + lspace(i,' ',4) + lspace(click,' ',20) + "     "+ cmd.cmd);
                                        linei++;
                                    }
                                }
                            }
                        }
                    }
                    else
                        player.sendMessage(errc + "You do not have the required permissions for this.");
                }
                else if(args[0].equalsIgnoreCase("reload"))
                {
                    if(checkPermissions(player, "ICmds.admin", adminNeedOP))
                    {
                        readDB();
                        readPref();
                        System.out.println("ItemCommands Has been Reloaded");
                        player.sendMessage(ChatColor.RED+"ItemCommands: Has been Reloaded");
                    }
                    else
                        player.sendMessage(errc + "You do not have the required permissions for this.");
                }
                else
                    showHelp(player);
            }
            else
                showHelp(player);
            //event.setCancelled(true);
            return true;
        }
        return false;
    }
    private void setupPermissions() {
        Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
        if (this.Permissions == null) {
            if(permissions == 1)
                System.out.println("ItemCommands: Using Basic Permissions");
            else if(test != null) {
                Permissions p = (Permissions)test;
                this.Permissions = p.getHandler();
                System.out.println("ItemCommands: Using Permissions Plugin v" + p.version);
            } else {
                permissions=1;
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
    void saveData(ArrayList<String> data, String file)
    {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(this.getDataFolder() + File.separator + file);
            bufferedWriter = new BufferedWriter(fileWriter);

            for (int i = 0; i < data.size(); i++)
                bufferedWriter.write(data.get(i) + "\n", 0, data.get(i).length()+1);

            bufferedWriter.close();
            fileWriter.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(this.getDataFolder() + File.separator + file + " Not Found... Making new file.");
            this.getDataFolder().mkdir();
            new File(this.getDataFolder() + File.separator + file);
            saveData(data, file);
        }
        catch (IOException e) { System.out.println(this.getDataFolder() + File.separator + file + " Could not be open"); }
    }
    ArrayList<String> readData(String file)
    {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        ArrayList<String> data = new ArrayList<String>();
        try {
            fileReader = new FileReader(this.getDataFolder() + File.separator + file);
            bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                data.add(line);
            }
            bufferedReader.close();
            fileReader.close();
        }
        catch (FileNotFoundException e) {
            this.getDataFolder().mkdir();
            new File(this.getDataFolder() + File.separator + file);
            System.out.println(this.getDataFolder() + File.separator + file + " Not Found");
            data = null; }
        catch (IOException e) {
            System.out.println(this.getDataFolder() + File.separator + file + " Could not be open");
            data = null; }
        return data;
    }
    void saveDB()
    {
        ArrayList<String> data = new ArrayList<String>();
        Enumeration<String> dict = players.keys();
        for (;dict.hasMoreElements();)
        {
            String i = dict.nextElement();
            Dictionary<String, ICommand> lines = players.get(i);
            Enumeration<String> dict2 = lines.keys();

            for (;dict2.hasMoreElements();)
            {
                String i2 = dict2.nextElement();
                ICommand line = lines.get(i2);

                data.add(i + " " + i2 + " " + line.toString());
            }
        }
        saveData(data, database);
    }
    void readDB()
    {
        ArrayList<String> data = readData(database);
        String line = null;
        String last = "@";
        Dictionary<String, ICommand> dict = null;
        if(data != null){
            Collections.sort(data);
            for (int i2 = 0; i2 < data.size(); i2++)
            {
                line = data.get(i2);
                String[] s = line.split(" ");
                if(!last.equalsIgnoreCase(s[0])){
                    dict = getDict(s[0]);
                    last = s[0];
                }
                String cmd2 = "";
                for(int i = 2; i < s.length; i++)
                    cmd2 += (i == 2?"":" ") + s[i];
                dict.put(s[1], new ICommand(cmd2));
            }
        }
    }
    int per = 1;
    int keybindings = 1;
    int permissions = 0;
    void readPref()
    {
        ArrayList<String> data = readData(properties);
        if(data == null){
            savePref();
            data = readData(properties);}
        String line = null;
        for(int i = 0; i < data.size(); i++)
        {
            line = data.get(i);
            int e = line.indexOf("#");
            if(e != -1)
                line = line.substring(0, e);
            String[] p = line.split("=");
            if(p.length == 2)
            {
                String name = p[0].trim();
                String value = p[1].trim();
                if(name.equalsIgnoreCase("per"))
                {
                    if(value.equalsIgnoreCase("player"))
                        per = 1;
                    else if(value.equalsIgnoreCase("global"))
                        per = 0;
                    else if(value.equalsIgnoreCase("group"))
                        per = 2;
                    else
                        System.out.println("Error in ItemCommands.properties with per on line #" + i);
                }
                else if(name.equalsIgnoreCase("permissionType"))
                {
                    if(value.equalsIgnoreCase("plugin")){
                        permissions = 0;}
                    else if(value.equalsIgnoreCase("basic")){
                        permissions = 1;}
                    else
                        System.out.println("Error in ItemCommands.properties with permissionType on line #" + i);
                }
                else if(name.equalsIgnoreCase("bindto"))
                {
                    if(value.equalsIgnoreCase("item")){
                        keybindings = 0;}
                    else if(value.equalsIgnoreCase("slot")){
                        keybindings = 1;}
                    else
                        System.out.println("Error in ItemCommands.properties with bindto on line #" + i);
                }
                else if(name.equalsIgnoreCase("click"))
                {
                    if(value.equalsIgnoreCase("right")){
                        bclick = 0;}
                    else if(value.equalsIgnoreCase("left")){
                        bclick = 1;}
                    else
                        System.out.println("Error in ItemCommands.properties with click on line #" + i);
                }
                else if(name.equalsIgnoreCase("createNeedOP"))
                {
                    if(value.equalsIgnoreCase("true"))
                        createNeedOP = true;
                    else
                        createNeedOP = false;
                }
                else if(name.equalsIgnoreCase("useNeedOP"))
                {
                    if(value.equalsIgnoreCase("true"))
                        useNeedOP = true;
                    else
                        useNeedOP = false;
                }
                else if(name.equalsIgnoreCase("adminNeedOP"))
                {
                    if(value.equalsIgnoreCase("true"))
                        adminNeedOP = true;
                    else
                        adminNeedOP = false;
                }
            }
        }
    }
    void savePref()
    {
        ArrayList<String> data = new ArrayList<String>();
        data.add("per=" + (per == 1 ? "player" : "global") + " #global OR player");
        data.add("permissionType=" + (permissions == 0 ? "plugin" : "basic") + " #plugin OR basic");
        data.add("bindto=" + (keybindings == 0 ? "item" : "slot") + " #slot OR item");
        data.add("click=" + (bclick == 0 ? "right" : "left") + " #right OR left");
        data.add("#For basic permission use only(No Plugin)");
        data.add("useNeedOP="+String.valueOf(useNeedOP));
        data.add("createNeedOP="+String.valueOf(createNeedOP));
        data.add("adminNeedOP="+String.valueOf(adminNeedOP));
        saveData(data, properties);
    }

    protected boolean checkPermissions(Player player, String node, boolean needOp)
    {
        if (ItemCommands.Permissions == null || this.permissions == 1) {
            return (player.isOp() && needOp) || !needOp;
        }
        else {
            return ItemCommands.Permissions.has(player, node);
        }
    }
    public String rspace(String s, char n, int len)
    {
        for(int i = s.length(); i < len; i++)
            s = s + n;
        return s;
    }
    public String lspace(String s, char n, int len)
    {
        for(int i = s.length(); i < len; i++)
            s = n + s;
        return s;
    }
    void showHelp(Player player){
        int i = 0;
        Dictionary<String, ICommand> dict = getDict(player);
        int bindto = dict.get("bindto").click;
        String sbindto = bindto == 0 ? "Item" : "Slot";
        String key = "";
        if(bindto == 1)
            key = String.valueOf(player.getInventory().getHeldItemSlot());
        else
            key = String.valueOf(player.getItemInHand().getTypeId()) + ":" + String.valueOf(player.getItemInHand().getDurability());
        ICommand cmd = dict.get(key);
        player.sendMessage(ChatColor.GOLD + "Item Commands");
        player.sendMessage(infoc + "Commands are set "+(this.per == 1 ? "per player" : "globally") + " and Binded to "+sbindto+"s");
        if(cmd != null)
        {
            String a = (cmd.click == 0 ? "right" : "left");
            player.sendMessage(infoc + "Current Command: "+descc+cmd.cmd);
            player.sendMessage(infoc + "Triggered by a " + a + " click, " + a + " click events are "+(cmd.clickevent == 1 ? "enabled" : "disable"));
        }
        if(checkPermissions(player, "ICmds.create", createNeedOP)){
            player.sendMessage(cmdc + "Usage: /icmd add [command] "+descc+"#Add command to the selected "+sbindto);
            player.sendMessage(cmdc + "/icmd remove "+descc+"#Remove command from the selected "+sbindto);
            player.sendMessage(cmdc + "/icmd click [left l|right r] "+descc+"#Change trigger of the selected "+sbindto);
            player.sendMessage(cmdc + "/icmd clickevent [on t| off f] "+descc+"#Change whether the normal click functionality is performed");
            i++;
        }
        if((per == 0 && checkPermissions(player, "ICmds.create", createNeedOP)) || (per == 1 && checkPermissions(player, "ICmds.use", useNeedOP))) {
            player.sendMessage(cmdc + "/icmd bindto [item i|slot s] "+descc+"#Binds commands to slots or items");
            i++;}
        if(checkPermissions(player, "ICmds.use", useNeedOP)){
            player.sendMessage(cmdc + "/icmd list "+descc+"#List commands available to you");
            i++;}
        if(checkPermissions(player, "ICmds.admin", adminNeedOP)){
            player.sendMessage(cmdc + "/icmd per [player pl|global gl] "+descc+"#Set commands globally or per player");
            player.sendMessage(cmdc + "/icmd reload");
            i++;}
        if(i == 0)
            player.sendMessage(cmdc + "No Permissions to use ItemCommands");
    }
    Dictionary<String, ICommand> getDict(Player player)
    {
        Dictionary<String, ICommand> dict = null;
        String i = this.per == 1 ? player.getName() : "";
        if((dict=players.get(i)) == null){
            players.put(i, dict=new Hashtable<String, ICommand>());
            dict.put("bindto", new ICommand("", keybindings, 0));
            dict.put("click", new ICommand("", bclick, 0));
        }
        return dict;
    }
    Dictionary<String, ICommand> getDict(String player)
    {
        Dictionary<String, ICommand> dict = null;
        String i = this.per == 1 ? player : "";
        if((dict=players.get(i)) == null){
            players.put(i, dict=new Hashtable<String, ICommand>());
            dict.put("bindto", new ICommand("", keybindings, 0));
            dict.put("click", new ICommand("", bclick, 0));
        }
        return dict;
    }
}