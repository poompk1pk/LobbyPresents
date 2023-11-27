package com.github.poompk1pk.lobbypresents.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.github.poompk1pk.lobbypresents.Main;
import com.github.poompk1pk.lobbypresents.Profile;
import com.github.poompk1pk.lobbypresents.config.ConfigFile;
import com.github.poompk1pk.lobbypresents.listeners.Setup;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.github.poompk1pk.lobbypresents.versions.v188.listeners.Presents_18;
import  com.github.poompk1pk.lobbypresents.type.ConfigType;

import net.md_5.bungee.api.ChatColor;

public class PresentsUtils {
  public static HashMap<ConfigType, ConfigFile> config = new HashMap<>();
  
  public static HashMap<UUID, Profile> profile = new HashMap<>();
  
  public static HashMap<String, Presents_18> Pressents_18 = new HashMap<>();
  
 
  
  public static HashMap<Integer, String> sequence_rewards = new HashMap<>();
  
  public static HashMap<Integer, String> custom_rewards = new HashMap<>();
  
  public static ArrayList<ItemStack> Heads = new ArrayList<>();
  
  public static BukkitTask actionbars;
  
  public static HashMap<UUID, Integer> cansetup = new HashMap<>();
  
  public static Sound found;
  
  public static int distance = 5;
  
  public static String effect_canclaim;
  
  public static String eeffect_claimed;
  
  public static String effect_click;
  
  public static Sound Sound_alreadyfound;
  
  public static int total_present;
  
  public static boolean isActionbar = false;
  
  public static int updateActionbar = 0;
  
  public static int ticksEffect = 40;
  
  public static ArrayList<String> world_actionbar = new ArrayList<>();
  
  public static String reward_type = "none";
  
  public static String Message_found = "";
  
  public static String Message_alreadyfound = "";
  
  public static String Message_completed = "";
  
  public static String Message_actionbar = "";
  
  public static String Message_actionbar_completed = "";
  
  public static int amount_canclaim;
  
  public static int amount_claimed;
  
  public static void chat(CommandSender sender, String message) {
    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
  }
  
  public static void loadConfig() {
    if (!(new File(Main.getInstance().getDataFolder() + "/heads.yml")).exists())
      Main.getInstance().saveResource("heads.yml", true); 
    config.put(ConfigType.Default, new ConfigFile(new File(Main.getInstance().getDataFolder() + "/config.yml")));
    config.put(ConfigType.Presents, new ConfigFile(new File(Main.getInstance().getDataFolder() + "/presents.yml")));
    config.put(ConfigType.PlayerData, new ConfigFile(new File(Main.getInstance().getDataFolder() + "/data.yml")));
    config.put(ConfigType.Heads, new ConfigFile(new File(Main.getInstance().getDataFolder() + "/heads.yml")));
    chat((CommandSender)Bukkit.getConsoleSender(), "&a Loaded: file.yml");
  }
  
  public static void loadSoundandEffect() {
    FileConfiguration getConfig = ((ConfigFile)config.get(ConfigType.Default)).getConfig();
    found = getSound(getConfig.getString("Sounds.found"));
    distance = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getInt("Effect.distance");
    effect_canclaim = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Effect.canclaim");
    eeffect_claimed = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Effect.claimed");
    effect_click = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Effect.click");
    Sound_alreadyfound = getSound(((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Sounds.alreadyfound"));
    Message_found = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Messages.found");
    Message_alreadyfound = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Messages.alreadyfound");
    Message_completed = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Messages.completed");
    Message_actionbar = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Messages.actionbar");
    Message_actionbar_completed = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Messages.actionbar-completed");
    amount_canclaim = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getInt("Effect.amount_canclaim");
    amount_claimed = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getInt("Effect.amount_claimed");
    chat((CommandSender)Bukkit.getConsoleSender(), "&a Loaded: Config!");
  }
  
  public static void loadRewards() {
    reward_type = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Rewards.type");
    if (reward_type.equalsIgnoreCase("none")) {
      chat((CommandSender)Bukkit.getConsoleSender(), " &aRewards type: &7none");
    } else if (reward_type.equalsIgnoreCase("sequence")) {
      chat((CommandSender)Bukkit.getConsoleSender(), " &aRewards type: &7sequence");
      for (int i = 1; i <= total_present; i++) {
        if (((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Rewards.sequence-rewards." + i) == null || ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Rewards.sequence-rewards." + i).equalsIgnoreCase("none")) {
          sequence_rewards.put(Integer.valueOf(i), "none");
        } else {
          sequence_rewards.put(Integer.valueOf(i), ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Rewards.sequence-rewards." + i));
        } 
      } 
    } else if (reward_type.equalsIgnoreCase("custom")) {
      chat((CommandSender)Bukkit.getConsoleSender(), " &aRewards type: &7custom");
      if (Main.is18) {
        for (Presents_18 ps : Pressents_18.values())
          custom_rewards.put(Integer.valueOf(ps.getId()), ps.getCustomRewards()); 
      } else {
			/*
			 * for (Presents_19 ps : Pressents_19.values())
			 * custom_rewards.put(Integer.valueOf(ps.getId()), ps.getCustomRewards());
			 */
      } 
    } else if (reward_type.equalsIgnoreCase("both")) {
      chat((CommandSender)Bukkit.getConsoleSender(), " &aRewards type: &7both");
      try {
        for (int i = 1; i <= total_present; i++) {
          if (((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Rewards.sequence-rewards." + i) == null || ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Rewards.sequence-rewards." + i).equalsIgnoreCase("none")) {
            sequence_rewards.put(Integer.valueOf(i), "none");
          } else {
            sequence_rewards.put(Integer.valueOf(i), ((ConfigFile)config.get(ConfigType.Default)).getConfig().getString("Rewards.sequence-rewards." + i));
          } 
        } 
        for (String s : ((ConfigFile)config.get(ConfigType.Presents)).getConfig().getConfigurationSection("presents").getKeys(false)) {
          if (((ConfigFile)config.get(ConfigType.Presents)).getConfig().getString("presents." + s + ".custom-rewards") == null || ((ConfigFile)config.get(ConfigType.Presents)).getConfig().getString("presents." + s + ".custom-rewards").equalsIgnoreCase("none")) {
            custom_rewards.put(Integer.valueOf(Integer.parseInt(s)), "none");
            continue;
          } 
          custom_rewards.put(Integer.valueOf(Integer.parseInt(s)), ((ConfigFile)config.get(ConfigType.Presents)).getConfig().getString("presents." + s + ".custom-rewards"));
        } 
      } catch (Exception exception) {}
    } 
  }
  
  public static void loadActionbars() {
    Main.getInstance().getLogger().info("load actionbars!");
    isActionbar = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getBoolean("Actionbar.enable");
    world_actionbar.clear();
    for (String world_name : ((ConfigFile)config.get(ConfigType.Default)).getConfig().getStringList("Actionbar.world"))
      world_actionbar.add(world_name.toLowerCase());
    Main.getInstance().getLogger().info("all world "+world_actionbar.toString());
    updateActionbar = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getInt("Actionbar.update");
    if (!isActionbar)
      return; 
    if(actionbars != null) {

      try {
        actionbars.cancel();
        actionbars = null;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    	actionbars = new BukkitRunnable() {
			
			@Override
			public void run() {
              try {
                if (!((ConfigFile) PresentsUtils.config.get(ConfigType.Default)).getConfig().getBoolean("enable")) {
                  return;
                }
                if (!((ConfigFile) PresentsUtils.config.get(ConfigType.Default)).getConfig().getBoolean("Actionbar.enable")) {
                  cancel();
                  actionbars = null;
                  return;
                }
                for (Player pls : Bukkit.getOnlinePlayers()) {
                  if (PresentsUtils.world_actionbar.contains(pls.getWorld().getName().toLowerCase())) {
                    Main.presents.sendtitlebar(pls, PresentsUtils.getTextStatusActionBar(pls));
                  }
                }
              } catch (NullPointerException e) {

              } catch (Exception e) {
                e.printStackTrace();
              }
			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, updateActionbar);


  }



  public static String getTextStatusActionBar(Player p) {
    int total = getProfile(p.getUniqueId()).getClaim().size();
    if (total >= total_present)
      return ChatColor.translateAlternateColorCodes('&', Message_actionbar_completed.replace("%player%", p.getDisplayName()).replace("%unfound%", Main.getUnfound(p)+"").replace("%found%", (new StringBuilder(String.valueOf(getProfile(p.getUniqueId()).getClaim().size()))).toString()).replace("%total%", (new StringBuilder(String.valueOf(total_present))).toString())); 
    return ChatColor.translateAlternateColorCodes('&',  Message_actionbar_completed.replace("%player%", p.getDisplayName()).replace("%found%", (new StringBuilder(String.valueOf(getProfile(p.getUniqueId()).getClaim().size()))).toString()).replace("%total%", (new StringBuilder(String.valueOf(total_present))).toString()));
  }
  
  public static String getTextNormalMessage(Player p) {
    int total = getProfile(p.getUniqueId()).getClaim().size();
    if (total >= total_present)
      return Message_completed.replace("%player%", p.getDisplayName()).replace("%unfound%", Main.getUnfound(p)+"").replace("%found%", (new StringBuilder(String.valueOf(getProfile(p.getUniqueId()).getClaim().size()))).toString()).replace("%total%", (new StringBuilder(String.valueOf(total_present))).toString()); 
    return Message_found.replace("%player%", p.getDisplayName()).replace("%unfound%", Main.getUnfound(p)+"").replace("%found%", (new StringBuilder(String.valueOf(getProfile(p.getUniqueId()).getClaim().size()))).toString()).replace("%total%", (new StringBuilder(String.valueOf(total_present))).toString());
  }
  
  public static void setRewardType(String type) {
    ((ConfigFile)config.get(ConfigType.Default)).getConfig().set("Rewards.type", type);
    ((ConfigFile)config.get(ConfigType.Default)).save();
  }
  
  public static void setSequenceRewards(CommandSender sender, int id, String s) {
    ((ConfigFile)config.get(ConfigType.Default)).getConfig().set("Rewards.sequence-rewards." + id, s);
    ((ConfigFile)config.get(ConfigType.Default)).save();
    sequence_rewards.put(Integer.valueOf(id), s);
  }
  
  public static void setCustomRewards(CommandSender sender, int id, String s) {
    if (Main.is18) {
      if (Pressents_18.get((new StringBuilder(String.valueOf(id))).toString()) != null) {
        ((Presents_18)Pressents_18.get((new StringBuilder(String.valueOf(id))).toString())).setCustomRewards(s);
        custom_rewards.put(Integer.valueOf(id), ((Presents_18)Pressents_18.get((new StringBuilder(String.valueOf(id))).toString())).getCustomRewards());
        chat(sender, "&bCustom-Reward[" + id + "]: " + s.substring(0, s.length() - 1));
      } else {
        chat(sender, "&cThe present id is not found! you must '/lps reload' before set Custom Command");
      } 
	} /*
		 * else if (Pressents_19.get((new StringBuilder(String.valueOf(id))).toString())
		 * != null) { ((Presents_19)Pressents_19.get((new
		 * StringBuilder(String.valueOf(id))).toString())).setCustomRewards(s);
		 * custom_rewards.put(Integer.valueOf(id), ((Presents_19)Pressents_19.get((new
		 * StringBuilder(String.valueOf(id))).toString())).getCustomRewards());
		 * chat(sender, "&bCustom-Reward[" + id + "]: " + s.substring(0, s.length() -
		 * 1)); }
		 */else {
      chat(sender, "&cThe present id is not found! you must '/lps reload' before set Custom Command");
    } 
  }
  
  public static void loadPresents() {
    ticksEffect = ((ConfigFile)config.get(ConfigType.Default)).getConfig().getInt("Effect.ticks");
    if (((ConfigFile)config.get(ConfigType.Presents)).getConfig().getString("presents") != null) {
      chat((CommandSender)Bukkit.getConsoleSender(), " &aLoading the presents");
      if (Main.is18) {
        for (String s : ((ConfigFile)config.get(ConfigType.Presents)).getConfig().getConfigurationSection("presents").getKeys(false))
          Pressents_18.put(s, new Presents_18(((ConfigFile)config.get(ConfigType.Presents)).getConfig().getString("presents." + s + ".loc").split(":"), Integer.parseInt(s))); 
      } else {
			/*
			 * for (String s : ((ConfigFile)config.get(ConfigType.Presents)).getConfig().
			 * getConfigurationSection("presents").getKeys(false)) Pressents_19.put(s, new
			 * Presents_19(((ConfigFile)config.get(ConfigType.Presents)).getConfig().
			 * getString("presents." + s + ".loc").split(":"), Integer.parseInt(s)));
			 */      } 
    } 
    if (Main.is18) {
      total_present = Pressents_18.size();
    } else {
     // total_present = Pressents_19.size();
    } 
    chat((CommandSender)Bukkit.getConsoleSender(), " &aLoaded " + total_present + " completed!");
  }
  
  public static void setcanSetupPresent(Player p, int id) {
    cansetup.put(p.getUniqueId(), Integer.valueOf(id));
    chat((CommandSender)p, "&eClick on the skull [" + id + "]");
  }
  
  public static void setSetupToConfig(String loc, int id) {
    ((ConfigFile)config.get(ConfigType.Presents)).getConfig().set("presents." + id + ".loc", loc);
    ((ConfigFile)config.get(ConfigType.Presents)).save();
  }
  
  public static void removePresentsID(CommandSender sender, int id) {
    if (((ConfigFile)config.get(ConfigType.Presents)).getConfig().getString("presents." + id) == null) {
      chat(sender, "&cNot found id: " + id);
      return;
    } 
    ((ConfigFile)config.get(ConfigType.Presents)).getConfig().set("presents." + id, null);
    ((ConfigFile)config.get(ConfigType.Presents)).save();
    chat(sender, "&eremoved id: " + id + "!");
  }
  
  public static Sound getSound(String s) {
    Sound sound = null;
    try {
      sound = Sound.valueOf(s);
    } catch (IllegalArgumentException excep) {
      Bukkit.getLogger().warning("LobbyPresents: Not found sound " + s + " on " + Bukkit.getBukkitVersion());
      found = Sound.values()[(Sound.values()).length - 1];
      Bukkit.getLogger().warning("set to " + Sound.values()[(Sound.values()).length - 1].name());
    } 
    return sound;
  }
  public static void clearUserData(CommandSender sender, Player p2) {
    if (p2 == null) {
      chat(sender, "&cPlayer is not online!");
      return;
    }
    getProfile(p2.getUniqueId()).clearClaim();
    chat(sender, "&cClearUserData successfully!");
  }
  public static void clearAllData(CommandSender sender) {
    Main.getInstance().getLobbyPresentsDataManager().clearAllData(sender);
    Profile.clearCached();
    profile.clear();
    chat(sender, "&cClear All Data successfully!");
  }
  
  public static void reload(CommandSender sender) {
    isActionbar = false;
    if (Main.is18) {
      for (Presents_18 ps : Pressents_18.values())
        ps.unRegisterListener(); 
      Pressents_18.clear();
    } else {
		/*
		 * for (Presents_19 ps : Pressents_19.values()) ps.unRegisterListener();
		 * Pressents_19.clear();
		 */
    } 
    Heads.clear();
    loadConfig();
    loadHeads();
    Main.presents.conSoundDefault();
    
    PresentsUtils.loadRewards();
    if(((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).getConfig().getBoolean("enable")) {
        
        
  	  PresentsUtils.loadPresents();
        
  	  
        if(((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).getConfig().getBoolean("enable")) {
      	  PresentsUtils.loadActionbars();
        }
    }
    
    chat(sender, "&bReloaded!");
  }
  
  public static void loadHeads() {
    for (String s : ((ConfigFile)config.get(ConfigType.Heads)).getConfig().getConfigurationSection("heads").getKeys(true)) {
      ItemStack is;
      String title = s;
      String value = ((ConfigFile)config.get(ConfigType.Heads)).getConfig().getString("heads." + s);
      try {
        if (value.length() > 50) {
          is = Setup.createSkull(value, title);
        } else {
          is = Setup.ItemSkull(1, value, title);
        } 
      } catch (Exception e) {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("&7Can not load &c" + title);
        is = Setup.Item("BARRIER", 1, 0, "&c" + title, lore);
      } 
      Heads.add(is);
    } 
    chat((CommandSender)Bukkit.getConsoleSender(), " &aLoaded heads-gui " + Heads.size() + "&a completed!");
  }
  
  public static Sound getSoundalready(String s) {
    Sound sound = null;
    try {
      sound = Sound.valueOf(s);
    } catch (IllegalArgumentException excep) {
      Bukkit.getLogger().warning("LobbyPresents: Not found sound " + s + " on " + Bukkit.getBukkitVersion());
      Sound_alreadyfound = Sound.values()[(Sound.values()).length - 2];
      Bukkit.getLogger().warning("set to " + Sound.values()[(Sound.values()).length - 2].name());
    } 
    return sound;
  }
  
  public static Profile getProfile(UUID uuid) {
    if (profile.containsKey(uuid)) {
      return profile.get(uuid);
    } else {
      profile.put(uuid,new Profile(uuid));
    }
    return null;
  }
  
  public static void onEnableloadProfile() {
    for (Player pls : Bukkit.getOnlinePlayers())
      profile.put(pls.getUniqueId(), new Profile(pls.getUniqueId())); 
  }
  
  public static void onJoinloadProfile(UUID uuid) {
    profile.put(uuid, new Profile(uuid));
  }
  
  public static void CheckVersionSystem(CommandSender sender) {
    try {
      URL url = new URL("https://pastebin.com/raw/cZmDwD6J");
      URL url2 = new URL("https://pastebin.com/raw/EWiL2yDS");
      BufferedReader bf = new BufferedReader(new InputStreamReader(url.openStream()));
      BufferedReader bf2 = new BufferedReader(new InputStreamReader(url2.openStream()));
      String message = bf2.readLine();
      double version_this = Double.parseDouble(Main.getInstance().getDescription().getVersion());
      double version_new = Double.parseDouble(bf.readLine());
      bf.close();
      bf2.close();
      if (version_this < version_new) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[LobbyPresents]&c Version outdated You must update LobbyPresents to verion " + version_new + "!"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&f " + message));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e https://www.spigotmc.org/resources/50290/"));
      } else {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[LobbyPresents]&7 Version: &bThe latest version (" + version_new + ")"));
      } 
    } catch (Exception exception) {}
  }


}
