package com.Cutch.bukkit.ICmds;

import org.bukkit.Material;

public class Item {

    public int id = 0;
    public int amount = 1;
    public int damage = 0;
    private String samount = "";
    private String name = "";
    public Item(String str)
    {
        name = Material.getMaterial(id).name();
        if(str.startsWith("$")){
            id = -2;
            amount = Integer.parseInt(str.substring(2));
            if(str.charAt(1) == '-')
                amount*=-1;
        }
        else if(str.startsWith("*")){
            id = -3;
            amount = Integer.parseInt(str.substring(2));
            if(str.charAt(1) == '-')
                amount*=-1;
        }
        else
        {
            int iamount = str.indexOf("+");
            if(iamount == -1){
                iamount = str.indexOf("-");}
            int col=str.indexOf(":");
            if(col != -1)
            {
                id = Integer.parseInt(str.substring(0, col));
                damage = Integer.parseInt(str.substring(col+1, iamount));
                amount = Integer.parseInt(str.substring(iamount+1));
            } else {
                id = Integer.parseInt(str.substring(0, iamount));
                amount = Integer.parseInt(str.substring(iamount+1));
            }
            if(str.charAt(iamount) == '-')
                amount*=-1;
        }
    }
    public Item(int id, int amount, int damage)
    {
        this.id = id;
        this.amount = amount;
        this.damage = damage;
    }
    @Override
    public String toString()
    {
        samount = amount < 0 ? "-" : "+";
        if(id >= 0)
            return id + ":" + damage + samount + String.valueOf(Math.abs(amount))+";";
        if(id == -3)
            return "*" + samount + String.valueOf(Math.abs(amount))+";";
        else if(id == -2)
            return "$" + samount + String.valueOf(Math.abs(amount)) + ";";
        return "";
    }
    public String getName()
    {
        return name;
    }
}
