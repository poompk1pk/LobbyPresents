package com.poompk.LobbyPresents;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.external.EZPlaceholderHook;

@SuppressWarnings("deprecation")
public class PlaceholderAPI extends EZPlaceholderHook {
	public PlaceholderAPI(LobbyPresents m) {
		super(m, "lobbypresent");
	}


    @Override
    public String onPlaceholderRequest(Player p, String registername) {
    	try {
		if(registername.equals("max")) {
			return LobbyPresents.getIntThousan(LobbyPresents.getMax());
		} else if(registername.equals("totalfound")) {
			return LobbyPresents.getIntThousan(LobbyPresents.getFound(p));
		} else if(registername.equals("totalnotfound")) {
			return LobbyPresents.getIntThousan(LobbyPresents.getNotFound(p));
		}
    	}catch (Exception e) {
    		return "-";
		}
		return "&c 404 &r";
    }
}
