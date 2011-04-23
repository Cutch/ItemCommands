package com.Cutch.bukkit.ICmds;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;

public class PlayerEvents extends PlayerListener {
    ChatColor cmdc = ChatColor.BLUE;
    ChatColor descc = ChatColor.AQUA;
    ChatColor errc = ChatColor.RED;
    ChatColor infoc = ChatColor.YELLOW;
    String properties = "ItemCommands.properties";
    String database = "Commands.db";
    int bclick = 0;
    private final ItemCommands plugin;
    public Dictionary<String, Dictionary<String, String>> players = new Hashtable<String, Dictionary<String, String>>();
    public PlayerEvents(ItemCommands instance) {
        plugin = instance;
    }
    public void saveData(ArrayList<String> data, String file)
    {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(plugin.getDataFolder() + File.separator + file);
            bufferedWriter = new BufferedWriter(fileWriter);

            for (int i = 0; i < data.size(); i++)
                bufferedWriter.write(data.get(i) + "\n", 0, data.get(i).length()+1);

            bufferedWriter.close();
            fileWriter.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(plugin.getDataFolder() + File.separator + file + " Not Found... Making new file.");
            plugin.getDataFolder().mkdir();
            new File(plugin.getDataFolder() + File.separator + file);
            saveData(data, file);
        }
        catch (IOException e) { System.out.println(plugin.getDataFolder() + File.separator + file + " Could not be open"); }
    }
    public ArrayList<String> readData(String file)
    {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        ArrayList<String> data = new ArrayList<String>();
        try {
            fileReader = new FileReader(plugin.getDataFolder() + File.separator + file);
            bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                data.add(line);
            }
            bufferedReader.close();
            fileReader.close();
        }
        catch (FileNotFoundException e) {
            plugin.getDataFolder().mkdir();
            new File(plugin.getDataFolder() + File.separator + file);
            System.out.println(plugin.getDataFolder() + File.separator + file + " Not Found");
            data = null; }
        catch (IOException e) {
            System.out.println(plugin.getDataFolder() + File.separator + file + " Could not be open");
            data = null; }
        return data;
    }
    public void saveDB()
    {
        ArrayList<String> data = new ArrayList<String>();
        Enumeration<String> dict = players.keys();
        for (;dict.hasMoreElements();)
        {
            String i = dict.nextElement();
            Dictionary<String, String> lines = players.get(i);
            Enumeration<String> dict2 = lines.keys();

            for (;dict2.hasMoreElements();)
            {
                String i2 = dict2.nextElement();
                String line = lines.get(i2);

                data.add(i + " " + i2 + " " + line);
            }
        }
        saveData(data, database);
    }
    public void readDB()
    {
        ArrayList<String> data = readData(database);
        String line = null;
        String last = "@";
        Dictionary<String, String> dict = null;
        if(data != null){
            Collections.sort(data);
            for (int i2 = 0; i2 < data.size(); i2++)
            {
                line = data.get(i2);
                String[] s = line.split(" ");
    //            System.out.println("----------");
    //            System.out.println(i2);
    //            System.out.println(line);
    //            System.out.println("\"" + s[0] + "\"" + s[1] + "\"" + s[2] + "\"");
                if(!last.equalsIgnoreCase(s[0])){
                    dict = getDict(s[0]);
                    last = s[0];
                }
                String cmd2 = "";
                for(int i = 2; i < s.length; i++)
                    cmd2 += (i == 2?"":" ") + s[i];
                dict.put(s[1], cmd2);
            }
        }
    }
    int per = 1;
    int keybindings = 1;
    public int permissions = 0;
    public void readPref()
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
    public void savePref()
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
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
//        player.sendRawMessage(cmdc + "Event: " + event.getAction().toString());
        if(checkPermissions(player, "ICmds.use", useNeedOP))
        {
            Dictionary<String, String> dict = getDict(player);
            ItemStack i = player.getItemInHand();
            String key;
            if(Integer.parseInt(dict.get("bindto")) == 1)
                key = String.valueOf(player.getInventory().getHeldItemSlot());
            else
                key = String.valueOf(i.getTypeId()) + ":" + String.valueOf(i.getDurability());
            String cmd = dict.get(key);
            if (cmd != null) {
                int click = Character.getNumericValue(cmd.trim().charAt(0));
                if((click == 0 && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) ||
                (click == 1 && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)))
                {
                    cmd = cmd.substring(1);
                    if(cmd.startsWith("/"))
                        player.chat(cmd);
                    else
                        player.sendMessage(cmd);
                    event.setCancelled(true);
                    event.setUseItemInHand(Result.DENY);
                }
            }
//            String cmd = dict.get(key);
//            int click = Integer.parseInt(dict.get("click"));
//            if (cmd != null)
//                if((click == 0 && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) ||
//                (click == 1 && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)))
//                {
//                    player.chat(cmd);
//                    event.setCancelled(true);
//                    event.setUseItemInHand(Result.DENY);
//                }
        }
    }
    boolean createNeedOP = true;
    boolean useNeedOP = false;
    public boolean adminNeedOP = true;
    @Override
    public void onPlayerCommandPreprocess (PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage();
        if(msg.charAt(0) == '/')
            msg = msg.substring(1);
        Player player = event.getPlayer();
        String[] s = msg.split(" ");
        if(s[0].equalsIgnoreCase("ic")){
            if(s.length >= 2)
            {
                if(s[1].equalsIgnoreCase("add"))
                {
                    if(checkPermissions(player, "ICmds.create", createNeedOP))
                    {
                        if(s.length >= 3)
                        {
                            String key;
                            Dictionary<String, String> dict = getDict(player);
                            int bindto = Integer.parseInt(dict.get("bindto"));
                            if(bindto == 1)
                                key = String.valueOf(player.getInventory().getHeldItemSlot());
                            else
                                key = String.valueOf(player.getItemInHand().getTypeId()) + ":" + String.valueOf(player.getItemInHand().getDurability());
                            String cmd = "";
                            for(int i = 2; i < s.length; i++)
                                cmd += (i == 2?"":" ") + s[i];
                            dict.put(key, String.valueOf(bclick) + cmd);
                            if(cmd.contains(":0"))
                                cmd = cmd.replaceFirst(":0", "");
                            player.sendRawMessage(cmdc + "Command: " + cmd + " added to "+(bindto == 0 ? "item" : "slot") + " " + key + " for " + (permissions == 0?player.getName():"Everyone"));
                            saveDB();
                        }
//                        else if(s.length >= 4)
//                        {
//                                if(!s[2].contains(":"))
//                                    s[2] = s[2] + ":0";
//                                Dictionary<String, String> dict = getDict(player);
//                                int bindto = Integer.parseInt(dict.get("bindto"));
//                                dict.put(s[2], cmd + charclick + String.valueOf(bclick));
//                                saveDB();
//                                player.sendRawMessage(cmdc + "Command: " + cmd + " added to "+(bindto == 0 ? "item" : "slot")+" " + s[2] + " for " + (permissions == 0?player.getName():"Everyone"));
//                        }
                        else
                            player.sendRawMessage(cmdc + "Usage: /ic add [command] "+descc+"#Add command to the selected item");
                    }
                    else
                        player.sendRawMessage(errc + "You do not have the required permissions for this.");
                }
//                else if(s[1].equalsIgnoreCase("replace"))
//                {
//                    if(checkPermissions(player, "ICmds.create", createNeedOP))
//                    {
//                        if(s.length >= 3)
//                        {
//                            String key;
//                            Dictionary<String, String> dict = getDict(player);
//                            int bindto = Integer.parseInt(dict.get("bindto"));
//                            if(bindto == 1)
//                                key = String.valueOf(player.getInventory().getHeldItemSlot());
//                            else
//                                key = String.valueOf(player.getItemInHand().getTypeId()) + ":" + String.valueOf(player.getItemInHand().getDurability());
//                            String cmd = "";
//                            for(int i = 2; i < s.length; i++)
//                                cmd += (i == 2?"":" ") + s[i];
//                            dict.put(key, String.valueOf(bclick) + cmd);
//                            if(cmd.contains(":0"))
//                                cmd = cmd.replaceFirst(":0", "");
//                            player.sendRawMessage(cmdc + "Command: " + cmd + " added to "+(bindto == 0 ? "item" : "slot") + " " + key + " for " + (permissions == 0?player.getName():"Everyone"));
//                            saveDB();
//                        }
//                        else
//                            player.sendRawMessage(cmdc + "Usage: /ic replace [command] "+descc+"#Replace command binded to target");
//                    }
//                    else
//                        player.sendRawMessage(errc + "You do not have the required permissions for this.");
//                }
//                else if(s[1].equalsIgnoreCase("addid"))
//                {
//                    if(checkPermissions(player, "ICmds.create", createNeedOP))
//                        if(s.length >= 4){
//                            Dictionary<String, String> dict = getDict(player);
//                            int bindto = Integer.parseInt(dict.get("bindto"));
//                            dict.put(s[2], cmd + charclick + String.valueOf(bclick));
//                            player.sendRawMessage(cmdc + "Command: " + cmd + " added to "+(bindto == 0 ? "item" : "slot")+" " + s[2] + " for " + (permissions == 0?player.getName():"Everyone"));
//                            saveDB();}
//                        else
//                            player.sendRawMessage(cmdc + "Usage: /ic addid [id] [command] "+descc+"#Add command to the selected item");
//                    else
//                        player.sendRawMessage(errc + "You do not have the required permissions for this.");
//                }
                else if(s[1].equalsIgnoreCase("remove"))
                {
                    if(checkPermissions(player, "ICmds.create", createNeedOP))
                    {
                        if(s.length == 2){
                            String cmd1 = "";
                            Dictionary<String, String> dict = getDict(player);
                            String key;
                            int bindto = Integer.parseInt(dict.get("bindto"));
                            if(bindto == 1)
                                key = String.valueOf(player.getInventory().getHeldItemSlot());
                            else
                                key = String.valueOf(player.getItemInHand().getTypeId()) + ":" + String.valueOf(player.getItemInHand().getDurability());
                            
                            if(dict == null || (cmd1 = dict.remove(key)) == null)
                                player.sendRawMessage(cmdc + "Command not mapped to item " + key);
                            else
                                player.sendRawMessage(cmdc + "Command: "+ cmd1 +" removed from "+(bindto == 0 ? "item" : "slot")+" " + key + " for " + (permissions == 0?player.getName():"Everyone"));
                            saveDB();
                        }
//                        else
//                        {
//                            if(!s[2].contains(":"))
//                                s[2] = s[2] + ":0";
//                            if(s.length >= 3)
//                            {
//                                String cmd1 = "";
//                                Dictionary<String, String> dict = getDict(player);
//                                int bindto = Integer.parseInt(dict.get("bindto"));
//                                if(dict == null || (cmd1 = dict.remove(s[2])) == null)
//                                    player.sendRawMessage(cmdc + "Command not mapped to "+(bindto == 0 ? "item" : "slot")+" " + s[2]);
//                                else
//                                    player.sendRawMessage(cmdc + "Command: "+ cmd1 +" removed from "+(bindto == 0 ? "item" : "slot")+" " + s[2] + " for " + (permissions == 0?player.getName():"Everyone"));
//                                saveDB();
//                            }
//                            else
//                                player.sendRawMessage(cmdc + "Usage: /ic remove "+descc+"#Remove command from the selected item");
//                        }
                    }
                    else
                        player.sendRawMessage(errc + "You do not have the required permissions for this.");
                }
//                else if(s[1].equalsIgnoreCase("removeid"))
//                {
//
//                    if(checkPermissions(player, "ICmds.create", createNeedOP))
//                        if(s.length >= 3)
//                        {
//                            String cmd1 = "";
//                            Dictionary<String, String> dict = getDict(player);
//                            int bindto = Integer.parseInt(dict.get("bindto"));
//                            if(dict == null || (cmd1 = dict.remove(s[2])) == null)
//                                player.sendRawMessage(cmdc + "Command not mapped to "+(bindto == 0 ? "item" : "slot")+" " + s[2]);
//                            else
//                                player.sendRawMessage(cmdc + "Command: "+ cmd1 +" removed from "+(bindto == 0 ? "item" : "slot")+" " + s[2] + " for " + (permissions == 0?player.getName():"Everyone"));
//                            saveDB();
//                        }
//                        else
//                            player.sendRawMessage(cmdc + "Usage: /ic removeid [id] "+descc+"#Remove command from the selected item");
//                    else
//                        player.sendRawMessage(errc + "You do not have the required permissions for this.");
//                }
                else if(s[1].equalsIgnoreCase("click"))
                {
                    if(s.length >= 3)
                    {
                        String key;
                        Dictionary<String, String> dict = getDict(player);
                        int bindto = Integer.parseInt(dict.get("bindto"));
                        if(bindto == 1)
                            key = String.valueOf(player.getInventory().getHeldItemSlot());
                        else
                            key = String.valueOf(player.getItemInHand().getTypeId()) + ":" + String.valueOf(player.getItemInHand().getDurability());

                        if(checkPermissions(player, "ICmds.create", createNeedOP))
                        {
                            String cmd1 = dict.get(key);
                            if(s[2].startsWith("r"))
                            {
                                dict.put(key, String.valueOf(bclick) + cmd1);
                                player.sendRawMessage(cmdc + (bindto == 0 ? "item" : "slot") + " " + key + " is triggered with a right click");
                                saveDB();
                            }
                            else if(s[2].startsWith("l"))
                            {
                                dict.put(key, String.valueOf(bclick) + cmd1);
                                player.sendRawMessage(cmdc + (bindto == 0 ? "item" : "slot") + " " + key + " is triggered with a left click");
                                saveDB();
                            }
                            else
                                player.sendRawMessage(errc + "Parameter " + s[2] + " not valid.");
                        }
                        else
                            player.sendRawMessage(errc + "You do not have the required permissions for this.");
                    }
                    else
                        player.sendRawMessage(cmdc + "Usage: /ic click [left l|right r] "+descc+"#Change trigger of the selected item");
                }
                else if(s[1].equalsIgnoreCase("per"))
                {
                    if(s.length >= 3)
                    {
                        if(checkPermissions(player, "ICmds.admin", adminNeedOP))
                        {
                            if(s[2].startsWith("pl"))
                            {
                                per = 1;
                                player.sendRawMessage(cmdc + "Per Player command system initialized");
                                savePref();
                            }
                            else if(s[2].startsWith("gl"))
                            {
                                per = 0;
                                player.sendRawMessage(cmdc + "Global command system initialized");
                                savePref();
                            }
                            else if(s[2].startsWith("gr"))
                            {
                                per = 2;
                                player.sendRawMessage(cmdc + "Group command system initialized (NOT WORKING)");
                                savePref();
                            }
                            else
                                player.sendRawMessage(errc + "Parameter " + s[2] + " not valid.");
                        }
                        else
                            player.sendRawMessage(errc + "You do not have the required permissions for this.");
                    }
                    else
                        player.sendRawMessage(cmdc + "Usage: /ic per [player in|global gl] "+descc+"#Set commands globally per player");
                }
                else if(s[1].equalsIgnoreCase("bindto"))
                {
                    Dictionary<String, String> dict = getDict(player);
                    if((per == 0 && checkPermissions(player, "ICmds.create", createNeedOP))
                            || (per == 1 && checkPermissions(player, "ICmds.use", useNeedOP)))
                    {
                        if(s.length >= 3)
                        {
                            if(s[2].startsWith("s"))
                            {
                                player.sendRawMessage(cmdc + "Binded to slots");
                                dict.put("bindto", "1");
                                saveDB();
                            }
                            else if(s[2].startsWith("i"))
                            {
                                dict.put("bindto", "0");
                                player.sendRawMessage(cmdc + "Binded to items");
                                saveDB();
                            }
                            else
                                player.sendRawMessage(errc + "Parameter " + s[2] + " not valid.");
                        }
                        else
                            player.sendRawMessage(cmdc + "Usage: /ic bindto [item i|slot s] "+descc+"#Binds commands to slots or items");
                    }
                    else
                        player.sendRawMessage(errc + "You do not have the required permissions for this.");
                }
                else if(s[1].equalsIgnoreCase("list"))
                {
                    if(checkPermissions(player, "ICmds.use", useNeedOP))
                    {
                        Dictionary<String, String> dict = getDict(player);
                        int bindto = Integer.parseInt(dict.get("bindto"));
                        String str = "Commands (" + (this.per == 1 ? player.getName() : "Global") + ")";
                        player.sendRawMessage(infoc + (bindto == 0 ? "Item" : "Slot") + "   " + str + "\n");
                        player.sendRawMessage(infoc + "----------------------------------\n");
                        ArrayList<String> list = Collections.list(dict.keys());
                        int linei=0;
                        if(list != null) {
                            for (int i2 = 0; i2 < list.size(); i2++)
                            {
                                String i = list.get(i2);
                                if(!i.equalsIgnoreCase("bindto") && !i.equalsIgnoreCase("click")){
                                    if((bindto == 0 && i.contains(":")) || (bindto == 1 && !i.contains(":")))
                                    {
                                    String c = dict.get(i).substring(1);
                                    player.sendRawMessage((linei%2==0 ? cmdc : descc) + rspace(i,4) + "       " + rspace(c,0) + "\n");
                                    linei++;
                                    }
                                }
                            }
                        }
                    }
                    else
                        player.sendRawMessage(errc + "You do not have the required permissions for this.");
                }
                else if(s[1].equalsIgnoreCase("reload"))
                {
                    if(checkPermissions(player, "ICmds.admin", adminNeedOP))
                    {
                        readDB();
                        readPref();
                        System.out.println("ItemCommands Has been Reloaded");
                        player.sendRawMessage(ChatColor.RED+"ItemCommands: Has been Reloaded");
                    }
                    else
                        player.sendRawMessage(errc + "You do not have the required permissions for this.");
                }
                else
                    showHelp(player);
            }
            else
                showHelp(player);
            event.setCancelled(true);
        }
    }
    public boolean checkPermissions(Player player, String node, boolean needOp)
    {
        if (ItemCommands.Permissions == null || this.permissions == 1) {
            return (player.isOp() && needOp) || !needOp;
        }
        else {
            return ItemCommands.Permissions.has(player, node);
        }
    }
    public String rspace(String s, int len)
    {
        for(int i = s.length(); i < len; i++)
            s = " " + s;
        return s;
    }
    public void showHelp(Player player){
        int i = 0;
        Dictionary<String, String> dict = getDict(player);
        String bindto = Integer.parseInt(dict.get("bindto")) == 0 ? "Item" : "Slot";
        String key = "";
        if(Integer.parseInt(dict.get("bindto")) == 1)
            key = String.valueOf(player.getInventory().getHeldItemSlot());
        else
            key = String.valueOf(player.getItemInHand().getTypeId()) + ":" + String.valueOf(player.getItemInHand().getDurability());
        String cmd = dict.get(key);
        int click = Character.getNumericValue(cmd.trim().charAt(0));
        cmd = cmd.substring(1);
        player.sendRawMessage(ChatColor.GOLD + "Item Commands");
        player.sendRawMessage(infoc + "Commands are set "+(this.per == 1 ? "per player" : "globally") + "and Binded to "+bindto+"s");
        player.sendRawMessage(infoc + "Triggered by a " + (click == 0 ? "right" : "left") + " click , Current Command: "+descc+cmd);

        if(checkPermissions(player, "ICmds.create", createNeedOP)){
            player.sendRawMessage(cmdc + "Usage: /ic add [command] "+descc+"#Add command to the selected "+bindto);
            player.sendRawMessage(cmdc + "/ic remove "+descc+"#Remove command from the selected "+bindto);
            player.sendRawMessage(cmdc + "/ic click [left l|right r] "+descc+"#Change trigger of the selected "+bindto);
            i++;}
        if((per == 0 && checkPermissions(player, "ICmds.create", createNeedOP))
                || (per == 1 && checkPermissions(player, "ICmds.use", useNeedOP))) {
            player.sendRawMessage(cmdc + "/ic bindto [item i|slot s] "+descc+"#Binds commands to slots or items");
            i++;}
        if(checkPermissions(player, "ICmds.use", useNeedOP)){
            player.sendRawMessage(cmdc + "/ic list "+descc+"#List commands available to you");
            i++;}
        if(checkPermissions(player, "ICmds.admin", adminNeedOP)){
            player.sendRawMessage(cmdc + "/ic per [player pl|global gl] "+descc+"#Set commands globally or per player");
            player.sendRawMessage(cmdc + "/ic reload");
            i++;}
        if(i == 0)
            player.sendRawMessage(cmdc + "No Permissions to use ItemCommands");
    }
    public Dictionary<String, String> getDict(Player player)
    {
        Dictionary<String, String> dict = null;
        String i = this.per == 1 ? player.getName() : "";
        if((dict=players.get(i)) == null){
            players.put(i, dict=new Hashtable<String, String>());
            dict.put("bindto", String.valueOf(keybindings));
            dict.put("click", String.valueOf(bclick));
        }
        return dict;
    }
    public Dictionary<String, String> getDict(String player)
    {
        Dictionary<String, String> dict = null;
        String i = this.per == 1 ? player : "";
        if((dict=players.get(i)) == null){
            players.put(i, dict=new Hashtable<String, String>());
            dict.put("bindto", String.valueOf(keybindings));
            dict.put("click", String.valueOf(bclick));
        }
        return dict;
    }
}
