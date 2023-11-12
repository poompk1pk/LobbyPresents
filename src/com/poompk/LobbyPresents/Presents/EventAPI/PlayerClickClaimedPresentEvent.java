package com.poompk.LobbyPresents.Presents.EventAPI;

import com.poompk.LobbyPresents.PresentsUtils;
import com.poompk.LobbyPresents.Profile;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerClickClaimedPresentEvent extends Event {
  private static final HandlerList handlers = new HandlerList();
  
  private Player p;
  
  private int id;
  
  private Location loc;
  
  public PlayerClickClaimedPresentEvent(Player p, int id, Location loc) {
    this.p = p;
    this.id = id;
    this.loc = loc;
  }
  
  public Player getPlayer() {
    return this.p;
  }
  
  public Location getLocation() {
    return this.loc;
  }
  
  public int getTotal() {
    return PresentsUtils.total_present;
  }
  
  public Profile getProfile() {
    return PresentsUtils.getProfile(this.p.getUniqueId());
  }
  
  public int getID() {
    return this.id;
  }
  
  public HandlerList getHandlers() {
    return handlers;
  }
  
  public static HandlerList getHandlerList() {
    return handlers;
  }
}
