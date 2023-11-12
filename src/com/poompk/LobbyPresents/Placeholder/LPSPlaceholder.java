package com.poompk.LobbyPresents.Placeholder;

import com.poompk.LobbyPresents.PresentsUtils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class LPSPlaceholder extends PlaceholderExpansion {
  public String getAuthor() {
    return "PoomPK";
  }
  
  public String getIdentifier() {
    return "lobbypresents";
  }
  
  public String getVersion() {
    return "1.0";
  }
  
  public String onPlaceholderRequest(Player player, String identifier) {
    try {
      if (player == null)
        return ""; 
      if (identifier.equals("max"))
        return (new StringBuilder(String.valueOf(PresentsUtils.total_present))).toString(); 
      if (identifier.equals("found"))
        return (new StringBuilder(String.valueOf(PresentsUtils.getProfile(player.getUniqueId()).getClaim().size()))).toString(); 
      if (identifier.equals("unfound"))
        return (new StringBuilder(String.valueOf(PresentsUtils.total_present - PresentsUtils.getProfile(player.getUniqueId()).getClaim().size()))).toString(); 
      return "[LPS:404]";
    } catch (Exception e1) {
      return "loading";
    } 
  }
}
