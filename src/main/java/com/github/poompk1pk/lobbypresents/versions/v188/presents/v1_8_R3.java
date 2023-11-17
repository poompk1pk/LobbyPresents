package com.github.poompk1pk.lobbypresents.versions.v188.presents;

import com.github.poompk1pk.lobbypresents.type.ConfigType;
import com.github.poompk1pk.lobbypresents.versions.Presents;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import com.github.poompk1pk.lobbypresents.config.ConfigFile;
import com.github.poompk1pk.lobbypresents.utils.PresentsUtils;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import net.minecraft.server.v1_8_R3.WorldServer;

/**
 * can someone convert this to protocollib?
 * it can reduce nms class to use protocollib for another version
 */
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
        EnumParticle.valueOf(ef),true,b.getX() + 0.45F, b.getY() + 0.25F, b.getZ() + 0.5F, 0.2F, 0.2F, 0.2F, 1.0F,PresentsUtils.amount_canclaim, null);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
	@Override
	public void HitEffect(Player p, Block b,String ef) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
        EnumParticle.valueOf(ef),true,b.getX() + 0.45F, b.getY() + 0.29F, b.getZ() + 0.5F, 0.2F, 0.2F, 0.2F, 0.1F, PresentsUtils.amount_claimed, null);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}
  

  
	@Override
	public void PickupSound(Player p) {
		p.playSound(p.getLocation(), Sound.valueOf("ORB_PICKUP"), 1, 1);
	}
  
  public void FoundSound(Player p, float f) {
    p.playSound(p.getLocation(), PresentsUtils.found, 1.0F, f);
  }
  
  public void conSoundDefault() {
    if (((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).getConfig().getString("Sounds.found") == null) {
      ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).getConfig().set("Sounds.found", "LEVEL_UP");
      ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).getConfig().set("Sounds.alreadyfound", "CREEPER_DEATH");
      ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).save();
    } 
    if (((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).getConfig().getString("Effect.click") == null) {
      ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).getConfig().set("Effect.click", "REDSTONE");
      ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).save();
    } 
  }
  @Override
	public void effectclick(Player p) {
		Block b = p.getLocation().getBlock();
      PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
    		  EnumParticle.valueOf(PresentsUtils.effect_click),true,b.getX() + 0.35F, b.getY() + 0.25F, b.getZ() + 0.45F, 0.2F, 1.5F, 0.2F, 1.0F,8_00,null);
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
