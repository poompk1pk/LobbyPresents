package com.poompk.LobbyPresents.Type;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface Presents {
  void sendtitlebar(Player paramPlayer, String paramString);
  
  void GreenVillager(Player paramPlayer, Block paramBlock, String paramString);
  
  void HitEffect(Player paramPlayer, Block paramBlock, String paramString);
  
  void PickupSound(Player paramPlayer);
  
  void FoundSound(Player paramPlayer, float paramFloat);
  
  void conSoundDefault();
  
  void effectclick(Player paramPlayer);
  
  void sendBlockChange(Player paramPlayer, Location paramLocation);
}
