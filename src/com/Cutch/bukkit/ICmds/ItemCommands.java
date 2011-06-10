package com.Cutch.bukkit.ICmds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import me.taylorkelly.help.Help;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
public class ItemCommands extends JavaPlugin {
    public boolean createNeedOP = true;
    public boolean useNeedOP = false;
    public boolean globalNeedOP = true;
    public boolean adminNeedOP = true;
    public int superNeedOP = -1;
    public int superGlobalNeedOP = 0;
    public int freeNeedOP = -1;
    ChatColor cmdc = ChatColor.BLUE;
    ChatColor descc = ChatColor.AQUA;
    ChatColor errc = ChatColor.RED;
    ChatColor infoc = ChatColor.YELLOW;
    String properties = "ItemCommands.properties";
    String blacknwhitelist = "BlacknWhitelist.db";
    String database = "Commands.db";
    public iConomySupport ics = null;
    PermissionSupport pms = null;
    public int iConomyA = 0;
    int permissionsType = 0;
    private PlayerEvents playerListener;
    int bclick = 0;
    int keybindings = 1;
    Double version = null;
    boolean update = true;
    public Dictionary<String, Dictionary<String, ICommands>> players=null;
    public Dictionary<String, ArrayList<Item>> allow=null;
    public Dictionary<String, ArrayList<Item>> deny=null;

    public void onDisable() {
        System.out.println("ItemCommands is Disabled");
    }

    public void onEnable() {
        PluginDescriptionFile desc = this.getDescription();
        players = new Hashtable<String, Dictionary<String, ICommands>>();
        allow = new Hashtable<String, ArrayList<Item>>();
        deny = new Hashtable<String, ArrayList<Item>>();
        version = null;
        readPref();
        try
        {
            pms = new PermissionSupport(this);
            permissionsType = pms.setupPermissions(permissionsType);
        }catch(NoClassDefFoundError e){
            permissionsType=1;
            System.out.println("ItemCommands: Permission system not detected. Using Basic Permissions.");
        }
        readDB(true);
        readBlacknWhite();
        setupiConomy();
        setupHelp();
        PluginManager plmgr = getServer().getPluginManager();
        playerListener = new PlayerEvents(this);
        ics = new iConomySupport(this) {};
        plmgr.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Highest, this);
        System.out.println("ItemCommands: v" + desc.getVersion() + " is Enabled");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd1, String commandLabel, String[] args) {
        String cmdmsg = cmd1.getName();
        ICPlayer player = null;
        String splayer = "";
        if(sender instanceof Player)
        {
            player = new ICPlayer((Player)sender) { };
            splayer = player.getName();
        }
        if(cmdmsg.equalsIgnoreCase("icmd"))
        {
            if(args.length >= 1)
            {
                if(args[0].equalsIgnoreCase("add"))
                {
                    if(checkPermissions(player, "ICmds.create", createNeedOP))
                    {
                        if(args.length >= 2)
                        {
                            String key = "";
                            int click = 0;
                            int clickevent = 0;
                            int per = 1;
                            int bindto = -1;
                            int end = 0;
                            int cycle = 0;
                            double cooldown = 0;
                            ArrayList<Item> consumes = new ArrayList<Item>();
                            int i;
                            for(i = 1; i < args.length-1; i++)
                            {
                                if(args[i].equalsIgnoreCase("-s"))
                                    bindto = 1;
                                else if(args[i].equalsIgnoreCase("-i"))
                                    bindto = 0;
                                else if(args[i].equalsIgnoreCase("-d"))
                                    cooldown = parseTime(args[++i]);
                                else if(args[i].equalsIgnoreCase("-r"))
                                    click = 0;
                                else if(args[i].equalsIgnoreCase("-l"))
                                    click = 1;
                                else if(args[i].equalsIgnoreCase("-e"))
                                    clickevent = 1;
                                else if(args[i].equalsIgnoreCase("-al"))
                                    cycle = 0;
                                else if(args[i].equalsIgnoreCase("-sh"))
                                    cycle = 1;
                                else if(args[i].equalsIgnoreCase("-cy"))
                                    cycle = 2;
                                else if(args[i].equalsIgnoreCase("-ra"))
                                    cycle = 3;
                                else if(args[i].equalsIgnoreCase("-c"))
                                {
                                    String[] cs = args[++i].split(";");
                                    for(String c : cs)
                                    {
                                        Item item = new Item(c);
                                        consumes.add(item);
                                    }
                                }
                                else if(args[i].equalsIgnoreCase("-g"))
                                    per = 0;
                                else if(args[i].equalsIgnoreCase("-t"))
                                {
                                    key = args[++i];
                                }
                                else
                                {
                                    break;
                                }
                            }
                            end = i;
                            if(per == 0 && !(checkPermissions(player, "ICmds.global", globalNeedOP)||checkPermissions(player, "ICmds.admin", globalNeedOP)))
                                sendMessage(player, errc + "You do not have the required permissions for global assignments.");
                            else
                            {
                                if(player == null && key.isEmpty())
                                {
                                    sendMessage(player, cmdc + "Usage: /icmd add [-t typeid] [-i item|-s slot] <flag> [command] "+descc+"#Add command globally");
                                    return true;
                                }
                                if(bindto == -1)
                                {
                                    sendMessage(player, cmdc + "Need at least a -s slot or -i item flag");
                                    return true;
                                }
                                if(key.isEmpty()){
                                    if(bindto == 1)
                                        key = String.valueOf(player.getInventory().getHeldItemSlot()+1);
                                    else
                                        key = String.valueOf(player.getItemInHand().getTypeId()) + ":" + String.valueOf(player.getItemInHand().getDurability());
                                }
                                else
                                {
                                    if(bindto == 1)
                                    {
                                        if(key.contains(":"))
                                        {
                                            sendMessage(player, cmdc + "Item Given instead of slot");
                                            return true;
                                        }
                                        int slot = Integer.parseInt(key);
                                        if(slot < 1 || slot > 9)
                                        {
                                            sendMessage(player, cmdc + "Slot # must be between 1-9");
                                            return true;
                                        }
                                    } else {
                                        if(!key.contains(":"))
                                            key += ":0";
                                    }
                                }
                                String a = "";
                                for(i = end; i < args.length; i++)
                                    a += (i == end?"":" ") + args[i];
                                if(per == 0)
                                    splayer = "";
                                int id = findNextID(splayer);
                                ICommand cmd = new ICommand(splayer, key, id, a, click, clickevent, cooldown, consumes, this);
                                Item item = new Item(key);
                                boolean listed = isListed(cmd, item, bindto == 1);
                                if(listed)
                                {
                                    cmd.parent = putDict(splayer, key, cycle, cmd);
                                    String pcmd = cmd.cmd;
                                    pcmd = pcmd.replaceFirst(":0", "");
                                    sendMessage(player, cmdc + "ID: " + id + " Command: " + pcmd + " added to "+(bindto == 0 ? "item" : "slot") + " " + key + " for " + (!splayer.isEmpty()?splayer:"Everyone"));
                                    saveDB();
                                }
                                else
                                    player.sendMessage(errc+"Using command \"" + cmd.getRunningCommand() + "\" with "+(cmd.bindto==0?Material.getMaterial(item.id):"slot "+cmd.key)+" is forbidden.");
                            }
                        }
                        else
                            sendMessage(player, cmdc + "Usage: /icmd add [-i item|-s slot] <flags> [command] "+descc+"#Add command to the selected item");
                    }
                    else
                        sendMessage(player, errc + "You do not have the required permissions for this.");
                }
                else if(args[0].equalsIgnoreCase("remove"))
                {
                    if(checkPermissions(player, "ICmds.create", createNeedOP))
                    {
                        if(args.length >= 2){
                            int per = 1;
                            int i;
                            for(i = 2; i < args.length; i+=1)
                            {
                                if(args[i].equalsIgnoreCase("-g"))
                                    per = 0;
                                else
                                {
                                    break;
                                }
                            }
                            int id = -1;
                            try{
                                id = Integer.parseInt(args[1]);
                            }catch(NumberFormatException e)
                            {
                                sendMessage(player, errc + "Argument 2 expects an ID (Remove is Arg 1)");
                            }
                            if(id != -1){
                                
                                ICommands cmds = getICmdsByID(per == 0 ? "" : splayer, id);
                                ICommand cmd = null;
                                if(cmds == null || (cmd = cmds.remove(id)) == null)
                                {
                                    sendMessage(player, cmdc + "Command not found.");
                                } else {
                                    sendMessage(player, cmdc + "Command: "+ cmd.cmd +" removed from "+(cmd.clickevent == 0 ? "item" : "slot")+" " + cmd.key + " for " + (!cmd.isGlobal()?player.getName():"Everyone"));
                                    saveDB();
                                }
                            }
                        }
                        else
                            sendMessage(player, cmdc + "Usage: /icmd remove [id] "+descc+"#Remove command by ID");
                    }
                    else
                        sendMessage(player, errc + "You do not have the required permissions for this.");
                }
                else if(args[0].equalsIgnoreCase("bind"))
                {
                    int option = 0;
                    if(args.length > 1)
                    {
                        if(args[1].equalsIgnoreCase("add"))
                            option = 1;
                        else if(args[1].equalsIgnoreCase("set"))
                            option = 2;
                        else if(args[1].equalsIgnoreCase("remove"))
                            option = 3;
                        else if(args[1].equalsIgnoreCase("list"))
                            option = 4;
                    }
                    bind(player, option, args);
                }
                else if(args[0].equalsIgnoreCase("swap"))
                {
                    if(checkPermissions(player, "ICmds.create", createNeedOP))
                    {
                        if(args.length >= 3){
                            int per = 1;
                            int i;
                            for(i = 3; i < args.length; i+=1)
                            {
                                if(args[i].equalsIgnoreCase("-g"))
                                    per = 0;
                                else
                                {
                                    break;
                                }
                            }
                            int id = -1;
                            int id2 = -1;
                            try
                            {
                                id = Integer.parseInt(args[1]);
                            }catch(NumberFormatException e)
                            {
                                sendMessage(player, errc + "Argument 2 expects an ID (Swap is Arg 1 :p)");
                            }
                            try
                            {
                                id2 = Integer.parseInt(args[2]);
                            }catch(NumberFormatException e)
                            {
                                sendMessage(player, errc + "Argument 3 expects an ID (Swap is Arg 1 :p)");
                            }
                            if(id != -1 && id2 != -1)
                            {
                                ICommands cmds = getICmdsByID(per == 0 ? "" : splayer, id);
                                if(cmds != null)
                                {
                                    ICommand cmd = cmds.findByID(id);
                                    ICommand cmd2 = cmds.findByID(id2);
                                    if(cmd2 != null)
                                    {
                                        cmd.id = id2;
                                        cmd2.id = id;
                                        cmds.putDict(cmd);
                                        cmds.putDict(cmd2);
                                        saveDB();
                                    } else
                                        sendMessage(player, errc + "Id #2 "+id2+" was not found");
                                } else
                                    sendMessage(player, errc + "Id #1 "+id+" was not found");
                            }
                        }
                        else
                            sendMessage(player, cmdc + "Usage: /icmd swap [id #1] [id #2] "+descc+"#Swap commands by ID");
                    }
                    else
                       sendMessage(player, errc + "You do not have the required permissions for this.");
                }
                else if(args[0].equalsIgnoreCase("change"))
                {
                    if(args.length >= 2)
                    {
                        if(checkPermissions(player, "ICmds.create", createNeedOP))
                        {
                            int id = -1;
                            try
                            {
                                id = Integer.parseInt(args[1]);
                            }catch(NumberFormatException e)
                            {
                                sendMessage(player, errc + "Argument 2 expects an ID (Change is Arg 1)");
                            }
                            if(id != -1)
                            {
                                String key = "";
                                int click = -1;
                                int clickevent = -1;
                                int bindto = -1;
                                int per = 1;
                                int end = 0;
                                double cooldown = -1;
                                int cycle = -1;
                                ArrayList<Item> consumes = new ArrayList<Item>();
                                int i;
                                for(i = 2; i < args.length; i+=1)
                                {
                                    if(args[i].equalsIgnoreCase("-s"))
                                        bindto = 1;
                                    else if(args[i].equalsIgnoreCase("-i"))
                                        bindto = 0;
                                    else if(args[i].equalsIgnoreCase("-d"))
                                        cooldown = parseTime(args[++i]);
                                    else if(args[i].equalsIgnoreCase("-r"))
                                        click = 0;
                                    else if(args[i].equalsIgnoreCase("-l"))
                                        click = 1;
                                    else if(args[i].equalsIgnoreCase("-e"))
                                        clickevent = 1;
                                    else if(args[i].equalsIgnoreCase("-al"))
                                        cycle = 0;
                                    else if(args[i].equalsIgnoreCase("-sh"))
                                        cycle = 1;
                                    else if(args[i].equalsIgnoreCase("-cy"))
                                        cycle = 2;
                                    else if(args[i].equalsIgnoreCase("-ra"))
                                        cycle = 3;
                                    else if(args[i].equalsIgnoreCase("-c"))
                                    {
                                        String[] cs = args[++i].split(";");
                                        for(String c : cs)
                                        {
                                            Item item = new Item(c);
                                            consumes.add(item);
                                        }
                                    }
                                    else if(args[i].equalsIgnoreCase("-g"))
                                        per = 0;
                                    else if(args[i].equalsIgnoreCase("-t"))
                                    {
                                        key = args[++i];
                                    }
                                    else
                                    {
                                        break;
                                    }
                                }
                                end = i;

                                if(player == null && key.isEmpty())
                                {
                                    sendMessage(player, cmdc + "Usage: /icmd change [id] [-t typeid] <flags> <command> "+descc+"#Change flags by id");
                                    return true;
                                }
                                if(per == 0)
                                    splayer = "";
                                ICommands cmds = getICmdsByID(splayer, id);
                                if(per == 0 && !(checkPermissions(player, "ICmds.global", globalNeedOP)||checkPermissions(player, "ICmds.admin", globalNeedOP)))
                                    sendMessage(player, errc + "You do not have the required permissions for global assignments.");
                                else if(cmds != null)
                                {
                                    ICommand cmd = cmds.findByID(id);
                                    if(key.isEmpty())
                                        key = cmd.key;
                                    if(click == -1)
                                        click = cmd.click;
                                    if(clickevent == -1)
                                        clickevent = cmd.clickevent;
                                    if(cooldown == -1)
                                        cooldown = cmd.cooldown;
                                    if(cycle == -1)
                                        cycle = cmds.cycle;
                                    if(bindto == -1)
                                        bindto = cmd.bindto;
                                    else
                                    {
                                        String[] keys = new String[2];
                                        ItemStack is = player.getItemInHand();
                                        keys[0] = String.valueOf(is.getTypeId()) + ":" + String.valueOf(is.getDurability());
                                        keys[1] = String.valueOf(player.getInventory().getHeldItemSlot()+1);
                                        cmd.key = keys[bindto];
                                        cmds.remove(id);
                                        putDict(splayer, key, cycle, cmd);
                                    }
                                    if(per == -1)
                                        per = cmd.global;
                                    String a = "";
                                    for(i = end; i < args.length; i++)
                                        a += (i == end?"":" ") + args[i];
                                    Item item = new Item(key);
                                    boolean listed = isListed(cmd, item, bindto == 1);
                                    if(listed)
                                    {
                                        cmd.click=click;
                                        if(!a.isEmpty())
                                            cmd.cmd = a;
                                        cmd.consume = consumes;
                                        cmd.clickevent = clickevent;
                                        cmd.cooldown = cooldown;
                                        cmds.cycle = cycle;
                                        putDict(splayer, key, cycle, cmd);
                                        saveDB();
                                    }
                                    else
                                        player.sendMessage(errc+"Using command \"" + cmd.getRunningCommand() + "\" with "+(cmd.bindto==0?Material.getMaterial(item.id):"slot "+cmd.key)+" is forbidden.");
                                }
                                else
                                    sendMessage(player, errc + "ID " + args[1] + " not valid.");
                            }
                        }
                        else
                            sendMessage(player, errc + "You do not have the required permissions for this.");
                    }
                    else
                        sendMessage(player, cmdc + "Usage: /icmd change [id] <flags> <command> "+descc+"#Change flags by id");
                }
                else if(args[0].equalsIgnoreCase("list"))
                {
                    if(checkPermissions(player, "ICmds.use", useNeedOP))
                    {
                        int fper = 1;
                        int fbindto = -1;
                        int page = 1;
                        for(int i = 1; i < args.length; i+=1)
                        {
                            if(args[i].equalsIgnoreCase("-s"))
                                fbindto = 1;
                            else if(args[i].equalsIgnoreCase("-i"))
                                fbindto = 0;
                            else if(args[i].equalsIgnoreCase("-g"))
                                fper = 0;
                            else
                            {
                                try{
                                    page = Integer.parseInt(args[i]);
                                }
                                catch(Exception e){}
                            }
                        }
                        String str = "Commands (" + (fper == 0 || splayer.isEmpty() ? "Global" : splayer) + ")";
                        sendMessage(player, infoc + "  ID     Item/Slot     Click Trigger     " + str);
                        sendMessage(player, infoc + "---------------------------------------------------");
                        Dictionary<String, ICommands> get = players.get(fper == 1 ? splayer : "");
                        if(get != null)
                        {
                            Enumeration<ICommands> elements = get.elements();
                            List<Integer> iDList = new ArrayList<Integer>();
                            while(elements.hasMoreElements())
                            {
                                ICommands cmds = elements.nextElement();
                                Integer[] iDs = cmds.getIDList();
                                iDList.addAll(Arrays.asList(iDs));
//                                for (Integer id : iDs)
//                                {
//                                    ICommand cmd = cmds.findByID(id);
//                                    if(fbindto == -1 || cmd.bindto == fbindto)
//                                    {
//                                        String click = (cmd.click == 1 ? "Left" : "Right");
//                                        sendMessage(player, (linei%2==0 ? cmdc : descc) + lspace(String.valueOf(cmd.id)," ",4) + "     " + lspace(cmd.key.replaceAll(":0", "")," ",9) + lspace(click," ",20) + "     "+ cmd.cmd);
//                                        linei++;
//                                    }
//                                }
                            }
                            ICommands cmds = null;
//                            Integer[] toArray = (Integer[])iDList.toArray();
                            int maxpage = (iDList.size() / 12)+1;
                            page = Math.max(Math.min(page, (iDList.size() / 12)+1), 1);
                            int max = Math.min((page)*12, iDList.size());
                            for (int i = (page-1)*12; i < max; i++)
                            {
                                if(cmds == null)
                                    cmds = getICmdsByID(splayer, iDList.get(i));
                                ICommand cmd = cmds.findByID(iDList.get(i));
                                if(cmd == null){
                                    cmds = getICmdsByID(splayer, iDList.get(i));
                                    cmd = cmds.findByID(iDList.get(i));
                                }
                                if(fbindto == -1 || cmd.bindto == fbindto)
                                {
                                    String click = (cmd.click == 1 ? "Left" : "Right");
                                    sendMessage(player, (i%2==0 ? cmdc : descc) + lspace(String.valueOf(cmd.id)," ",4) + "     " + lspace(cmd.key.replaceAll(":0", "")," ",9) + lspace(click," ",20) + "     "+ cmd.cmd);
                                }
                            }
                            sendMessage(player, "Page "+String.valueOf(page)+" of "+String.valueOf(maxpage));
                        }
                    }
                    else
                        sendMessage(player, errc + "You do not have the required permissions for this.");
                }
                else if(args[0].equalsIgnoreCase("reload"))
                {
                    if(checkPermissions(player, "ICmds.admin", adminNeedOP))
                    {
                        this.onDisable();
                        this.onEnable();
                        sendMessage(player, ChatColor.RED+"ItemCommands Has been Reloaded");
                    }
                    else
                        sendMessage(player, errc + "You do not have the required permissions for this.");
                }
                else if(args[0].equalsIgnoreCase("flags"))
                {
                    showFlags(player);
                }
                else
                    showHelp(player);
            }
            else
                showHelp(player);
            return true;
        }
        return false;
    }
    private void setupiConomy() {
        Plugin test = this.getServer().getPluginManager().getPlugin("iConomy");
        if(iConomyA == -1)
        {
            iConomyA = 0;
            System.out.println("ItemCommands: iConomy Support Disabled");
        }
        else if(test != null) {
            iConomyA = 1;
            System.out.println("ItemCommands: Using iConomy Plugin v" + test.getDescription().getVersion());
        } else {
            iConomyA = 0;
            System.out.println("ItemCommands: iConomy Support Disabled");
        }
    }
    private void setupHelp()
    {
        Plugin test = this.getServer().getPluginManager().getPlugin("Help");
        if (test != null) {
            String[] permissions = new String[]{"ICmds.create", "ICmds.use", "ICmds.admin"};
            Help help = ((Help)test);
            help.registerCommand("icmd", "Help for Item Commands", this, true, permissions);
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
    ArrayList<String> readData(String file, boolean printerror)
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
            if(printerror)
                System.out.println(this.getDataFolder() + File.separator + file + " Not Found");
            data = null; }
        catch (IOException e) {
            if(printerror)
                System.out.println(this.getDataFolder() + File.separator + file + " Could not be open");
            data = null; }
        return data;
    }
    void saveDB()
    {
        if(update) {
            ArrayList<String> data = new ArrayList<String>();
            Enumeration<String> dict = players.keys();
            for (;dict.hasMoreElements();)
            {
                String i = dict.nextElement();
                Dictionary<String, ICommands> lines = players.get(i);
                Enumeration<String> dict2 = lines.keys();

                for (;dict2.hasMoreElements();)
                {
                    String i2 = dict2.nextElement();
                    Enumeration<ICommand> elements = lines.get(i2).cmds.elements();
                    for(;elements.hasMoreElements();)
                        data.add(i.replaceAll(" ", "&+") + " " + i2 + " " + elements.nextElement().toString());
                }
            }
            saveData(data, database);
        }
    }
    void readDB(boolean update)
    {
        ArrayList<String> data = readData(database, true);
        String line = null;
        if(data != null){
            Collections.sort(data);
            for (int i2 = 0; i2 < data.size(); i2++)
            {
                line = data.get(i2);
                int e = line.indexOf("#");
                if(e != -1)
                    line = line.substring(0, e);
                if(update)
                    if(getVersion(line))
                        break;
                if(!line.trim().isEmpty())
                {
                    String[] s = line.split(" ");
                    String cmd2 = "";
                    for(int i = 2; i < s.length; i++)
                        cmd2 += (i == 2?"":" ") + s[i];
                    String name = s[0].replaceAll("&+", " ");
                    try
                    {
                        int cycle = Character.getNumericValue(cmd2.charAt(12));
                        ICommand icmd = null;
                        ICommands putDict = this.putDict(name, s[1], cycle, icmd = new ICommand(name, s[1], cmd2, this));
                        icmd.parent = putDict;
                    }
                    catch(Exception e2)
                    {
                        System.out.println("Line "+i2+" could not be parsed");
                    }
                }
            }
        }
    }
    void readPref()
    {
        superNeedOP = -1;
        superGlobalNeedOP = -1;
        freeNeedOP = -1;
        ArrayList<String> data = readData(properties, true);
        if(data == null){
            savePref();
            data = readData(properties, false);}
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
                if(name.equalsIgnoreCase("version"))
                {
                    try{
                        version=Double.parseDouble(value);}
                    catch(NumberFormatException ew){
                        System.out.println("Error in ItemCommands.properties version should be in the format x.xx but with numbers...");
                    }
                }
                else if(name.equalsIgnoreCase("UpdateDB"))
                {
                    if(value.equalsIgnoreCase("true"))
                        update = true;
                    else
                        update = false;
                }
                else if(name.equalsIgnoreCase("permissionType"))
                {
                    if(value.equalsIgnoreCase("plugin")){
                        permissionsType = 0;}
                    else if(value.equalsIgnoreCase("basic")){
                        permissionsType = 1;}
                    else
                        System.out.println("Error in ItemCommands.properties with permissionType on line #" + i);
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
                else if(name.equalsIgnoreCase("globalNeedOP"))
                {
                    if(value.equalsIgnoreCase("true"))
                        globalNeedOP = true;
                    else
                        globalNeedOP = false;
                }
                else if(name.equalsIgnoreCase("adminNeedOP"))
                {
                    if(value.equalsIgnoreCase("true"))
                        adminNeedOP = true;
                    else
                        adminNeedOP = false;
                }
                else if(name.equalsIgnoreCase("superNeedOP"))
                {
                    if(value.equalsIgnoreCase("true"))
                        superNeedOP = 1;
                    else
                        superNeedOP = 0;
                }
                else if(name.equalsIgnoreCase("superGlobalNeedOP"))
                {
                    if(value.equalsIgnoreCase("true"))
                        superGlobalNeedOP = 1;
                    else
                        superGlobalNeedOP = 0;
                }
                else if(name.equalsIgnoreCase("freeNeedOP"))
                {
                    if(value.equalsIgnoreCase("true"))
                        freeNeedOP = 1;
                    else
                        freeNeedOP = 0;
                }
            }
        }
    }
    void savePref()
    {
        if(update) {
            ArrayList<String> data = new ArrayList<String>();
            data.add("Version="+this.getDescription().getVersion()+" #This is used to update the database and properties files");
            data.add("UpdateDB="+String.valueOf(update)+" #Setting this to false will disable version updates for the database, it will be usable but changes wont be saved");
            data.add("PermissionType=" + (permissionsType == 0 ? "plugin" : "basic") + " # Plugin OR Basic Permissions");
            data.add("#For basic permission use only(No Plugin)");
            data.add("UseNeedOP="+String.valueOf(useNeedOP));
            data.add("CreateNeedOP="+String.valueOf(createNeedOP));
            data.add("GlobalNeedOP="+String.valueOf(globalNeedOP));
            data.add("AdminNeedOP="+String.valueOf(adminNeedOP));
            data.add("#These next lines can be commented out with a # to disable the permission");
            data.add((superNeedOP == -1 ? "#":"")+"superNeedOP="+String.valueOf(!(superNeedOP==0)));
            data.add((superGlobalNeedOP == -1 ? "#":"")+"superGlobalNeedOP="+String.valueOf(superGlobalNeedOP==1));
            data.add((freeNeedOP == -1 ? "#":"")+"freeNeedOP="+String.valueOf(!(freeNeedOP==0)));
            saveData(data, properties);
        }
    }
    void readBlacknWhite()
    {
        ArrayList<String> data = readData(blacknwhitelist, true);
        if(data == null){
            data = new ArrayList();
            saveData(data, blacknwhitelist);}
        for(int i = 0; i < data.size(); i++)
        {
            String get = data.get(i);
            boolean allow = get.startsWith("a");
            get = get.substring(1);
            boolean slot = get.contains("s|");
            String[] s;
            if(slot)
                s = get.split("s\\|");
            else
                s = get.split("i\\|");
            String[] sids = s[0].split(";");
            ArrayList<Item> ids = new ArrayList<Item>();
            for(int i2 = 0; i2 < sids.length; i2++)
                if(!sids[i2].trim().isEmpty())
                    ids.add(new Item(sids[i2]));
            if(allow)
                this.allow.put((slot?"s":"i")+s[1], ids);
            else
                this.deny.put((slot?"s":"i")+s[1], ids);
        }
    }
    void saveBlacknWhite()
    {
        ArrayList<String> data = new ArrayList<String>();
        Enumeration<String> keys = allow.keys();
        while(keys.hasMoreElements())
        {
            String nextElement = keys.nextElement();
            String bind = nextElement.substring(0, 1);
            data.add("a"+listToString(allow.get(nextElement).toArray(), "")+bind+"|"+nextElement.substring(1));
        }
        keys = deny.keys();
        while(keys.hasMoreElements())
        {
            String nextElement = keys.nextElement();
            String bind = nextElement.substring(0, 1);
            data.add("d"+listToString(deny.get(nextElement).toArray(), "")+bind+"|"+nextElement.substring(1));
        }
        saveData(data, blacknwhitelist);
    }
    protected boolean checkPermissions(Player player, String node, boolean needOp)
    {
        if(player == null)
            return true;
        else if(permissionsType == 1) {
            return (player.isOp() && needOp) || !needOp;
        }
        else {
            return pms.has(player, node);
        }
    }
    protected boolean checkPermissions(Player player, String node, int needOp)
    {
        if(player == null)
            return true;
        else if(permissionsType == 1) {
            if(needOp < 0)
                return false;
            return (player.isOp() && needOp==1) || needOp==0;
        }
        else {
            return pms.has(player, node);
        }
    }
    public String rspace(String s, String n, int len)
    {
        for(int i = s.length(); i < len; i++)
            s = s + n;
        return s;
    }
    public String lspace(String s, String n, int len)
    {
        for(int i = s.length(); i < len; i++)
            s = n + s;
        return s;
    }
    void showHelp(Player player){
        sendMessage(player, errc + "[] is required, <> is optional");
        int i = 0;
        if(checkPermissions(player, "ICmds.create", createNeedOP)) {
            sendMessage(player, cmdc + "Usage: /icmd add [-i item|-s slot] <flags> [command] "+descc+"#Add command");
            sendMessage(player, cmdc + "/icmd remove [id] "+descc+"#Remove command by ID");
            sendMessage(player, cmdc + "/icmd change [id] <flags> <command> "+descc+"#Change properties of IDs command");
            sendMessage(player, cmdc + "/icmd swap [id #1] [id #2] "+descc+"#Swap commands by ID");
            sendMessage(player, cmdc + "/icmd flags "+descc+"#Shows flags and their usage");
            i++;}
        if(checkPermissions(player, "ICmds.use", useNeedOP)){
            sendMessage(player, cmdc + "/icmd list <-i item|-s slot> <-g global> "+descc+"#List commands available");
            i++;}
        if(checkPermissions(player, "ICmds.admin", adminNeedOP)){
            sendMessage(player, cmdc + "/icmd bind "+descc+"#Allow or Block Commands");
            sendMessage(player, cmdc + "/icmd reload");
            i++;}
        if(i == 0)
            sendMessage(player, cmdc + "No Permissions to use ItemCommands");
    }
    void showFlags(Player player){
        int i = 0;
        if(checkPermissions(player, "ICmds.create", createNeedOP)) {
            sendMessage(player, cmdc + "-g "+descc+"Add / Change / Swap / Remove / List globally");
            sendMessage(player, cmdc + "-l "+descc+"Left Click / -r Right Click");
            sendMessage(player, cmdc + "-e "+descc+"Enable Normal Click Events");
            sendMessage(player, cmdc + "-d [#] "+descc+"Specifies a Cooldown for the command, Max is around 27h");
            sendMessage(player, descc + "# Can be in the format #s, #m, #h, just a # defaults as seconds");
            sendMessage(player, cmdc + "-c [list] "+descc+"Consumables");
            sendMessage(player, descc + "list = id<:damage>+/-amount;... or $+/-amount;... or *-amount;...");
            sendMessage(player, cmdc + "-al (all) "+descc+"#Runs all commands");
            sendMessage(player, cmdc + "-cy (cycle) "+descc+"#Runs one command after another in order");
            sendMessage(player, cmdc + "-ra (random) "+descc+"#Runs one random command");
            sendMessage(player, cmdc + "-sh (shuffle) "+descc+"#Runs one random command but runs them the same amount of times");
            i++;}
        if(checkPermissions(player, "ICmds.admin", adminNeedOP)){
            sendMessage(player, cmdc + "-t [id] "+descc+"Only used from Console to specify a slot 1-9 or Item #");
            i++;}
        if(i == 0)
            sendMessage(player, cmdc + "No Permissions to use ItemCommands");
    }
    public Dictionary<String, ICommands> getDict(String player, String key)
    {
        ICommands cmd = null;
        Dictionary<String, ICommands> dict = null;
        if((dict=players.get(player)) == null){
            players.put(player, dict=new Hashtable<String, ICommands>());
        }
        cmd = dict.get(key);
        if(cmd == null)
        {
            cmd=new ICommands(this);
            cmd.click = bclick;
            cmd.bindto = keybindings;
            dict.put(key, cmd);
        }
        return dict;
    }
    public ICommands findByKey(String player, String key)
    {
        ICommands cmd = null;
        Dictionary<String, ICommands> dict = null;
        if((dict=players.get(player)) == null){
            players.put(player, dict=new Hashtable<String, ICommands>());
        }
        cmd = dict.get(key);
        if(cmd == null)
        {
            cmd=new ICommands(this);
            cmd.click = bclick;
            cmd.bindto = keybindings;
            dict.put(key, cmd);
        }
        return cmd;
    }
    public ICommands putDict(String player, String key, int cycle, ICommand i)
    {
        Dictionary<String, ICommands> cmds = getDict(player, key);
        ICommands get = cmds.get(key);
        if(get == null)
            
        get.cycle = cycle;
        int id = get.putDict(i, cycle);
        cmds.put(key, get);
        players.put(player, cmds);
        return get;
    }
    public ICommand findByID(String player, int id)
    {
        Enumeration<ICommands> dict = players.get(player).elements();
        for (;dict.hasMoreElements();)
        {
            ICommands cmds = dict.nextElement();
            ICommand cmd = cmds.findByID(id);
            if(cmd != null)
                return cmd;
        }
        return null;
    }
    public ICommands getICmdsByID(String player, int id)
    {
        Enumeration<ICommands> dict = players.get(player).elements();
        for (;dict.hasMoreElements();)
        {
            ICommands cmds = dict.nextElement();
            ICommand cmd = cmds.findByID(id);
            if(cmd != null)
                return cmds;
        }
        return null;
    }
    public int findNextID(String player)
    {
        Integer[] iDs = getIDs(player);
        for(int i = 1; i <= iDs.length; i++)
        {
            if(iDs[i-1] != i)
                return i;
        }
        return iDs.length+1;
    } 
    public Integer[] getIDs(String player)
    {
        ArrayList<Integer> data = new ArrayList<Integer>();
        Dictionary<String, ICommands> get = players.get(player);
        if(get == null)
            players.put(player, get=new Hashtable<String, ICommands>());
        Enumeration<ICommands> cmdsl = get.elements();
        for (;cmdsl.hasMoreElements();)
        {
            ICommands cmds = cmdsl.nextElement();
            Integer[] iDList = cmds.getIDList();
            data.addAll(Arrays.asList(iDList));
        }
        Collections.sort(data);
        Integer[] a = new Integer[data.size()];
        for(int i = 0; i < data.size(); i++) {
            a[i] = data.get(i);
            i++;
        }
        return data.toArray(a);
    }
    void sendMessage(Player player, String s)
    {
        if(player != null)
            player.sendMessage(s);
        else
        {
            System.out.println(ChatColor.stripColor(s));
        }
    }         
    public String stringReplacer(String str, Player player, Block block)
    {
        if(block != null)
        {
            str = str.replaceAll("<bx>", String.valueOf(block.getX()));
            str = str.replaceAll("<by>", String.valueOf(block.getY()));
            str = str.replaceAll("<bz>", String.valueOf(block.getZ()));
            str = str.replaceAll("<blight>", String.valueOf(block.getLightLevel()));
            str = str.replaceAll("<btype>", String.valueOf(block.getTypeId()));
            str = str.replaceAll("<bdata>", String.valueOf(block.getData()));
            str = str.replaceAll("<bname>", block.getType().name());
        }
        else
        {
            str = str.replaceAll("<bx>", "");
            str = str.replaceAll("<by>", "");
            str = str.replaceAll("<bz>", "");
            str = str.replaceAll("<blight>", "");
            str = str.replaceAll("<btype>", "");
            str = str.replaceAll("<bdata>", "");
            str = str.replaceAll("<bname>", "");
        }
        if(player != null)
        {
            Location location = player.getLocation();
            str = str.replaceAll("<x>", String.valueOf(location.getX()));
            str = str.replaceAll("<y>", String.valueOf(location.getY()));
            str = str.replaceAll("<z>", String.valueOf(location.getZ()));
            str = str.replaceAll("<name>", String.valueOf(player.getDisplayName()));
            str = str.replaceAll("<ip>", String.valueOf(player.getAddress().getAddress().toString()));
        }
        Server server = getServer();
        if(server != null)
        {
            str = str.replaceAll("<players>", listToString(server.getOnlinePlayers()));
            str = str.replaceAll("<sname>", server.getServerName());
            str = str.replaceAll("<sip>", server.getIp());
            str = str.replaceAll("<sversion>", server.getVersion());
        }
        Pattern p = Pattern.compile("&([0-9]{1,2})");
        Matcher m = p.matcher(str);
        while(m.find())
            str = m.replaceAll(ChatColor.getByCode(Integer.parseInt(m.group(1))).toString());
        Random r = new Random();
        p = Pattern.compile("<([^\\|]*)[\\|([^\\|]*)]+>");
        m = p.matcher(str);
        while(m.find())
        {
            String group = m.group();
            String[] split = group.substring(1, group.length()-1).split("\\|");
            int n = (int)(r.nextDouble()*split.length);
            str = m.replaceAll(split[n]);
        }
        p = Pattern.compile("<([0-9]+)-([0-9]+)>");
        m = p.matcher(str);
        while(m.find())
        {
            double n = r.nextDouble();
            int g1 = Integer.parseInt(m.group(1));
            int g2 = Integer.parseInt(m.group(2));
            int m1 = Math.max(g1, g2)+1;
            int m2 = Math.min(g1, g2);
            str = m.replaceAll(String.valueOf((int)((m1-m2)*n+m2)));
        }
        return str;
    }
    public String listToString(Object[] o)
    {
        String c = "";
        for(int i = 0; i < o.length; i++)
        {
            c += o[i] + (i != o.length-1 ? ", " : "");
        }
        return c;
    }
    public String listToString(Object[] o, String seperator)
    {
        String c = "";
        for(int i = 0; i < o.length; i++)
        {
            c += o[i] + (i != o.length-1 ? seperator : "");
        }
        return c;
    }
    protected boolean getVersion(String str)
    {
        Double oldversion = this.version;
        if(this.version == null)
        {
            int e = str.indexOf("#");
            if(e != -1)
                str = str.substring(0, e);
            if(!str.trim().isEmpty())
            {
                String[] s = str.split(" ");
                String cmd2 = "";
                for(int i = 2; i < s.length; i++)
                    cmd2 += (i == 2?"":" ") + s[i];
                try{
                    Integer.parseInt(cmd2.substring(0, 13));
                    oldversion = Double.parseDouble(this.getDescription().getVersion());
                }catch(Exception e1){
                try{
                    Integer.parseInt(cmd2.substring(0, 12));
                    oldversion = 1.13d;
                }catch(Exception e2){
                try{
                    Integer.parseInt(cmd2.substring(0, 6));
                    oldversion = 1.11d;
                }catch(Exception e3){
                try{
                    Integer.parseInt(cmd2.substring(0, 2));
                    oldversion = 1.02d;
                }catch(Exception e4){
                try{
                    Integer.parseInt(cmd2.substring(0, 1));
                    oldversion = 1.0d;
                }catch(Exception e5){ oldversion = null; }}}}}
            }
        }
        double currentversion = Double.parseDouble(getDescription().getVersion());
        if(oldversion != null && oldversion < currentversion)
        {
            updatePref();
            if(oldversion != 0)
            {
                updateDB(oldversion);
                System.out.println("ItemCommands: Updated from v"+String.valueOf(oldversion)+" to v"+currentversion);
                this.version = currentversion;
                return true;
            }
        }
        return false;
    }
    void updatePref()
    {
        savePref();
    }
    void updateDB(Double currentVersion)
    {
        ArrayList<String> data = readData(database, false);
        if(data != null)
        {
            players = new Hashtable<String, Dictionary<String, ICommands>>();
            for(int i = 0; i < data.size(); i++)
            {
                data.set(i, updateLine(data.get(i), currentVersion));
            }
            saveData(data, database);
            players = new Hashtable<String, Dictionary<String, ICommands>>();
            readDB(false);
        }
    }
    String updateLine(String line, Double currentVersion)
    {
        int click = -1;
        int clickevent = 0;
        int id = -1;
        String cmd = "";
        String name = "";
        String key = "";
        double cooldown = 0;
        int cycle = 0;
        ArrayList<Item> consume = new ArrayList<Item>();
        if(currentVersion < Double.parseDouble(this.getDescription().getVersion()))
        {
            int e = line.indexOf("#");
            if(e != -1)
                line = line.substring(0, e);
            if(!line.trim().isEmpty())
            {
                String[] s = line.split(" ");
                String cmd2 = "";
                for(int i = 2; i < s.length; i++)
                    cmd2 += (i == 2?"":" ") + s[i];
                cmd2 = cmd2.trim();
                name = s[0].replaceAll("&+", " ");
                key = s[1];
                try
                {
                    if(currentVersion >= 1.14d)
                    {
                        ICommand icmd = null;
                        cycle = Character.getNumericValue(cmd2.charAt(12));
                        try
                        {
                            icmd = new ICommand(name, key, cmd2, this);
                        }
                        catch(Exception e2){}
                        icmd.parent = putDict(name, key, cycle, icmd);
                        return name.replaceAll(" ", "&+") + " " + key + " " + icmd.toString();
                    }
                    if(currentVersion >= 1.12d)
                    {
                        id = Integer.parseInt(cmd2.substring(0, 4));
                        cooldown = Double.parseDouble(cmd2.substring(4, 10))*0.1;
                        click = Character.getNumericValue(cmd2.charAt(10));
                        clickevent = Character.getNumericValue(cmd2.charAt(11));
                        cmd = cmd2.substring(12);
                        int i = cmd.split(" ")[0].lastIndexOf(";");
                        if(i != -1)
                            consume = ICommand.parseConsume(cmd.substring(0, i));
                        cmd = cmd.substring(i+1);
                        ICommand icmd = new ICommand(name, key, id, cmd, click, clickevent, cooldown, consume, this);
                        icmd.parent = putDict(name, key, cycle, icmd);
                        return name.replaceAll(" ", "&+") + " " + key + " " + icmd.toString();
                    }
                    else if(currentVersion >= 1.1d)
                    {
                        id = Integer.parseInt(cmd2.substring(0, 4));
                        click = Character.getNumericValue(cmd2.charAt(4));
                        clickevent = Character.getNumericValue(cmd2.charAt(5));
                        cmd2 = cmd2.substring(6);
                        int i = cmd2.split(" ")[0].lastIndexOf(";");
                        if(i != -1)
                            consume = ICommand.parseConsume(cmd2.substring(0, i));
                        cmd = cmd2.substring(i+1);
                        ICommand icmd = new ICommand(name, key, id, cmd, click, clickevent, cooldown, consume, this);
                        icmd.parent = putDict(name, key, cycle, icmd);
                        return name.replaceAll(" ", "&+") + " " + key + " " + icmd.toString();
                    }
                    else
                    {
                        id = findNextID(name);
                        click = Character.getNumericValue(cmd2.charAt(0));
                        if(currentVersion > 1.0d)
                        {
                            clickevent = Character.getNumericValue(cmd2.charAt(1));
                            cmd = cmd2.substring(2);
                        }
                        else
                            cmd = cmd2.substring(1);
                        ICommand icmd = new ICommand(name, key, id, cmd, click, clickevent, cooldown, consume, this);
                        icmd.parent = putDict(name, key, cycle, icmd);
                        return name.replaceAll(" ", "&+") + " " + key + " " + icmd.toString();
                    }
                }
                catch(Exception e2)
                {
                    System.out.println("ItemCommands: Preference Version number does not match the database");
                }
            }
        }
        return line;
    }
    double parseTime(String time)
    {
        int toS = 1;
        if(time.endsWith("s"))
        {
            time = time.substring(0, time.length()-1);
        }
        else if(time.endsWith("m"))
        {
            toS = 60;
            time = time.substring(0, time.length()-1);
        }
        else if(time.endsWith("h"))
        {
            toS = 3600;
            time = time.substring(0, time.length()-1);
        } else {
            time = time.substring(0, time.length());
        }
        return Double.parseDouble(time)*toS;
    }
    private void bind(Player player, int option, String[] args)
    {
        switch(option)
        {
            case 1://add
                if(checkPermissions(player, "ICmds.admin", adminNeedOP))
                {
                    if(args.length >= 6){
                        int bindto = -1;
                        int end = 0;
                        int allow = -1;
                        int i;
                        for(i = 3; i < args.length; i++)
                        {
                            if(args[i].equalsIgnoreCase("-s"))
                                bindto = 1;
                            else if(args[i].equalsIgnoreCase("-i"))
                                bindto = 0;
                            else if(args[i].equalsIgnoreCase("-a"))
                                allow = 1;
                            else if(args[i].equalsIgnoreCase("-d"))
                                allow = 0;
                            else
                            {
                                break;
                            }
                        }
                        end = i;
                        if(allow == -1 || bindto == -1)
                            sendMessage(player, cmdc + "Usage: /icmd bind add [ids] [-a allow|-d deny] [-i item|-s slot] [command] "+descc+"#Allow or Block Commands By ids (Comma Seperated)");
                        else
                        {
                            String[] sids = args[2].split(",");
                            
                            ArrayList<Item> ids = new ArrayList<Item>();
                            for(int i2 = 0; i2 < sids.length; i2++)
                            {
                                ids.add(new Item(sids[i2]));
                                if(bindto == 1)
                                {
                                    int latest = ids.size()-1;
                                    int id = ids.get(latest).id;
                                    if(id > 9 || id < 1)
                                    {
                                        ids.remove(latest);
                                        sendMessage(player, errc + "Slot ids are valued 1-9");
                                    }
                                }
                            }
                            String a = "";
                            for(i = end; i < args.length; i++)
                                a += (i == end?"":" ") + args[i];
                            if(ids.size() > 0){
                                ArrayList<Item> old;
                                if(allow == 1)
                                    old = this.allow.get((bindto==1?"s":"i")+a);
                                else
                                    old = this.deny.get((bindto==1?"s":"i")+a);
                                if(old == null)
                                    old = new ArrayList<Item>();
                                int oldSize = old.size();
                                old.addAll(ids);
                                if(allow == 1)
                                    this.allow.put((bindto==1?"s":"i")+a, old);
                                else
                                    this.deny.put((bindto==1?"s":"i")+a, old);
                                saveBlacknWhite();
                                sendMessage(player, cmdc + "Added "+ids.size()+" to " + oldSize + " id"+(oldSize>1?"s":""));
                            }
                            else
                                sendMessage(player, errc + "Need at least one id");
                        }
                    }
                    else
                        sendMessage(player, cmdc + "Usage: /icmd bind add [ids] [-a allow|-d deny] [-i item|-s slot] [command] "+descc+"#Allow or Block Commands By ids (Comma Seperated)");
                }
                else
                    sendMessage(player, errc + "You do not have the required permissions for this.");
                break;
            case 2://set
                if(checkPermissions(player, "ICmds.admin", adminNeedOP))
                {
                    if(args.length >= 6){
                        int bindto = -1;
                        int end = 0;
                        int allow = -1;
                        int i;
                        for(i = 3; i < args.length; i++)
                        {
                            if(args[i].equalsIgnoreCase("-s"))
                                bindto = 1;
                            else if(args[i].equalsIgnoreCase("-i"))
                                bindto = 0;
                            else if(args[i].equalsIgnoreCase("-a"))
                                allow = 1;
                            else if(args[i].equalsIgnoreCase("-d"))
                                allow = 0;
                            else
                            {
                                break;
                            }
                        }
                        end = i;
                        if(allow == -1 || bindto == -1)
                            sendMessage(player, cmdc + "Usage: /icmd bind set [ids] [-a allow|-d deny] [-i item|-s slot] [command] "+descc+"#Allow or Block Commands By ids (Comma Seperated)");
                        else
                        {
                            String[] sids = args[2].split(",");
                            ArrayList<Item> ids = new ArrayList<Item>();
                            for(int i2 = 0; i2 < sids.length; i2++){
                                ids.add(new Item(sids[i2]));
                                if(bindto == 1)
                                {
                                    int latest = ids.size()-1;
                                    int id = ids.get(latest).id;
                                    if(id > 9 || id < 1)
                                    {
                                        ids.remove(latest);
                                        sendMessage(player, errc + "Slot ids are valued 1-9");
                                    }
                                }
                            }
                            String a = "";
                            for(i = end; i < args.length; i++)
                                a += (i == end?"":" ") + args[i];
                            int oldSize=0; 
                            if(allow == 1)
                                oldSize = this.allow.get((bindto==1?"s":"i")+a).size();
                            else
                                oldSize = this.deny.get((bindto==1?"s":"i")+a).size();
                            if(ids.size() > 0){
                                if(allow == 1)
                                    this.allow.put((bindto==1?"s":"i")+a, ids);
                                else
                                    this.deny.put((bindto==1?"s":"i")+a, ids);
                                saveBlacknWhite();
                                sendMessage(player, cmdc + "Replaced "+oldSize+" id"+(oldSize>1?"s":"")+" with "+ids.size());
                            }
                            else
                                sendMessage(player, errc + "Need at least one id");
                        }
                    }
                    else
                        sendMessage(player, cmdc + "Usage: /icmd bind set [ids] [-a allow|-d deny] [-i item|-s slot] [command] "+descc+"#Allow or Block Commands By ids (Comma Seperated)");
                }
                else
                    sendMessage(player, errc + "You do not have the required permissions for this.");
                break;
            case 3://remove
                if(checkPermissions(player, "ICmds.admin", adminNeedOP))
                {
                    if(args.length >= 6){
                        int bindto = -1;
                        int end = 0;
                        int allow = -1;
                        int i;
                        for(i = 3; i < args.length; i++)
                        {
                            if(args[i].equalsIgnoreCase("-s"))
                                bindto = 1;
                            else if(args[i].equalsIgnoreCase("-i"))
                                bindto = 0;
                            else if(args[i].equalsIgnoreCase("-a"))
                                allow = 1;
                            else if(args[i].equalsIgnoreCase("-d"))
                                allow = 0;
                            else
                            {
                                break;
                            }
                        }
                        end = i;
                        if(allow == -1 || bindto == -1)
                            sendMessage(player, cmdc + "Usage: /icmd bind remove [ids] [-a allow|-d deny] [-i item|-s slot] [command] "+descc+"#Allow or Block Commands By ids (Comma Seperated or *)");
                        else
                        {
                            String a = "";
                            for(i = end; i < args.length; i++)
                                a += (i == end?"":" ") + args[i];
                            String[] sids = args[2].split(",");
                            ArrayList<Item> old = new ArrayList<Item>();
                            if(sids[0].contains("*"))
                            {
                                int oldSize=0; 
                                if(allow == 1)
                                    oldSize = this.allow.get((bindto==1?"s":"i")+a).size();
                                else
                                    oldSize = this.deny.get((bindto==1?"s":"i")+a).size();
                                if(allow == 1)
                                    this.allow.put((bindto==1?"s":"i")+a, old);
                                else
                                    this.deny.put((bindto==1?"s":"i")+a, old);
                                saveBlacknWhite();
                                sendMessage(player, cmdc + "Cleared "+oldSize+" id"+(oldSize>1?"s":""));
                            }
                            else
                            {
                                ArrayList<Item> ids = new ArrayList<Item>();
                                for(int i2 = 0; i2 < sids.length; i2++)
                                {
                                    ids.add(new Item(sids[i2]));
                                    if(bindto == 1)
                                    {
                                        int latest = ids.size()-1;
                                        int id = ids.get(latest).id;
                                        if(id > 9 || id < 1)
                                        {
                                            ids.remove(latest);
                                            sendMessage(player, errc + "Slot ids are valued 1-9");
                                        }
                                    }
                                }
                                if(ids.size() > 0){
                                    if(allow == 1)
                                        old = this.allow.get((bindto==1?"s":"i")+a);
                                    else
                                        old = this.deny.get((bindto==1?"s":"i")+a);
                                    if(old == null)
                                        old = new ArrayList<Item>();
                                    int oldSize = old.size();
                                    old.removeAll(ids);
                                    if(allow == 1)
                                        this.allow.put((bindto==1?"s":"i")+a, old);
                                    else
                                        this.deny.put((bindto==1?"s":"i")+a, old);
                                    saveBlacknWhite();
                                    sendMessage(player, cmdc + "Removed "+ids.size()+" of " + oldSize + " id"+(oldSize>1?"s":""));
                                }
                                else
                                    sendMessage(player, errc + "Need at least one id");
                            }
                        }
                    }
                    else
                            sendMessage(player, cmdc + "Usage: /icmd bind remove [ids] [-a allow|-d deny] [-i item|-s slot] [command] "+descc+"#Allow or Block Commands By ids (Comma Seperated or *)");
                }
                else
                    sendMessage(player, errc + "You do not have the required permissions for this.");
                break;
            case 4://list
                if(checkPermissions(player, "ICmds.use", useNeedOP))
                {
                    if(args.length >= 4){
                        boolean msg = false;
                        int bindto = -1;
                        int end = 0;
                        int allow = -1;
                        int i;
                        boolean useCmd = true;
                        for(i = 2; i < args.length; i++)
                        {
                            if(args[i].equalsIgnoreCase("-s"))
                                bindto = 1;
                            else if(args[i].equalsIgnoreCase("-i"))
                                bindto = 0;
                            else if(args[i].equalsIgnoreCase("-a"))
                                allow = 1;
                            else if(args[i].equalsIgnoreCase("-d"))
                                allow = 0;
                            else
                            {
                                if(i == 2)
                                {
                                    useCmd = false;
                                }
                                else
                                    break;
                            }
                        }
                        end = i;
                        if(bindto == -1){
                            sendMessage(player, cmdc + "Usage: /icmd bind list [-i item|-s slot] <-a allow|-d deny> [command] "+descc+"#List Allowed or Blocked ids By Commands");
                            sendMessage(player, cmdc + "Usage: /icmd bind list [id] [-i item|-s slot] <-a allow|-d deny> "+descc+"#List Allowed or Blocked Commands By id");
                        }
                        else
                        {
                            if(useCmd)
                            {
                                String a = (bindto==1?"s":"i");
                                for(i = end; i < args.length; i++)
                                    a += (i == end?"":" ") + args[i].trim();

                                Enumeration<String> keys;
                                String c = (bindto==1?"Slots ":"Items ");
                                ArrayList<Item> old;
                                if(allow == 1 || allow == -1)
                                {
                                    keys = this.allow.keys();
                                    while(keys.hasMoreElements())
                                    {
                                        String nextElement = keys.nextElement();
                                        if(nextElement.startsWith(a))
                                        {
                                            old = this.allow.get(nextElement);
                                            String listToString = listToString(old.toArray(), "").replaceAll(";", ", ");
                                            listToString = listToString.substring(0, listToString.length() - 2);
                                            sendMessage(player, cmdc + nextElement.substring(1)+ " can be used with "+c + listToString);
                                            msg = true;
                                        }
                                    }
                                }
                                if(allow == 0 || allow == -1){
                                    keys = this.deny.keys();
                                    while(keys.hasMoreElements())
                                    {
                                        String nextElement = keys.nextElement();
                                        if(nextElement.startsWith(a))
                                        {
                                            old = this.deny.get(nextElement);
                                            String listToString = listToString(old.toArray(), "").replaceAll(";", ", ");
                                            listToString = listToString.substring(0, listToString.length() - 2);
                                            sendMessage(player, cmdc + nextElement.substring(1)+ " cannot be used with "+c + listToString);
                                            msg = true;
                                        }
                                    }
                                }
                            }
                            else
                            {
                                String c = (bindto==1?"Slots ":"Items ");
                                Enumeration<String> keys;
                                ArrayList<Item> old;
                                String id = args[2];
                                int i2 = id.indexOf(":");
                                int data = -1;
                                int intId;
                                if(i2 != -1)
                                {
                                    try{
                                        data = Integer.parseInt(id.substring(i2+1));}
                                    catch(NumberFormatException e2){}
                                    id = id.substring(0, i2);}
                                try
                                {
                                    intId = Integer.parseInt(id);
                                }
                                catch(NumberFormatException e2)
                                {
                                    sendMessage(player, errc + "Could not read the id.");
                                    intId = -1;
                                }
                                
                                if(bindto == 1 && (intId > 9 || intId < 1))
                                {
                                    sendMessage(player, errc + "Slot ids are valued 1-9");
                                }
                                else if(intId != -1)
                                {
                                    if(allow == 1 || allow == -1)
                                    {
                                        keys = this.allow.keys();
                                        while(keys.hasMoreElements())
                                        {
                                            String nextElement = keys.nextElement();
                                            old = this.allow.get(nextElement);
                                            if(itemsContains(old, intId, data))
                                            {
                                                String listToString = listToString(old.toArray(), "").replaceAll(";", ", ");
                                                listToString = listToString.substring(0, listToString.length() - 2);
                                                sendMessage(player, cmdc + nextElement.substring(1) + " can be used with "+c + listToString);
                                                msg = true;
                                            }
                                        }
                                    }
                                    if(allow == 0 || allow == -1){
                                        keys = this.deny.keys();
                                        while(keys.hasMoreElements())
                                        {
                                            String nextElement = keys.nextElement();
                                            old = this.deny.get(nextElement);
                                            if(itemsContains(old, intId, data))
                                            {
                                                String listToString = listToString(old.toArray(), "").replaceAll(";", ", ");
                                                listToString = listToString.substring(0, listToString.length() - 2);
                                                sendMessage(player, cmdc + nextElement.substring(1)+ " cannot be used with "+c + listToString);
                                                msg = true;
                                            }
                                        }
                                    }
                                }
                                else
                                    sendMessage(player, cmdc + "Usage: /icmd bind list [id] [-i item|-s slot] <-a allow|-d deny> "+descc+"#List Allowed or Blocked Commands By id");
                            }
                            if(!msg)
                                sendMessage(player, cmdc + "Nothing Listed");
                        }
                    }
                    else{
                        sendMessage(player, cmdc + "Usage: /icmd bind list [-i item|-s slot] <-a allow|-d deny> [command] "+descc+"#List Allowed or Blocked ids By Commands");
                        sendMessage(player, cmdc + "Usage: /icmd bind list [id] [-i item|-s slot] <-a allow|-d deny> "+descc+"#List Allowed or Blocked Commands By id");}
                        
                }
                else
                    sendMessage(player, errc + "You do not have the required permissions for this.");
                break;
            default:
                sendMessage(player, errc + "[] is required, <> is optional");
                if(checkPermissions(player, "ICmds.admin", adminNeedOP)){
                sendMessage(player, cmdc + "/icmd bind add [ids] [-a allow|-d deny] [-i item|-s slot] [command] "+descc+"#Add ids to Allow or Block Commands (ids Comma Seperated)");
                sendMessage(player, cmdc + "/icmd bind set [ids] [-a allow|-d deny] [-i item|-s slot] [command] "+descc+"#Set ids to Allow or Block Commands (ids Comma Seperated)");
                sendMessage(player, cmdc + "/icmd bind remove [ids] [-a allow|-d deny] [-i item|-s slot] [command] "+descc+"#Remove ids to Allow or Block Commands (ids Comma Seperated or *)");}
                if(checkPermissions(player, "ICmds.use", useNeedOP)){
                sendMessage(player, cmdc + "/icmd bind list [-i item|-s slot] <-a allow|-d deny> [command] "+descc+"#List Allowed or Blocked ids By Commands");
                sendMessage(player, cmdc + "/icmd bind list [id] [-i item|-s slot] <-a allow|-d deny> "+descc+"#List Allowed or Blocked Commands By id");}
        }
    }
    boolean itemsContains(ArrayList<Item> items, int id, int data)
    {
        for(int i = 0; i < items.size(); i++)
        {
            if(items.get(i).equals(id, data))
                return true;
        }
        return false;
    }
    boolean isListed(ICommand cmd, Item id, boolean slot)
    {
        String runningCommand = cmd.getRunningCommand();
        Enumeration<String> keys = this.deny.keys();
        while(keys.hasMoreElements())
        {
            String nextElement = keys.nextElement();
            String str = nextElement.substring(1);
            if(str.startsWith(runningCommand))
            {
                ArrayList<Item> asList = this.deny.get(nextElement);
                if(this.itemsContains(asList, id.id, id.damage))
                    return false;
            }
        }
        keys = this.allow.keys();
        while(keys.hasMoreElements())
        {
            String nextElement = keys.nextElement();
            String str = nextElement.substring(1);
            ArrayList<Item> asList = new ArrayList<Item>();
            if(str.startsWith(runningCommand))
                asList.addAll(this.allow.get(nextElement));
            if(itemsContains(asList, id.id, id.damage))
                return true;
            else if(asList.size()>0)
                return false;
        }
        return true;
    }
}