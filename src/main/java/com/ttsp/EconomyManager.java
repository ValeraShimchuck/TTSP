package com.ttsp;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class EconomyManager {
    private static Economy e;
    public static void init(){
        RegisteredServiceProvider<Economy> reg = Bukkit.getServicesManager().getRegistration(Economy.class);
        if(reg != null) e = reg.getProvider();
    }
    public static boolean sendMoney(Player sender, Player receiver, double amount){
        if(e == null) return false;
        if(e.getBalance(sender) < amount) return false;
        Boolean bool = e.withdrawPlayer(sender, amount).transactionSuccess();
        if(bool){
            return e.depositPlayer(receiver,amount).transactionSuccess();
        }else return false;
    }
    public static boolean isEnoughMoney(Player p, double amount){
        if(e == null) return false;
        if(e.getBalance(p) < amount) return false;
        return true;
    }
    public static Double getPlayerMoney(Player p){
        if(e == null) return null;
        Double money = e.getBalance(p);
        return money;
    }
}
