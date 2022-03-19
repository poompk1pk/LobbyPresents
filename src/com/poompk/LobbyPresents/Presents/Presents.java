package com.poompk.LobbyPresents.Presents;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface Presents {
	public void sendtitlebar(Player p, String msg);
	public void GreenVillager(Player p, Block b,String ef);
	public void HitEffect(Player p, Block b,String ef);
	public void PickupSound(Player p);
	public void FoundSound(Player p,float f);
	public void conSoundDefault();
	public void effectclick(Player p);
	public void sendBlockChange(Player p,Location loc);
}
