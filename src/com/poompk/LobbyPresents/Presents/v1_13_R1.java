package com.poompk.LobbyPresents.Presents;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.poompk.LobbyPresents.LobbyPresents;

import net.minecraft.server.v1_13_R1.ChatMessageType;
import net.minecraft.server.v1_13_R1.IChatBaseComponent;
import net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_13_R1.PacketPlayOutChat;

public class v1_13_R1 implements Presents {
	@Override
	public void sendtitlebar(Player p, String msg) {
		  IChatBaseComponent icbc = ChatSerializer.a("{\"text\":\"" + msg + "\"}");
		  PacketPlayOutChat packet = new PacketPlayOutChat(icbc, ChatMessageType.GAME_INFO);
		  ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
	@Override
	public void GreenVillager(Player p, Block b,String ef) {
		Location loc = new Location(b.getWorld(), b.getX()-0.35f,b.getY()+0.29f,b.getZ()-0.5);
		((CraftPlayer) p).spawnParticle(Particle.valueOf(ef),loc,8, 0.2, 0.2, 0.2, null);

        
	}
	
	@Override
	public void HitEffect(Player p, Block b,String ef) {
		Location loc = new Location(b.getWorld(), b.getX()-0.35f,b.getY()+0.29f,b.getZ()-0.5);
		((CraftPlayer) p).spawnParticle(Particle.valueOf(ef),loc,3, 0.1, 0.1, 0.2, null);

	}
	@Override
	public void PickupSound(Player p) {

		p.playSound(p.getLocation(), Sound.valueOf("ENTITY_EXPERIENCE_ORB_PICKUP"), 1, 1);
	}
	@Override
	public void FoundSound(Player p, float f) {
	try {	
		p.playSound(p.getLocation(), Sound.valueOf(LobbyPresents.getInstance().getConfig().getString("Sounds.found")), 1, f);
	} catch (IllegalArgumentException excep) {
				Bukkit.getLogger().warning("LobbyPresents: Not found sound "+LobbyPresents.getInstance().getConfig().getString("Sounds.found")+" on "+Bukkit.getBukkitVersion());
			}
		
	}
	@Override
	public void conSoundDefault() {
		if(LobbyPresents.getInstance().getConfig().getString("Sounds.found") == null) {
			LobbyPresents.getInstance().getConfig().set("Sounds.found", "ENTITY_PLAYER_LEVELUP");
			LobbyPresents.getInstance().getConfig().set("Sounds.alreadyfound", "ENTITY_MULE_DEATH");
			LobbyPresents.getInstance().saveConfig();
		}
		if(LobbyPresents.getInstance().getConfig().getString("Effect.click") == null) {
			LobbyPresents.getInstance().getConfig().set("Effect.click", "EXPLOSION_HUGE");
			LobbyPresents.getInstance().saveConfig();
		}
	}
	@Override
	public void effectclick(Player p) {
		Location loc = new Location(p.getWorld(), p.getLocation().getX()+0.35,p.getLocation().getY()+0.25f,p.getLocation().getZ()+0.45);
		try {
		((CraftPlayer) p).spawnParticle(Particle.valueOf(LobbyPresents.getInstance().getConfig().getString("Effect.click")),loc,8, 0.2, 0_2, 0.1, null);
		} catch (IllegalArgumentException e) {
		}
	}
	@SuppressWarnings("deprecation")
	public void sendBlockChange(Player p,Location loc){
		p.sendBlockChange(loc, Material.AIR, (byte) 0);
	}
}
