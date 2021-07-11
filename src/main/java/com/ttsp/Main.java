package com.ttsp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Main extends JavaPlugin implements Listener,Runnable {
    public InvChecks ic = new InvChecks(this);
    public Inventories inv = new Inventories(this);
    public HashMap<Player,Player> trades = new HashMap<>();
    public HashMap<Player,Integer> timer = new HashMap<>();
    public HashMap<Player, Inventory> receiverTrades = new HashMap<>();
    public HashMap<Player,Inventory> senderTrades = new HashMap<>();
    public HashMap<Player,Inventory> receiverMoney = new HashMap<>();
    public HashMap<Player,Inventory> senderMoney = new HashMap<>();
    public HashMap<Player,Double> moneyTrades = new HashMap<>();
    public HashMap<Player,Boolean> transitions = new HashMap<>();
    public HashMap<Player,Boolean> playersInMainTradeInv = new HashMap<>();
    public HashMap<Player,Boolean> tradeAccept = new HashMap<>();
    public Items it = new Items(this);
    //public HashMap<List<String>,Integer> tradeTime = new HashMap<>();
    //public HashMap<List<String>, Boolean> acceptedTrades = new HashMap<>();
    @Override
    public void onEnable() {
        getLogger().info("TTSP enabled!");
        Bukkit.getPluginManager().registerEvents(this,this);
        getCommand("trade").setExecutor(this);
        getCommand("tradetest").setExecutor(this);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this,this,0,20);
        EconomyManager.init();

    }

    @Override
    public void onDisable() {
        getLogger().info("TTSP disabled");
    }

    @Override
    public boolean onCommand(CommandSender snd, Command command, String label, String[] args) {
        if(command.getName().equals("tradetest")){
            if(!(snd instanceof Player)) return true;
            //Player sender = (Player) snd;
            //sender.openInventory(inv.tradeMainInventory());
            return true;
        }else{
            if(!(snd instanceof Player)) return true;
            if(args.length !=1)return false;

            Player sender = (Player) snd;

            if(args[0].equals("accept")){
                if(trades.containsKey(sender)){
                    Player receiver = sender;
                    sender = trades.get(receiver);
                    if(trades.get(receiver).isOnline()){
                        senderTrades.put(sender,inv.tradeMainInventory(sender,receiver));
                        senderMoney.put(sender,inv.moneySendInventory());
                        sender.openInventory(senderTrades.get(sender));

                        receiverTrades.put(receiver,inv.tradeMainInventory(sender,receiver));
                        receiverMoney.put(receiver, inv.moneySendInventory());
                        receiver.openInventory(receiverTrades.get(receiver));
                        sender.sendMessage("Player accept ur trade");
                        receiver.sendMessage("u accept trade");
                        //getLogger().info(String.valueOf(trades));
                        //getLogger().info(String.valueOf(receiverTrades));
                        //getLogger().info(String.valueOf(senderTrades));

                    }else{
                        receiver.sendMessage("Player who send trade is offline");
                    }
                    moneyTrades.put(receiver, (double) 0);
                    moneyTrades.put(sender, (double) 0);
                    tradeAccept.put(receiver,false);
                    tradeAccept.put(sender,false);
                    transitions.put(receiver, false);
                    transitions.put(sender,false);
                    playersInMainTradeInv.put(receiver,true);
                    playersInMainTradeInv.put(sender,true);
                    //trades.remove(receiver);
                    timer.remove(receiver);

                }
                return true;
            }
            if(args[0].equals("decline")){
                if(trades.containsKey(sender)){
                    Player receiver = sender;
                    sender = trades.get(receiver);


                    if(trades.get(sender).isOnline()){
                        trades.get(sender).sendMessage("Receiver is not accept your trade");
                    }
                    if(trades.get(receiver).isOnline()){
                        trades.get(receiver).sendMessage("You cancel trade");
                    }
                    trades.remove(receiver);
                    timer.remove(receiver);

                }
                return  true;
            }
            Player receiver = Bukkit.getPlayer(args[0]);
            if(receiver == null) return false;
            if(receiver.equals(sender)) return false;
            if(trades.containsKey(receiver)|| trades.containsKey(sender)) return true;
            trades.put(receiver,sender);
            timer.put(receiver,0);
            sender.sendMessage("trade request created for "+receiver);
            receiver.sendMessage("You got a trade from "+sender);
            return true;
        }

    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null) return;
        Player p = (Player) e.getWhoClicked();
        Inventory playerInv = p.getInventory();
        if(trades.containsKey(p) || trades.containsValue(p)){
            if(e.getClickedInventory().equals(playerInv)){
                //getLogger().info(String.valueOf(e.getCursor()));
                //getLogger().info(String.valueOf(e.getCurrentItem()));
                if(playersInMainTradeInv.get(p)){
                    if(e.getCurrentItem()!=null){
                        e.setCancelled(true);
                        ic.declineTradeRequest(p);
                        ItemStack clickedItem = e.getCurrentItem();
                        Integer slot = e.getSlot();
                        Integer emptySlot = null;
                        e.getClickedInventory().setItem(slot,null);
                        if(senderTrades.containsKey(p)){
                            for(int i=0;i<17;i++){
                                if(senderTrades.get(p).getItem(i+27) == null && i!=8 && i!=17){
                                    emptySlot = i+27;
                                    break;
                                }
                            }
                            if(emptySlot != null){
                                senderTrades.get(p).setItem(emptySlot,clickedItem);
                                receiverTrades.get(getKeyByValue(trades,p)).setItem(emptySlot-27,clickedItem);
                                transitions.replace(p,true);

                                p.openInventory(senderTrades.get(p));
                                if(playersInMainTradeInv.get(getKeyByValue(trades,p))){
                                    transitions.replace(getKeyByValue(trades,p),true);
                                    getKeyByValue(trades,p).openInventory(receiverTrades.get(getKeyByValue(trades,p)));
                                }

                            }

                        }else if(receiverTrades.containsKey(p)){
                            for(int i=0;i<17;i++){
                                if(receiverTrades.get(p).getItem(i+27) == null){
                                    emptySlot = i+27;
                                    break;
                                }
                            }
                            if(emptySlot != null){
                                receiverTrades.get(p).setItem(emptySlot,clickedItem);
                                senderTrades.get(trades.get(p)).setItem(emptySlot-27,clickedItem);
                                transitions.replace(p,true);

                                p.openInventory(receiverTrades.get(p));
                                if(playersInMainTradeInv.get(trades.get(p))){
                                    transitions.replace(trades.get(p),true);
                                    trades.get(p).openInventory(senderTrades.get(trades.get(p)));
                                }

                            }
                        }
                    }
                }else{e.setCancelled(true);}
            }
            if(e.getClickedInventory().equals(receiverMoney.get(p)) || e.getClickedInventory().equals(senderMoney.get(p))){
                e.setCancelled(true);
                if(e.getSlot() == 4){
                    if(receiverMoney.containsKey(p)){
                        Player sender = trades.get(p);
                        //getLogger().info("Pass trade1");
                        receiverTrades.get(p).setItem(44,it.mainMoneyInfo(moneyTrades.get(p),true));
                        //getLogger().info("Pass trade2");
                        senderTrades.get(sender).setItem(17,it.mainMoneyInfo(moneyTrades.get(p),false));
                        transitions.replace(p,true);
                        //getLogger().info("Pass trade3");
                        p.openInventory(receiverTrades.get(p));
                        //getLogger().info("Pass trade4");
                        playersInMainTradeInv.replace(p,true);
                        if(playersInMainTradeInv.get(sender)){
                            //getLogger().info("Pass trade5");
                            transitions.replace(sender,true);
                            sender.openInventory(senderTrades.get(sender));
                            //getLogger().info("Pass trade6");
                        }
                    }
                    if(senderMoney.containsKey(p)){
                        Player receiver = getKeyByValue(trades,p);
                        //getLogger().info("Pass trade1");
                        receiverTrades.get(receiver).setItem(17,it.mainMoneyInfo(moneyTrades.get(p),false));
                        //getLogger().info("Pass trade2");
                        senderTrades.get(p).setItem(44,it.mainMoneyInfo(moneyTrades.get(p),true));
                        transitions.replace(p,true);
                        //getLogger().info("Pass trade3");
                        p.openInventory(senderTrades.get(p));
                        //getLogger().info("Pass trade4");
                        playersInMainTradeInv.replace(p,true);
                        if(playersInMainTradeInv.get(receiver)){
                            //getLogger().info("Pass trade5");
                            transitions.replace(receiver,true);
                            receiver.openInventory(receiverTrades.get(receiver));
                            //getLogger().info("Pass trade6");
                        }
                    }
                }
                if(e.getSlot() < 4){
                    Integer base = 1;
                    if(e.isShiftClick()){
                        base = 10000;
                    }
                    Double cash = moneyTrades.get(p)+(base*(Math.pow(10,3-e.getSlot())));
                    if(EconomyManager.isEnoughMoney(p,cash)){
                        moneyTrades.replace(p,cash);
                        if(receiverMoney.containsKey(p)){
                            transitions.replace(p,true);
                            receiverMoney.get(p).setItem(4,it.moneySendInfoItem(moneyTrades.get(p)));
                            p.openInventory(receiverMoney.get(p));
                        }
                        if(senderMoney.containsKey(p)){
                            transitions.replace(p,true);
                            senderMoney.get(p).setItem(4,it.moneySendInfoItem(moneyTrades.get(p)));
                            p.openInventory(senderMoney.get(p));
                        }
                    }
                }
                if(e.getSlot()>4){
                    Integer base = 1;
                    if(e.isShiftClick()){
                        base = 10000;
                    }
                    Double cash = moneyTrades.get(p)-(base*(Math.pow(10,e.getSlot()-5)));
                    if(cash < 0){
                        cash = Double.valueOf(0);
                    }
                    moneyTrades.replace(p,cash);
                    if(receiverMoney.containsKey(p)){
                        transitions.replace(p,true);
                        receiverMoney.get(p).setItem(4,it.moneySendInfoItem(moneyTrades.get(p)));
                        p.openInventory(receiverMoney.get(p));
                    }
                    if(senderMoney.containsKey(p)){
                        transitions.replace(p,true);
                        senderMoney.get(p).setItem(4,it.moneySendInfoItem(moneyTrades.get(p)));
                        p.openInventory(senderMoney.get(p));
                    }
                }
            }
            if(e.getClickedInventory().equals(receiverTrades.get(p))||e.getClickedInventory().equals(senderTrades.get(p))){
                e.setCancelled(true);

                if(e.getCurrentItem() != null){
                    //getLogger().info(String.valueOf(e.getCurrentItem()));
                    if(e.getSlot()<27 || e.getSlot() == 35 || e.getSlot() == 44){
                        if(e.getSlot() == 35){
                            if(!tradeAccept.get(p)){
                                tradeAccept.replace(p,true);
                                if(receiverTrades.containsKey(p)){
                                    Player sender = trades.get(p);
                                    if(tradeAccept.get(sender)){
                                        //code
                                        for(int i=18;i<27;i++){
                                            if( i < 22){
                                                senderTrades.get(sender).setItem(i,it.medianeGreen());
                                                receiverTrades.get(p).setItem(i,it.medianeGreen());
                                            }else if(i>22){
                                                senderTrades.get(sender).setItem(i,it.medianeGreen());
                                                receiverTrades.get(p).setItem(i,it.medianeGreen());
                                            }else{
                                                senderTrades.get(sender).setItem(i,it.infoBlock(sender,p,"allReady"));
                                                receiverTrades.get(p).setItem(i,it.infoBlock(sender,p,"allReady"));
                                            }


                                        }
                                        receiverTrades.get(p).setItem(35,it.declineBlock());
                                        transitions.replace(p,true);

                                        if(playersInMainTradeInv.get(sender)){
                                            transitions.replace(sender,true);
                                            sender.openInventory(senderTrades.get(sender));
                                        }
                                        p.openInventory(receiverTrades.get(p));
                                        timer.put(p,0);

                                    }else{

                                        for(int i=18;i<27;i++){
                                            if( i < 22){
                                                senderTrades.get(sender).setItem(i,it.medianeGreen());
                                                receiverTrades.get(p).setItem(i,it.medianeGreen());
                                            }else if(i>22){
                                                senderTrades.get(sender).setItem(i,it.medianeRed());
                                                receiverTrades.get(p).setItem(i,it.medianeRed());
                                            }else{
                                                senderTrades.get(sender).setItem(i,it.infoBlock(sender,p,"receiverReady"));
                                                receiverTrades.get(p).setItem(i,it.infoBlock(sender,p,"receiverReady"));
                                            }

                                        }
                                        receiverTrades.get(p).setItem(35,it.declineBlock());
                                        transitions.replace(p,true);

                                        if(playersInMainTradeInv.get(sender)){
                                            transitions.replace(sender,true);
                                            sender.openInventory(senderTrades.get(sender));
                                        }
                                        p.openInventory(receiverTrades.get(p));
                                    }
                                }
                                if(senderTrades.containsKey(p)){
                                    Player receiver = getKeyByValue(trades,p);
                                    if(tradeAccept.get(receiver)){
                                        //code
                                        for(int i=18;i<27;i++){
                                            if( i < 22){
                                                senderTrades.get(p).setItem(i,it.medianeGreen());
                                                receiverTrades.get(receiver).setItem(i,it.medianeGreen());
                                            }else if(i>22){
                                                senderTrades.get(p).setItem(i,it.medianeGreen());
                                                receiverTrades.get(receiver).setItem(i,it.medianeGreen());
                                            }else{
                                                senderTrades.get(p).setItem(i,it.infoBlock(p,receiver,"allReady"));
                                                receiverTrades.get(receiver).setItem(i,it.infoBlock(p,receiver,"allReady"));
                                            }


                                        }
                                        senderTrades.get(p).setItem(35,it.declineBlock());
                                        transitions.replace(p,true);
                                        p.openInventory(senderTrades.get(p));
                                        if(playersInMainTradeInv.get(receiver)){
                                            transitions.replace(receiver,true);
                                            receiver.openInventory(receiverTrades.get(receiver));
                                        }
                                        timer.put(receiver,0);

                                    }else{

                                        for(int i=18;i<27;i++){
                                            if( i < 22){
                                                senderTrades.get(p).setItem(i,it.medianeGreen());
                                                receiverTrades.get(receiver).setItem(i,it.medianeGreen());
                                            }else if(i>22){
                                                senderTrades.get(p).setItem(i,it.medianeRed());
                                                receiverTrades.get(receiver).setItem(i,it.medianeRed());
                                            }else{
                                                senderTrades.get(p).setItem(i,it.infoBlock(p,receiver,"senderReady"));
                                                receiverTrades.get(receiver).setItem(i,it.infoBlock(p,receiver,"senderReady"));
                                            }

                                        }
                                        senderTrades.get(p).setItem(35,it.declineBlock());
                                        transitions.replace(p,true);
                                        if(playersInMainTradeInv.get(receiver)){
                                            transitions.replace(receiver,true);
                                            receiver.openInventory(receiverTrades.get(receiver));
                                        }

                                        p.openInventory(senderTrades.get(p));

                                    }
                                }
                            }else{

                                ic.declineTradeRequest(p);
                            }


                        }
                        if(e.getSlot() == 44){
                            transitions.replace(p,true);
                            playersInMainTradeInv.replace(p,false);
                            if(receiverTrades.containsKey(p)){
                                p.openInventory(receiverMoney.get(p));
                            }
                            if(senderTrades.containsKey(p)){
                                p.openInventory(senderMoney.get(p));
                            }
                        }
                    }else{
                        ItemStack item = e.getCurrentItem();
                        ic.declineTradeRequest(p);
                        if(receiverTrades.containsKey(p)){
                            if(e.getSlot() != 35 && e.getSlot() != 44){
                                Player sender = trades.get(p);
                                receiverTrades.get(p).setItem(e.getSlot(),null);
                                senderTrades.get(sender).setItem(e.getSlot()-27,null);
                                p.getInventory().addItem(item);
                                transitions.replace(p,true);
                                transitions.replace(sender,true);
                                sender.openInventory(senderTrades.get(sender));
                                p.openInventory(receiverTrades.get(p));
                            }

                        }else if(senderTrades.containsKey(p)){

                                if(e.getSlot() != 35 && e.getSlot() != 44){
                                    Player receiver = getKeyByValue(trades,p);
                                    senderTrades.get(p).setItem(e.getSlot(),null);
                                    receiverTrades.get(receiver).setItem(e.getSlot()-27,null);
                                    p.getInventory().addItem(item);
                                    transitions.replace(p,true);
                                    transitions.replace(receiver,true);
                                    receiver.openInventory(receiverTrades.get(receiver));
                                    p.openInventory(senderTrades.get(p));
                                }



                    }
                        //getLogger().info(String.valueOf(e.getCursor()));
                        //getLogger().info(String.valueOf(e.getCurrentItem()));
                }

            }
            }
        }

    }

    @EventHandler
    public void onInventoryExit(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        //getLogger().info(String.valueOf(p));
        //getLogger().info(String.valueOf(receiverTrades.containsKey(p)));
        //getLogger().info(String.valueOf(senderTrades.containsKey(p)));
        //getLogger().info(String.valueOf(receiverTrades.keySet()));

        if(receiverTrades.containsKey(p)){

            Player sender = trades.get(p);

            //getLogger().info("pass");
            //getLogger().info(String.valueOf(closedInventory.getContents()));
            //getLogger().info(String.valueOf(closedInventory.getStorageContents()));
            //Set<ItemStack> items = new HashSet<ItemStack>();
            if(!transitions.get(p)){
                Inventory closedInventory = e.getInventory();
                for(int i=27; i<44;i++){
                    if(i != 35){
                        if(closedInventory.getItem(i) != null){
                            //items.add(closedInventory.getItem(i));
                            p.getInventory().addItem(closedInventory.getItem(i));
                            //getLogger().info(String.valueOf(closedInventory.getItem(i)));
                        }

                    }
                }
                receiverTrades.remove(p);
                //getLogger().info("receiverTrades DELETED!!!!!!");
                if(timer.containsKey(p)){
                    timer.remove(p);
                }
                if(senderTrades.containsKey(sender)){
                    sender.closeInventory();
                }else{
                    receiverMoney.remove(p);
                    moneyTrades.remove(p);
                    tradeAccept.remove(p);
                    playersInMainTradeInv.remove(p);
                    transitions.remove(p);
                    trades.remove(p);}    
            }else{
                //getLogger().info("pass");
                transitions.replace(p,false);
            }
            
            
            

        }else if(senderTrades.containsKey(p)){
            Player receiver = getKeyByValue(trades,p);
            //getLogger().info(String.valueOf(trades));

            //getLogger().info("pass");

            //getLogger().info(String.valueOf(closedInventory.getContents()));
            //getLogger().info(String.valueOf(closedInventory.getStorageContents()));
            //Set<ItemStack> items = new HashSet<ItemStack>();
            if(!transitions.get(p)){
                Inventory closedInventory = e.getInventory();
                for(int i=27; i<44;i++){
                    if(i != 35){
                        if(closedInventory.getItem(i) != null){
                            //items.add(closedInventory.getItem(i));
                            p.getInventory().addItem(closedInventory.getItem(i));
                            //getLogger().info(String.valueOf(closedInventory.getItem(i)));
                        }

                    }
                }


                senderTrades.remove(p);
                //getLogger().info("senderTrades DELETED!!!!!!");
                if(receiverTrades.containsKey(receiver)){
                    receiver.closeInventory();
                }else{
                    senderMoney.remove(p);
                    moneyTrades.remove(p);
                    tradeAccept.remove(p);
                    playersInMainTradeInv.remove(p);
                    transitions.remove(p);
                    trades.remove(receiver);}
            }else{
                //getLogger().info("pass");
                transitions.replace(p,false);
            }
            

        }
    }


    @Override
    public void run() {
        Set<Player> players = timer.keySet();
        for(Player p: players){
            if(receiverTrades.containsKey(p)){
                timer.replace(p,timer.get(p)+1);
                if(timer.get(p) >= 5){
                    //trade
                    Player sender = trades.get(p);
                    for(int i=0;i<17;i++){
                        if(receiverTrades.get(p).getItem(i+27) != null && i != 8 && i!=17){
                           sender.getInventory().addItem(receiverTrades.get(p).getItem(i+27));
                           receiverTrades.get(p).setItem(i+27,null);

                        }
                    }
                    for(int i=0;i<17;i++){
                        //getLogger().info(String.valueOf(sender));
                        //getLogger().info(String.valueOf(senderTrades));
                        //getLogger().info(String.valueOf(senderTrades.containsKey(sender)));
                        if(senderTrades.get(sender).getItem(i+27) != null && i != 8 && i!=17){
                            p.getInventory().addItem(senderTrades.get(sender).getItem(i+27));
                            senderTrades.get(sender).setItem(i+27,null);

                        }
                    }
                    if(moneyTrades.get(p) != 0){
                        EconomyManager.sendMoney(p,sender,moneyTrades.get(p));
                    }
                    if(moneyTrades.get(sender) != 0){
                        EconomyManager.sendMoney(sender,p,moneyTrades.get(sender));
                    }
                    transitions.replace(p,true);
                    transitions.replace(sender,true);
                    p.openInventory(receiverTrades.get(p));
                    sender.openInventory(senderTrades.get(sender));
                    p.closeInventory();
                    sender.closeInventory();
                }
            }else{
                timer.replace(p,timer.get(p)+1);
                //getLogger().info(String.valueOf(timer.get(p)));
                if(timer.get(p) >= 60){

                    if(trades.get(p).isOnline()){
                        trades.get(p).sendMessage("Receiver is not accept your trade");
                    }
                    timer.remove(p);
                    trades.remove(p);

                }
            }

        }
    }
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
