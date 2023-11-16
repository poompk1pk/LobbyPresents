package com.github.poompk1pk.lobbypresents.presents.placeholderapi;

import com.github.poompk1pk.lobbypresents.utils.PresentsUtils;
import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class LobbyPresentsExpansion extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "poompk";
    }
    
    @Override
    public String getIdentifier() {
        return "lobbypresents";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public String onRequest(OfflinePlayer player, String identifier) {
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