package com.poompk.LobbyPresents;

import com.poompk.LobbyPresents.Type.ConfigType;
import java.util.ArrayList;
import java.util.UUID;

public class Profile {
  private UUID uuid;
  
  private ConfigFile config;
  
  public Profile(UUID uuid) {
    this.uuid = uuid;
    this.config = PresentsUtils.config.get(ConfigType.PlayerData);
  }
  
  public UUID getUUID() {
    return this.uuid;
  }
  
  public ConfigFile getConfig() {
    return this.config;
  }
  
  public void addClaim(int id) {
    ArrayList<Integer> ids = getClaim();
    if (ids.contains(Integer.valueOf(id)))
      return; 
    ids.add(Integer.valueOf(id));
    if (Main.isMysql) {
      Main.Update("UPDATE `" + Main.getTb_name() + "` SET `claimed`='" + ids.toString().replace("[", "").replace("]", "").replace(" ", "") + "' WHERE `uuid`='" + this.uuid + "'");
    } else {
      getConfig().getConfig().set("user." + getUUID(), ids.toString().replace("[", "").replace("]", "").replace(" ", ""));
      getConfig().save();
    } 
  }
  
  public void clearClaim() {
    String s = "";
    if (Main.isMysql) {
      Main.Update("UPDATE `" + Main.getTb_name() + "` SET `claimed`='" + s.toString().replace("[", "").replace("]", "").replace(" ", "") + "' WHERE `uuid`='" + this.uuid + "'");
    } else {
      getConfig().getConfig().set("user." + getUUID(), null);
      getConfig().save();
    } 
  }
  
  public ArrayList<Integer> getClaim() {
    ArrayList<Integer> ids_list = new ArrayList<>();
    if (Main.isMysql) {
      String s = Main.getClaimedDB(getUUID());
      if (s.equalsIgnoreCase(""))
        return ids_list; 
      String[] ids = s.split(",");
      byte b;
      int i;
      String[] arrayOfString1;
      for (i = (arrayOfString1 = ids).length, b = 0; b < i; ) {
        String claimed = arrayOfString1[b];
        ids_list.add(Integer.valueOf(Integer.parseInt(claimed)));
        b++;
      } 
    } else if (getConfig().getConfig().getString("user." + getUUID()) != null) {
      String[] ids = getConfig().getConfig().getString("user." + getUUID()).split(",");
      byte b;
      int i;
      String[] arrayOfString1;
      for (i = (arrayOfString1 = ids).length, b = 0; b < i; ) {
        String claimed = arrayOfString1[b];
        ids_list.add(Integer.valueOf(Integer.parseInt(claimed)));
        b++;
      } 
    } 
    return ids_list;
  }
}
