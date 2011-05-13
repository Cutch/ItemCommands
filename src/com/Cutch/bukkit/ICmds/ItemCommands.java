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
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
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

    public void onDisable() {
        System.out.println("ItemCommands is Disabled");
    }

    public void onEnable() {
        PluginDescriptionFile desc = this.getDescription();
        players = new Hashtable<String, Dictionary<String, ICommands>>();
        version = null;
        readPref();
        try
        {
            pms = new PermissionSupport(this){};
            permissionsType = pms.setupPermissions(permissionsType);
        }catch(NoClassDefFoundError e){
            permissionsType=1;
            System.out.println("ItemCommands: Permission system not detected. Using Basic Permissions.");
        }
        readDB(true);
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
            player = new ICPlayer((Player)sender) {};
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
                            if(per == 0 && !checkPermissions(player, "ICmds.global", globalNeedOP))
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
                                putDict(splayer, key, cmd);
                                String pcmd = cmd.cmd;
                                pcmd = pcmd.replaceFirst(":0", "");
                                sendMessage(player, cmdc + "ID: " + id + " Command: " + pcmd + " added to "+(bindto == 0 ? "item" : "slot") + " " + key + " for " + (!splayer.isEmpty()?splayer:"Everyone"));
                                saveDB();
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
                            int id = Integer.parseInt(args[1]);
                            ICommands cmds = getICmdsByID(player, id, splayer.isEmpty());
                            ICommand cmd = null;
                            if(cmds == null || (cmd = cmds.remove(id)) == null)
                            {
                                sendMessage(player, cmdc + "Command not found.");
                            } else {
                                sendMessage(player, cmdc + "Command: "+ cmd.cmd +" removed from "+(cmd.clickevent == 0 ? "item" : "slot")+" " + cmd.key + " for " + (!cmd.isGlobal()?player.getName():"Everyone"));
                                saveDB();
                            }
                        }
                        else
                            sendMessage(player, cmdc + "Usage: /icmd remove [id] "+descc+"#Remove command by ID");
                    }
                    else
                        sendMessage(player, errc + "You do not have the required permissions for this.");
                }
                else if(args[0].equalsIgnoreCase("swap"))
                {
                    if(checkPermissions(player, "ICmds.create", createNeedOP))
                    {
                        if(args.length >= 2){
                            int id = Integer.parseInt(args[1]);
                            int id2 = Integer.parseInt(args[2]);
                            ICommands cmds = getICmdsByID(player, id, splayer.isEmpty());
                            ICommand cmd = cmds.get(id);
                            if(cmd != null)
                            {
                                if(cmd != null)
                                {
                                    ICommand cmd2 = cmds.get(id2);
                                    cmd.id = id2;
                                    cmd2.id = id;
                                    cmds.putDict(cmd);
                                    cmds.putDict(cmd2);
                                    saveDB();
                                } else
                                    sendMessage(player, errc + "Id #1 "+id2+" was not found");
                            } else
                                sendMessage(player, errc + "Id #2 "+id+" was not found");
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
                            int id = Integer.parseInt(args[1]);
                            String key = "";
                            int click = -1;
                            int clickevent = -1;
                            int bindto = -1;
                            int per = 1;
                            int end = 0;
                            double cooldown = -1;
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
                                sendMessage(player, cmdc + "Usage: /icmd change [id] <flags> <command> "+descc+"#Change flags by id");
                                return true;
                            }
                            if(per == 0)
                                splayer = "";
                            ICommands cmds = getICmdsByID(player, id, per == 0);
                            ICommand cmd = cmds.findByID(id);
                            if(per == 0 && !checkPermissions(player, "ICmds.global", globalNeedOP))
                                sendMessage(player, errc + "You do not have the required permissions for global assignments.");
                            else if(cmd != null)
                            {
                                if(key.isEmpty())
                                    key = cmd.key;
                                if(click == -1)
                                    click = cmd.click;
                                if(clickevent == -1)
                                    clickevent = cmd.clickevent;
                                if(cooldown == -1)
                                    cooldown = cmd.cooldown;
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
                                    putDict(splayer, key, cmd);
                                }
                                if(per == -1)
                                    per = cmd.global;
                                String a = "";
                                for(i = end; i < args.length; i++)
                                    a += (i == end?"":" ") + args[i];
                                cmd.click=click;
                                if(!a.isEmpty())
                                    cmd.cmd = a;
                                cmd.consume = consumes;
                                cmd.clickevent = clickevent;
                                cmd.cooldown = cooldown;
                                putDict(splayer, key, cmd);
                                saveDB();
                            }
                            else
                                sendMessage(player, errc + "ID " + args[1] + " not valid.");
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
                        for(int i = 1; i < args.length; i+=1)
                        {
                            if(args[i].equalsIgnoreCase("-s"))
                                fbindto = 1;
                            else if(args[i].equalsIgnoreCase("-i"))
                                fbindto = 0;
                            else if(args[i].equalsIgnoreCase("-g"))
                                fper = 0;
                        }
                        String str = "Commands (" + (fper == 0 || splayer.isEmpty() ? "Global" : splayer) + ")";
                        sendMessage(player, infoc + "  ID     Item/Slot     Click Trigger     " + str);
                        sendMessage(player, infoc + "---------------------------------------------------");
                        Dictionary<String, ICommands> get = players.get(fper == 1 ? splayer : "");
                        if(get != null)
                        {
                            Enumeration<ICommands> elements = get.elements();
                            int linei = 0;
                            while(elements.hasMoreElements())
                            {
                                ICommands cmds = elements.nextElement();
                                Integer[] iDList = cmds.getIDList();
                                for (Integer id : iDList)
                                {
                                    ICommand cmd = cmds.findByID(id);
                                    if(fbindto == -1 || cmd.bindto == fbindto)
                                    {
                                        String click = (cmd.click == 1 ? "Left" : "Right");
                                        sendMessage(player, (linei%2==0 ? cmdc : descc) + lspace(String.valueOf(cmd.id)," ",4) + "     " + lspace(cmd.key.replaceAll(":0", "")," ",9) + lspace(click," ",20) + "     "+ cmd.cmd);
                                        linei++;
                                    }
                                }
                            }
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
//                    try
                    {
                        this.putDict(name, s[1], new ICommand(name, s[1], cmd2, this));
                    }
//                    catch(Exception e2)
//                    {
//                        System.out.println("Line "+i2+" could not be parsed");
//                    }
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
        int i = 0;
        if(checkPermissions(player, "ICmds.create", createNeedOP)) {
            sendMessage(player, cmdc + "Usage: /icmd add [-i item|-s slot] [flags] [command] "+descc+"#Add command");
            sendMessage(player, cmdc + "/icmd remove [id] "+descc+"#Remove command by ID");
            sendMessage(player, cmdc + "/icmd change [id] <flags> <command> "+descc+"#Change properties of IDs command");
            sendMessage(player, cmdc + "/icmd swap [id #1] [id #2] "+descc+"#Swap commands by ID");
            i++;}
        if(checkPermissions(player, "ICmds.use", useNeedOP)){
            sendMessage(player, cmdc + "/icmd list <-i item|-s slot> <-g global> "+descc+"#List commands available");
            i++;}
        if(checkPermissions(player, "ICmds.admin", adminNeedOP)){
            sendMessage(player, cmdc + "/icmd reload");
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
    public int putDict(String player, String key, ICommand i)
    {
        Dictionary<String, ICommands> cmds = getDict(player, key);
        int id = cmds.get(key).putDict(i);
        players.put(player, cmds);
        return id;

    }
    public ICommand findByID(int id)
    {
        ArrayList<Integer> data = new ArrayList<Integer>();
        Enumeration<Dictionary<String, ICommands>> dict = players.elements();
        for (;dict.hasMoreElements();)
        {
            Dictionary<String, ICommands> values = dict.nextElement();
            Enumeration<ICommands> elements = values.elements();
            for (;elements.hasMoreElements();)
            {
                ICommands cmds = elements.nextElement();
                ICommand cmd = null;
                if((cmd = cmds.findByID(id)) != null)
                    return cmd;
            }
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
    ICommands getICmdsByID(Player player, int id, boolean global){
        int bindto = 1;
        String[] keys = new String[2];
        ItemStack is = player.getItemInHand();
        keys[0] = String.valueOf(is.getTypeId()) + ":" + String.valueOf(is.getDurability());
        keys[1] = String.valueOf(player.getInventory().getHeldItemSlot()+1);
        ICommands cmdt = getDict(player.getName(), keys[bindto]).get(keys[bindto]);
        ICommand cmd = null;
        if (cmdt != null)
            cmd = cmdt.findByID(id);
        if(cmd == null)
        {
            cmdt = getDict(player.getName(), keys[1-bindto]).get(keys[1-bindto]);
            if(cmdt != null)
                cmd = cmdt.findByID(id);
            if(cmd == null)
            {
                cmdt = getDict("", keys[bindto]).get(keys[bindto]);
                if(cmdt != null)
                    cmd = cmdt.findByID(id);
                if(cmd == null)
                {
                    cmdt = getDict("", keys[1-bindto]).get(keys[1-bindto]);
                    if(cmdt != null)
                        cmd = cmdt.findByID(id);
                }
            }
        }
        return cmdt;
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
    protected String stringReplacer(String str, Player player, Block block)
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
        return str;
    }
    protected String listToString(Object[] o)
    {
        String c = "";
        for(int i = 0; i < o.length; i++)
        {
            c += o[i] + (i != o.length-1 ? ", " : "");
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
                    Integer.parseInt(cmd2.substring(0, 11));
                    oldversion = 1.12d;
                }catch(NumberFormatException e1){
                try{
                    Integer.parseInt(cmd2.substring(0, 6));
                    oldversion = 1.11d;
                }catch(NumberFormatException e2){
                try{
                    Integer.parseInt(cmd2.substring(0, 2));
                    oldversion = 1.02d;
                }catch(NumberFormatException e3){
                try{
                    Integer.parseInt(cmd2.substring(0, 1));
                    oldversion = 1.0d;
                }catch(NumberFormatException e4){}}}}
            } else {
                updatePref();
                return true;
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
                if(currentVersion >= 1.12d)
                {
                    ICommand icmd = null;
                    try
                    {
                        icmd = new ICommand(name, key, cmd2, this);
                    }
                    catch(Exception e2){}
                    putDict(name, key, icmd);
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
                    putDict(name, key, icmd);
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
                    putDict(name, key, icmd);
                    return name.replaceAll(" ", "&+") + " " + key + " " + icmd.toString();
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
}