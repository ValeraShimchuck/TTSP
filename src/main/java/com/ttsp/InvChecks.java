package com.ttsp;

import org.bukkit.entity.Player;

public class InvChecks {
    private Main plugin;
    public InvChecks(Main plugin) {
        this.plugin = plugin;
    }
    public void declineTradeRequest(Player p){
        if(plugin.receiverTrades.containsKey(p)){
            Player sender = plugin.trades.get(p);
            plugin.tradeAccept.replace(p,true);
            plugin.tradeAccept.replace(sender,true);
            for(int i=18;i<27;i++){
                if( i < 22){
                    plugin.senderTrades.get(sender).setItem(i,plugin.it.mediane());
                    plugin.receiverTrades.get(p).setItem(i,plugin.it.mediane());
                }else if(i>22){
                    plugin.senderTrades.get(sender).setItem(i,plugin.it.mediane());
                    plugin.receiverTrades.get(p).setItem(i,plugin.it.mediane());
                }else{
                    plugin.senderTrades.get(sender).setItem(i,plugin.it.infoBlock(sender,p,"default"));
                    plugin.receiverTrades.get(p).setItem(i,plugin.it.infoBlock(sender,p,"default"));
                }

            }
            plugin.tradeAccept.replace(p,false);
            plugin.tradeAccept.replace(sender,false);
            plugin.receiverTrades.get(p).setItem(35,plugin.it.acceptBlock());
            plugin.senderTrades.get(sender).setItem(35,plugin.it.acceptBlock());
            plugin.transitions.replace(p,true);
            if(plugin.playersInMainTradeInv.get(sender)){
                plugin.transitions.replace(sender,true);
                sender.openInventory(plugin.senderTrades.get(sender));
            }
            p.openInventory(plugin.receiverTrades.get(p));
            if(plugin.timer.containsKey(p)){
                plugin.timer.remove(p);
            }
        }
        if(plugin.senderTrades.containsKey(p)){
            Player receiver = plugin.getKeyByValue(plugin.trades,p);
            plugin.tradeAccept.replace(p,true);
            plugin.tradeAccept.replace(receiver,true);
            for(int i=18;i<27;i++){
                if( i < 22){
                    plugin.senderTrades.get(p).setItem(i,plugin.it.mediane());
                    plugin.receiverTrades.get(receiver).setItem(i,plugin.it.mediane());
                }else if(i>22){
                    plugin.senderTrades.get(p).setItem(i,plugin.it.mediane());
                    plugin.receiverTrades.get(receiver).setItem(i,plugin.it.mediane());
                }else{
                    plugin.senderTrades.get(p).setItem(i,plugin.it.infoBlock(p,receiver,"default"));
                    plugin.receiverTrades.get(receiver).setItem(i,plugin.it.infoBlock(p,receiver,"default"));
                }

            }
            plugin.tradeAccept.replace(p,false);
            plugin.tradeAccept.replace(receiver,false);
            plugin.receiverTrades.get(receiver).setItem(35,plugin.it.acceptBlock());
            plugin.senderTrades.get(p).setItem(35,plugin.it.acceptBlock());
            plugin.transitions.replace(p,true);
            if(plugin.playersInMainTradeInv.get(receiver)){
                plugin.transitions.replace(receiver,true);
                receiver.openInventory(plugin.receiverTrades.get(receiver));
            }
            p.openInventory(plugin.senderTrades.get(p));
            if(plugin.timer.containsKey(receiver)){
                plugin.timer.remove(receiver);
            }
        }

    }
}
