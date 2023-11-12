package com.poompk.LobbyPresents;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class Listener_18 implements Listener{
	@SuppressWarnings({ "deprecation" })
	@EventHandler
	public void ClickPresents(PlayerInteractEvent e){
		Player p = e.getPlayer();
		Block b = e.getClickedBlock();
		int max = LobbyPresents.getInstance().getConfig().getInt("Max");
    	if(b == null) {
    		return;
    	}
    	if(b.getType() != Material.SKULL) {
    		return;
    	}
    	if(p.getItemInHand().getType() == Material.AIR && e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK){
			if(e.getClickedBlock().getType() == Material.SKULL){
				if(b.getData() == 0 || b.getData() == 1 || b.getData() == 2 || b.getData() == 4 || b.getData() == 3|| b.getData() == 5){
					if(LobbyPresents.getInstance().local.get(b.getLocation()) != null) {
						if(LobbyPresents.getInstance().ClaimedList(p.getUniqueId()).contains(LobbyPresents.getInstance().local.get(b.getLocation())+"") == true) {
							String total = ""+LobbyPresents.getInstance().ClaimedList(p.getUniqueId()).size();
							try {
							p.playSound(p.getLocation(), Sound.valueOf(LobbyPresents.getInstance().getConfig().getString("Sounds.alreadyfound")), 1, 1);
							} catch (IllegalArgumentException excep) {
									Bukkit.getLogger().warning("LobbyPresents: Not found sound "+LobbyPresents.getInstance().getConfig().getString("Sounds.alreadyfound")+" on "+Bukkit.getBukkitVersion());
							}
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', LobbyPresents.getMessage(p, LobbyPresents.getInstance().getConfig().getString("Messages.alreadyfound").replaceAll("%max%", max+"").replaceAll("%found%", total).replaceAll("%player%", p.getName()))));
							
						} else {
							String total = "";
							if(LobbyPresents.getInstance().getClaimed(p.getUniqueId()).equals("#") || (LobbyPresents.getInstance().getClaimed(p.getUniqueId()).equals(""))) {
								LobbyPresents.getInstance().Claiming(p.getUniqueId(), LobbyPresents.getInstance().local.get(b.getLocation()));
							total = ""+LobbyPresents.getInstance().ClaimedList(p.getUniqueId()).size();
							 for(String cmds : LobbyPresents.getInstance().getConfig().getStringList("Rewards."+total)) {
								 Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmds.replaceAll("%player%", p.getName()));
							 }
							 LobbyPresents.getInstance().effectclick(p);
							 LobbyPresents.getInstance().FoundSound(p);;
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', LobbyPresents.getMessage(p, LobbyPresents.getInstance().getConfig().getString("Messages.found").replaceAll("%max%", max+"").replaceAll("%found%", total).replaceAll("%player%", p.getName()))));
							} else {
								LobbyPresents.getInstance().Claiming(p.getUniqueId(), LobbyPresents.getInstance().local.get(b.getLocation()));
							total = ""+LobbyPresents.getInstance().ClaimedList(p.getUniqueId()).size();
							 for(String cmds : LobbyPresents.getInstance().getConfig().getStringList("Rewards."+total)) {
								 Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmds.replaceAll("%player%", p.getName()));
							 }
							 LobbyPresents.getInstance().effectclick(p);
							 LobbyPresents.getInstance().FoundSound(p);
							 Boolean complete = (LobbyPresents.getInstance().ClaimedList(p.getUniqueId()).size() == max);
							 if(complete == false) {
								 p.sendMessage(ChatColor.translateAlternateColorCodes('&', LobbyPresents.getMessage(p, LobbyPresents.getInstance().getConfig().getString("Messages.found").replaceAll("%max%", max+"").replaceAll("%found%", total).replaceAll("%status%", total+"/"+max).replaceAll("%player%", p.getName()))));
							} else {
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', LobbyPresents.getMessage(p, LobbyPresents.getInstance().getConfig().getString("Messages.completed").replaceAll("%max%", max+"").replaceAll("%found%", total).replaceAll("%status%", total+"/"+max).replaceAll("%player%", p.getName()))));
							}
							}
						}
							
					}
				}
			}
    	}
	}
}
