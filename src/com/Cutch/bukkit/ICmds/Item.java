package com.Cutch.bukkit.ICmds;

import org.bukkit.Material;

public class Item {

    public int id = 0;
    public int amount = 1;
    private boolean useAmount = true;
    public int damage = 0;
    private String samount = "";
    private String name = "";
    public Item(String str)
    {
        str = str.trim();
        try
        {
            this.id = Integer.parseInt(str);
            this.damage = -1;
            useAmount = false;
        }
        catch(NumberFormatException e)
        {
            try
            {
                String[] split = str.split(":");
                this.id = Integer.parseInt(split[0]);
                this.damage = Integer.parseInt(split[1]);
                useAmount = false;
            }
            catch(NumberFormatException e2)
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
        if(useAmount)
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
        else
        {
            return id + (damage != -1?":"+damage:"") + ";";
        }
    }
    public String getName()
    {
        return name;
    }
    public boolean equals(int id, int data)
    {
        if(data == -1)
            return this.id == id;
        return this.id == id && (this.damage == data || (this.damage == data - 1 && this.damage == -1));
    }
}
