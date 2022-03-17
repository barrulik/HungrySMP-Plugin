package me.barrulik.hungrysmp;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class HungrySMP extends JavaPlugin implements Listener {
    HungrySMP instance;
    @Override
    public void onEnable() {
        instance = this;
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        System.out.println("HungrySMP by Barrulik");

        // ontick
        new BukkitRunnable(){
            @Override
            public void run(){
                for (Player p : getServer().getOnlinePlayers()){
                    int maxHunger = getMaxHunger(p);
                    if (p.getFoodLevel()>maxHunger) {
                        p.setFoodLevel(maxHunger);
                        p.sendMessage("Oh no, you have reached your max hunger amount");
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        Entity killer = event.getEntity().getKiller();
        if (killer instanceof Player) {
            Player killer_ = ((Player) killer).getPlayer();
            setMaxHunger(dead, getMaxHunger(dead)-2);
            setMaxHunger(killer_, getMaxHunger(killer_)+2);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("resetHunger")) {
            for (Player target : Bukkit.getServer().getOnlinePlayers()) {
                setMaxHunger(target, 20);
            }
            Bukkit.getServer().broadcastMessage(ChatColor.RED + "hunger have been resetted.");
        }

        if (command.getName().equalsIgnoreCase("maxhunger")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.sendMessage(ChatColor.AQUA + "Your max hunger points is " + getMaxHunger(p));
            }
        }
        return true;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



    public int getMaxHunger(Player p){
        List<String> stringList = getConfig().getStringList("Players");
        List<Integer> intList = getConfig().getIntegerList("Max-hunger");
        int i = stringList.indexOf(p.getUniqueId().toString());
        if (i != -1){
            return intList.get(i);
        }
        setMaxHunger(p, 20);
        return 20;
    }

    public void setMaxHunger(Player p, int num){
        List<String> stringList = getConfig().getStringList("Players");
        List<Integer> intList = getConfig().getIntegerList("Max-hunger");
        int i = stringList.indexOf(p.getUniqueId().toString());
        if (i != -1){
            if (num!=0)
            intList.set(i, num);
        } else {
            stringList.add(p.getUniqueId().toString());
            intList.add(num);
        }
        getConfig().set("Players", stringList);
        getConfig().set("Max-hunger", intList);
        saveConfig();
    }
}

