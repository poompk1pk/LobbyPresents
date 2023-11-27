package com.github.poompk1pk.lobbypresents;

import java.lang.reflect.Field;
import java.util.UUID;

import com.github.poompk1pk.lobbypresents.commands.Commands;
import com.github.poompk1pk.lobbypresents.config.ConfigFile;
import com.github.poompk1pk.lobbypresents.databases.DatabaseType;
import com.github.poompk1pk.lobbypresents.databases.MysqlDB;
import com.github.poompk1pk.lobbypresents.databases.YmlDB;
import com.github.poompk1pk.lobbypresents.databases.LobbyPresentsDataManager;
import com.github.poompk1pk.lobbypresents.listeners.ClickPresentListener;
import com.github.poompk1pk.lobbypresents.listeners.Setup;
import com.github.poompk1pk.lobbypresents.presents.placeholderapi.LobbyPresentsExpansion;
import com.github.poompk1pk.lobbypresents.utils.PresentsUtils;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import  com.github.poompk1pk.lobbypresents.type.ConfigType;
import com.github.poompk1pk.lobbypresents.versions.Presents;
import com.github.poompk1pk.lobbypresents.versions.v188.presents.v1_8_R3;

public class Main extends JavaPlugin {
  private static Main instance;
  
  public static Presents presents;
  
  public static String version_R;
  
  public Main() {
    instance = this;
  }
  
  public static Main getInstance() {
    return instance;
  }
  
  public static boolean PlaceholderAPI = false;
  
  public static boolean is18 = false; // lower 1.9 version don't have offhand

	private LobbyPresentsDataManager lobbyPresentsDataManager;

	public LobbyPresentsDataManager getLobbyPresentsDataManager() {
		return lobbyPresentsDataManager;
	}

	public void onEnable() {
    getConfig().options().copyDefaults(true);
    saveConfig();
    if (setupPresents()) {
      PresentsUtils.chat((CommandSender)Bukkit.getConsoleSender(), "\n&e    &e_\\&e/_&e\r\n&e     /\\\r\n&a     /\\\r\n&a    /  \\\r\n&a    /&c~~&a\\&eo\r\n&a   /&eo&a   \\\r\n&a  /&c~~&e*&c~~~&a\\\r\n&a &eo&a/    &bo &a\\\r\n&a /&c~~~~~~~~&a\\&c~&a`\r\n&a/__&4*&a_______\\\r\n&a     ||\r\n&c   \\&5====&c/  &f[*]&c\r\n    \\__/ &b[*] &d[*]  &cLobby&fPresents &a" +
          
          getDescription().getVersion());
      PresentsUtils.loadConfig();
      presents.conSoundDefault();

      
      PresentsUtils.loadSoundandEffect();
      
      loadData();
      PresentsUtils.onEnableloadProfile();
      PresentsUtils.loadHeads();
      
      
      PresentsUtils.loadRewards();
      if(((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).getConfig().getBoolean("enable")) {


		  PresentsUtils.loadPresents();


		  if(((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).getConfig().getBoolean("enable")) {
			  PresentsUtils.loadActionbars();
          }
      }
      
      Bukkit.getPluginManager().registerEvents(new Setup(), this);
      Bukkit.getPluginManager().registerEvents(new ClickPresentListener(), this);
      new Thread(() -> {
		  PresentsUtils.CheckVersionSystem((CommandSender)Bukkit.getConsoleSender());
	  }).start();
      getCommand("lobbypresents").setExecutor(new Commands());
      if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        PlaceholderAPI = true;
        //(new LPSPlaceholder()).register();
        new LobbyPresentsExpansion().register();
        PresentsUtils.chat((CommandSender)Bukkit.getConsoleSender(), " &bHooked &7placeholderAPI");
      } 
    } 
    new BukkitRunnable() {

		@Override
		public void run() {
			PresentsUtils.reload(Bukkit.getConsoleSender());

		}
	}.runTaskLater(this, 20);
  }

	public void onDisable() {
		// Close the database connection if it's open
		try {
			if (PresentsUtils.isActionbar && PresentsUtils.actionbars != null) {
				try {
					PresentsUtils.actionbars.cancel();
					PresentsUtils.actionbars = null;
				} catch (Exception e) { // how to check task is running on 1.8.8 api?

				}

			}
			getLobbyPresentsDataManager().shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			//	PresentsUtils.chat((CommandSender)Bukkit.getConsoleSender(), "&aLobbyPresents: &cError while closing the database connection.");
		}
	}


	public static Presents getPresents() {
    return presents;
  }
  
  public boolean setupPresents() {
    String version;
    try {
      version = getVersion();
    } catch (ArrayIndexOutOfBoundsException e) {
      return false;
    } 
    version_R = version;
    if (version.equals("v1_8_R3")) {
		presents = (Presents)new v1_8_R3();
        is18 = true;
	} /*
		 * else if (version.equals("v1_8_R2")) { presents = (Presents)new v1_8_R2();
		 * is18 = true; } else if (version.equals("v1_8_R3")) { presents = (Presents)new
		 * v1_8_R3(); is18 = true; } else if (version.equals("v1_9_R1")) { presents =
		 * (Presents)new v1_9_R1(); } else if (version.equals("v1_9_R2")) { presents =
		 * (Presents)new v1_9_R2(); } else if (version.equals("v1_10_R1")) { presents =
		 * (Presents)new v1_10_R1(); } else if (version.equals("v1_11_R1")) { presents =
		 * (Presents)new v1_11_R1(); } else if (version.equals("v1_12_R1")) { presents =
		 * (Presents)new v1_12_R1(); } else if (version.equals("v1_13_R1")) { presents =
		 * (Presents)new v1_13_R1(); } else if (version.equals("v1_13_R2")) { presents =
		 * (Presents)new v1_13_R2(); }
		 */
    return (presents != null);
  }
  
  public String getVersion() throws ArrayIndexOutOfBoundsException {
	  String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	  return version;
  }

	public static ItemStack createSkull(String url, String name) {
		ItemStack head = new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1, (short)3);
		if (url.isEmpty())
			return head;
		SkullMeta headMeta = (SkullMeta)head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().put("textures", new Property("textures", url));
		try {
			Field profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
		} catch (IllegalArgumentException|NoSuchFieldException|SecurityException|IllegalAccessException error) {
			error.printStackTrace();
		}
		head.setItemMeta((ItemMeta)headMeta);
		return head;
	}

	public void loadData() {
		if (getConfig().getBoolean("MYSQL.Enable")) { // check for old config
			getConfig().set("DatabaseType", DatabaseType.MYSQL.name().toLowerCase());
			getConfig().set("MYSQL.Enable", null);
			saveConfig();
		}
		String databaseType = getConfig().getString("DatabaseType");
		if(databaseType == null) {
			databaseType = DatabaseType.FILE.name().toLowerCase();
			getConfig().set("DatabaseType", databaseType);
			saveConfig();
		}
		DatabaseType type = DatabaseType.valueOf(databaseType.toUpperCase());
		if(type == DatabaseType.MYSQL) {
			lobbyPresentsDataManager = new MysqlDB();
		} else { // else file or invalid
			lobbyPresentsDataManager = new YmlDB();
		}

		if (lobbyPresentsDataManager.initialData()) {
			// ok
		} else {
			Bukkit.getPluginManager().disablePlugin((Plugin) this);
		}
	}




	public static void color(String text) {
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
	}
  
  public static int getUnfound(Player p) {
	  try {
		  return PresentsUtils.total_present - PresentsUtils.getProfile(p.getUniqueId()).getClaim().size();
	  } catch(Exception e) {
		  return PresentsUtils.total_present;
	  }

  }
  
  public static String getMessage(Player p,String s) {
	  if(PlaceholderAPI) {
		  return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, s.replace("%player%", p.getName()));
	  }
	  return s.replace("%player%", p.getDisplayName());
  }

}
