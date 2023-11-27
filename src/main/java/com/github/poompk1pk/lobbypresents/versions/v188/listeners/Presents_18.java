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
	  private final Location loc;
	  
	  private final BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	  
	  private final int id;
	  
	  public Presents_18(String[] loc, int id) {
	    this.loc = new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]));
	    this.id = id;
	    runEffect();
	    Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	  }
	  
	  public void unRegisterListener() {
	    HandlerList.unregisterAll(this);
	    this.scheduler.cancelTasks(Main.getInstance());
	  }
	  
	  public void runEffect() {
	    this.scheduler.runTaskTimerAsynchronously(Main.getInstance(), new Runnable() {
	          public void run() {
				  try {
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

				  } catch (NullPointerException e) {

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
	    if (PresentsUtils.config.get(ConfigType.Presents).getConfig().getString("presents." + getId() + ".custom-rewards") == null || PresentsUtils.config.get(ConfigType.Presents).getConfig().getString("presents." + getId() + ".custom-rewards").equalsIgnoreCase("none"))
	      return "none"; 
	    return PresentsUtils.config.get(ConfigType.Presents).getConfig().getString("presents." + getId() + ".custom-rewards");
	  }
	  
	  public void setCustomRewards(String s) {
	    PresentsUtils.config.get(ConfigType.Presents).getConfig().set("presents." + getId() + ".custom-rewards", s);
	    PresentsUtils.config.get(ConfigType.Presents).save();
	  }
	  
	  @EventHandler
	  public void onPlayerInteractPresentBoxes(PlayerInteractEvent e) {

	    if (e.getAction() != Action.RIGHT_CLICK_BLOCK || !e.getClickedBlock().getWorld().equals(getLocationPresents().getWorld()))
	      return; 
	    if (e.getClickedBlock().getType() != Material.valueOf("SKULL"))
	      return; 
	    if (!e.getClickedBlock().equals(getLocationPresents().getBlock()))
	      return;
		// async task prevents server freeze
		new BukkitRunnable() {
			@Override
			public void run() {
				if (PresentsUtils.getProfile(e.getPlayer().getUniqueId()).getClaim().contains(Integer.valueOf(getId()))) {

					try {
						e.getPlayer().playSound(getLocationPresents(), PresentsUtils.Sound_alreadyfound, 1.0F, 1.0F);
					} catch (NullPointerException e) {
						Main.getInstance().getLogger().warning("Not found sound: "+PresentsUtils.Sound_alreadyfound+" "+Main.getInstance().getVersion());
					}

					if (!PresentsUtils.Message_alreadyfound.equalsIgnoreCase("none"))
						PresentsUtils.chat(e.getPlayer(), Main.getMessage(e.getPlayer(), PresentsUtils.Message_alreadyfound.replace("%unfound%", String.valueOf(Main.getUnfound(e.getPlayer()))).replace("%player%", e.getPlayer().getDisplayName()).replace("%found%", String.valueOf(PresentsUtils.getProfile(e.getPlayer().getUniqueId()).getClaim().size())).replace("%total%", String.valueOf(PresentsUtils.total_present))));
					return;
				}
				Main.getPresents().FoundSound(e.getPlayer(), 1.0F);
				PresentsUtils.getProfile(e.getPlayer().getUniqueId()).addClaim(getId());
				// commands still need sync task
				new BukkitRunnable() {
					@Override
					public void run() {
						Bukkit.getServer().getPluginManager().callEvent(new PlayerClickClaimedPresentEvent(e.getPlayer(), getId(), getLocationPresents()));
					}
				}.runTask(Main.getInstance());


			}
		}.runTaskAsynchronously(Main.getInstance());

	  }
}
