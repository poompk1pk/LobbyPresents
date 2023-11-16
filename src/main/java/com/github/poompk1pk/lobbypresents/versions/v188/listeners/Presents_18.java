package com.github.poompk1pk.lobbypresents.versions.v188.listeners;

import com.github.poompk1pk.lobbypresents.config.ConfigFile;
import  com.github.poompk1pk.lobbypresents.Main;
import com.github.poompk1pk.lobbypresents.presents.api.events.PlayerClickClaimedPresentEvent;
import com.github.poompk1pk.lobbypresents.utils.PresentsUtils;
import  com.github.poompk1pk.lobbypresents.type.ConfigType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class Presents_18 implements Listener {
	  private Location loc;
	  
	  private BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	  
	  private int id;
	  
	  public Presents_18(String[] loc, int id) {
	    this.loc = new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]));
	    this.id = id;
	    runEffect();
	    Bukkit.getPluginManager().registerEvents(this, (Plugin)Main.getInstance());
	  }
	  
	  public void unRegisterListener() {
	    HandlerList.unregisterAll(this);
	    this.scheduler.cancelTasks((Plugin)Main.getInstance());
	  }
	  
	  public void runEffect() {
	    this.scheduler.runTaskTimerAsynchronously((Plugin)Main.getInstance(), new Runnable() {
	          public void run() {
	            for (Player pls : Bukkit.getOnlinePlayers()) {
	              if (pls.getWorld() == Presents_18.this.getLocationPresents().getWorld() && pls != null && 
	                pls.getLocation().distance(Presents_18.this.getLocationPresents()) <= PresentsUtils.distance) {
	                if (PresentsUtils.getProfile(pls.getUniqueId()).getClaim().contains(Integer.valueOf(Presents_18.this.getId()))) {
	                  Main.getPresents().HitEffect(pls, Presents_18.this.getLocationPresents().getBlock(), PresentsUtils.eeffect_claimed);
	                  continue;
	                } 
	                Main.getPresents().GreenVillager(pls, Presents_18.this.getLocationPresents().getBlock(), PresentsUtils.effect_canclaim);
	              } 
	            } 
	          }
	        },0L, PresentsUtils.ticksEffect);
	  }
	  
	  public int getId() {
	    return this.id;
	  }
	  
	  public Location getLocationPresents() {
	    return this.loc;
	  }
	  
	  public BukkitScheduler getScheduler() {
	    return this.scheduler;
	  }
	  
	  public String getCustomRewards() {
	    if (((ConfigFile)PresentsUtils.config.get(ConfigType.Presents)).getConfig().getString("presents." + getId() + ".custom-rewards") == null || ((ConfigFile)PresentsUtils.config.get(ConfigType.Presents)).getConfig().getString("presents." + getId() + ".custom-rewards").equalsIgnoreCase("none"))
	      return "none"; 
	    return ((ConfigFile)PresentsUtils.config.get(ConfigType.Presents)).getConfig().getString("presents." + getId() + ".custom-rewards");
	  }
	  
	  public void setCustomRewards(String s) {
	    ((ConfigFile)PresentsUtils.config.get(ConfigType.Presents)).getConfig().set("presents." + getId() + ".custom-rewards", s);
	    ((ConfigFile)PresentsUtils.config.get(ConfigType.Presents)).save();
	  }
	  
	  @EventHandler
	  public void onPlayerInteractPresentBoxes(PlayerInteractEvent e) {
	    if (e.getAction() != Action.RIGHT_CLICK_BLOCK || !e.getClickedBlock().getWorld().equals(getLocationPresents().getWorld()))
	      return; 
	    if (e.getClickedBlock().getType() != Material.valueOf("SKULL"))
	      return; 
	    if (!e.getClickedBlock().equals(getLocationPresents().getBlock()))
	      return;
		new BukkitRunnable() {
			@Override
			public void run() {
				if (PresentsUtils.getProfile(e.getPlayer().getUniqueId()).getClaim().contains(Integer.valueOf(getId()))) {
					e.getPlayer().playSound(getLocationPresents(), PresentsUtils.Sound_alreadyfound, 1.0F, 1.0F);
					if (!PresentsUtils.Message_alreadyfound.equalsIgnoreCase("none"))
						PresentsUtils.chat((CommandSender)e.getPlayer(), Main.getMessage(e.getPlayer(), PresentsUtils.Message_alreadyfound.replace("%unfound%", Main.getUnfound(e.getPlayer())+"").replace("%player%", e.getPlayer().getDisplayName()).replace("%found%", (new StringBuilder(String.valueOf(PresentsUtils.getProfile(e.getPlayer().getUniqueId()).getClaim().size()))).toString()).replace("%total%", (new StringBuilder(String.valueOf(PresentsUtils.total_present))).toString())));
					return;
				}
				Main.getPresents().FoundSound(e.getPlayer(), 1.0F);
				PresentsUtils.getProfile(e.getPlayer().getUniqueId()).addClaim(getId());
				new BukkitRunnable() {
					@Override
					public void run() {
						Bukkit.getServer().getPluginManager().callEvent((Event)new PlayerClickClaimedPresentEvent(e.getPlayer(), getId(), getLocationPresents()));
					}
				}.runTask(Main.getInstance());


			}
		}.runTaskAsynchronously(Main.getInstance());

	  }
}
