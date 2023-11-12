package com.poompk.LobbyPresents;

import org.bukkit.OfflinePlayer;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class SomeExpansion extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "poompk";
    }
    
    @Override
    public String getIdentifier() {
        return "lobbypresent";
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