package com.ttsp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Items {
    private Main plugin;
    public Items(Main plugin) {
        this.plugin = plugin;
    }
    public ItemStack mediane(){
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack medianeGreen(){
        ItemStack item = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack medianeRed(){
        ItemStack item = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack infoBlock(Player sender,Player receiver, String operationStatus){
        if(operationStatus.equals("default")){
            ItemStack item = new ItemStack(Material.COAL_BLOCK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+"Готовность игроков:");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.RED+"Sender: "+sender.getName());
            lore.add(ChatColor.RED+"Receiver: "+receiver.getName());
            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;

        }else
        if(operationStatus.equals("senderReady")){
            ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+"Готовность игроков:");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN+"Sender: "+sender.getName());
            lore.add(ChatColor.RED+"Receiver: "+receiver.getName());
            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;
        }else
        if(operationStatus.equals("receiverReady")){
            ItemStack item = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+"Готовность игроков:");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.RED+"Sender: "+sender.getName());
            lore.add(ChatColor.GREEN+"Receiver: "+receiver.getName());
            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;
        }else
        if(operationStatus.equals("allReady")){
            ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+"Готовность игроков:");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN+"Sender: "+sender.getName());
            lore.add(ChatColor.GREEN+"Receiver: "+receiver.getName());
            lore.add(ChatColor.GREEN+"Все приинял обмен!");
            meta.setLore(lore);
            item.setItemMeta(meta);
            return item;
        }else{
            return null;
        }
    }
    public ItemStack acceptBlock(){
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+"Принять обмен");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN+"Нажмите что бы принять обмен");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack declineBlock(){
        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.RED+"Отклонить обмен");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.RED+"Нажмите что бы отменить готовность к обмену");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack paperMoneyItem(Integer amount, Boolean isPositive){
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        if(isPositive){
            meta.setDisplayName(ChatColor.GREEN+"+"+amount);
            lore.add(ChatColor.GREEN+"Нажмите, что бы добавить: "+amount+"$");
        }else{
            meta.setDisplayName(ChatColor.RED+"-"+amount);
            lore.add(ChatColor.RED+"Нажмите, что бы отнять: "+amount+"$");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack moneySendInfoItem(Double amount){
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA+"Нажмите, что бы вернутся в инвентарь трейда");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE+"Нажмите, что бы установить: "+amount+"$");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public ItemStack mainMoneyInfo(Double amount, Boolean isSender){
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        if(isSender){
            meta.setDisplayName(ChatColor.GOLD+"Нажмите, что бы изменить");
            lore.add(ChatColor.GOLD+"После обмена с вашего счета будет списано: "+amount+"$");
        }else{
            meta.setDisplayName(ChatColor.GOLD+"Предложение вашего торгового партнера:");
            lore.add(ChatColor.GOLD+"После обмена к вам на счет будет добавлено: "+amount+"$");
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
