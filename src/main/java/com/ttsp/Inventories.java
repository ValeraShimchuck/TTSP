package com.ttsp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Inventories {
    private Main plugin;
    public Inventories(Main plugin) {
        this.plugin=plugin;
    }
    public Inventory tradeMainInventory(Player sender, Player receiver){
    Inventory inv = Bukkit.createInventory(null,5*9,"trade");
    for(int i=18;i<27;i++){
        if(i!= 22){
            inv.setItem(i,plugin.it.mediane());
        }else{
            inv.setItem(i,plugin.it.infoBlock(sender,receiver,"default"));
        }

    }
    inv.setItem(44,plugin.it.mainMoneyInfo((double) 0,true));
    inv.setItem(17,plugin.it.mainMoneyInfo((double) 0,false));
    inv.setItem(35,plugin.it.acceptBlock());
    return inv;
    }
    public Inventory moneySendInventory(){
        Inventory inv = Bukkit.createInventory(null,9,"Set money");
        for(int i=0;i<9;i++){
            if(i < 4){
                inv.setItem(i,plugin.it.paperMoneyItem((int) Math.pow(10,3-i),true));
            }else if(i > 4){
                inv.setItem(i,plugin.it.paperMoneyItem((int) Math.pow(10,0+i-5),false));
            }
        }
        inv.setItem(4,plugin.it.moneySendInfoItem((double) 0));
        return inv;
    }

}
