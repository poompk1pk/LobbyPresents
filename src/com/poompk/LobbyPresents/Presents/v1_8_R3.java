package com.poompk.LobbyPresents.Presents;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.poompk.LobbyPresents.Main;
import com.poompk.LobbyPresents.Type.Presents;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class v1_8_R3 implements Presents {
	@Override
	public void sendtitlebar(Player p, String msg) {
		  IChatBaseComponent icbc = ChatSerializer.a("{\"text\":\"" + msg + "\"}");
		  PacketPlayOutChat packet = new PacketPlayOutChat(icbc, (byte) 2);
		  ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
	@Override
	public void GreenVillager(Player p, Block b,String ef) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
        EnumParticle.valueOf(ef),true,b.getX()-0.35f,b.getY()+0.25f,b.getZ()-0.45f,0.2f,0.2f,0.2f,1,8,null);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
	
	@Override
	public void HitEffect(Player p, Block b,String ef) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
        EnumParticle.valueOf(ef),true,b.getX()-0.35f,b.getY()+0.55f,b.getZ()-0.45f,0,0,0,0.1f,2,null);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
	@Override
	public void PickupSound(Player p) {
		p.playSound(p.getLocation(), Sound.valueOf("ORB_PICKUP"), 1, 1);
	}
	@Override
	public void FoundSound(Player p, float f) {
		try {
		p.playSound(p.getLocation(), Sound.valueOf(Main.getInstance().getConfig().getString("Sounds.found")), 1, f);
		} catch (IllegalArgumentException excep) {
			Bukkit.getLogger().warning("LobbyPresents: Not found sound "+Main.getInstance().getConfig().getString("Sounds.found")+" on "+Bukkit.getBukkitVersion());
		}
		
	}
	@Override
	public void conSoundDefault() {
		if(Main.getInstance().getConfig().getString("Sounds.found") == null) {
			Main.getInstance().getConfig().set("Sounds.found", "LEVEL_UP");
			Main.getInstance().getConfig().set("Sounds.alreadyfound", "CREEPER_DEATH");
			Main.getInstance().saveConfig();
		}
		if(Main.getInstance().getConfig().getString("Effect.click") == null) {
			Main.getInstance().getConfig().set("Effect.click", "REDSTONE");
			Main.getInstance().saveConfig();
		}
	}
	@Override
	public void effectclick(Player p) {
		Block b = p.getLocation().getBlock();
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
        EnumParticle.valueOf(Main.getInstance().getConfig().getString("Effect.click")),true,b.getX()+0.35f,b.getY()+0.25f,b.getZ()+0.45f,0.2f,0_1.5f,0.2f,1,8_00,null);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
	@SuppressWarnings("deprecation")
	public void sendBlockChange(Player p,Location loc){
		WorldServer s = ((CraftWorld)loc.getWorld()).getHandle();
		PacketPlayOutBlockChange hide = new PacketPlayOutBlockChange(s, new BlockPosition(loc.getX(), loc.getY(), loc.getZ()));
	    hide.block = CraftMagicNumbers.getBlock(0).fromLegacyData(0);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(hide);
	}
}
