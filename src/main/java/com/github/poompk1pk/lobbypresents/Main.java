package com.github.poompk1pk.lobbypresents;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import com.github.poompk1pk.lobbypresents.commands.Commands;
import com.github.poompk1pk.lobbypresents.config.ConfigFile;
import com.github.poompk1pk.lobbypresents.listeners.ClickPresentListener;
import com.github.poompk1pk.lobbypresents.listeners.Setup;
import com.github.poompk1pk.lobbypresents.presents.placeholderapi.LobbyPresentsExpansion;
import com.github.poompk1pk.lobbypresents.utils.PresentsUtils;
import com.zaxxer.hikari.HikariConfig;
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
import  com.github.poompk1pk.lobbypresents.type.Presents;
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
  
  public static boolean is18 = false;
  
  public static boolean isMysql = false;
  
  private static String host;
  
  private static String port;
  
  private static String database;
  
  private static String username;
  
  private static String password;
  
  //private static Connection con;
  private static HikariDataSource dataSource;
  
  private static String tb_name;
  public static String getTb_name() {
	  return tb_name;
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
			if (Main.isMysql && Main.isHikariCPConnected()) {
				Main.dataSource.close();
				Main.dataSource = null;
				//PresentsUtils.chat((CommandSender)Bukkit.getConsoleSender(), "&aLobbyPresents: &bDatabase connection closed.");
			}
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
		if (getConfig().getBoolean("MYSQL.Enable")) {
			host = getConfig().getString("MYSQL.host");
			port = getConfig().getString("MYSQL.port");
			database = getConfig().getString("MYSQL.database");
			username = getConfig().getString("MYSQL.username");
			password = getConfig().getString("MYSQL.password");
			tb_name = getConfig().getString("MYSQL.table_name");
			isMysql = true;
			PresentsUtils.chat((CommandSender)Bukkit.getConsoleSender(), "&a Loaded: mysql");
			HikariConfig hikariConfig = new HikariConfig();
			hikariConfig.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database +
					"?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&autoReconnect=true");
			hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver"); // Updated driver class name
			hikariConfig.setUsername(username);
			hikariConfig.setPassword(password);
			hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
			hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
			hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

			// Additional properties for MySQL 8
			hikariConfig.addDataSourceProperty("useUnicode", "true");
			hikariConfig.addDataSourceProperty("useJDBCCompliantTimezoneShift", "true");
			hikariConfig.addDataSourceProperty("useLegacyDatetimeCode", "false");
			hikariConfig.addDataSourceProperty("serverTimezone", "UTC");

			// HikariCP setup
			dataSource = new HikariDataSource(hikariConfig);

			if(isHikariCPConnected()) {
				dataSource.close();
				dataSource = null;
			}
			dataSource = new HikariDataSource(hikariConfig);


			try (Connection con = dataSource.getConnection()) {
				String sql = "CREATE TABLE IF NOT EXISTS " + tb_name +
						"(id INTEGER PRIMARY KEY AUTO_INCREMENT, uuid VARCHAR(50), claimed VARCHAR(250));";
				try (PreparedStatement stmt = con.prepareStatement(sql)) {
					stmt.executeUpdate();
					for (Player pls : Bukkit.getOnlinePlayers()) {
						InsertDataDefault(pls.getUniqueId());
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				color("&aLobbyPresents: &cDatabase connection failed!");
				color("&aLobbyPresents: &cPlease check! host port databasename username password in &eConfig.yml! &cand restart your server");
				Bukkit.getPluginManager().disablePlugin((Plugin) this);
			}
		}

	}

	public void InsertDataDefault(UUID uuid) {
		String sqlSelect = "SELECT * FROM " + tb_name + " WHERE uuid = ?";
		String sqlInsert = "INSERT INTO " + tb_name + "(uuid, claimed) VALUES (?, '')";

		try (Connection connection = dataSource.getConnection();
			 PreparedStatement stmtSelect = connection.prepareStatement(sqlSelect)) {

			stmtSelect.setString(1, uuid.toString());

			try (ResultSet results = stmtSelect.executeQuery()) {
				if (!results.next()) {
					try (PreparedStatement stmtInsert = connection.prepareStatement(sqlInsert)) {
						stmtInsert.setString(1, uuid.toString());
						stmtInsert.executeUpdate();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void Update(String qry) {
		try (Connection connection = dataSource.getConnection();
			 Statement stmt = connection.createStatement()) {

			stmt.executeUpdate(qry);
		} catch (SQLException e) {
			handleSQLException(e);
		}
	}

	public static ResultSet Query(String qry) {
		try (Connection connection = dataSource.getConnection();
			 Statement stmt = connection.createStatement()) {

			return stmt.executeQuery(qry);
		} catch (SQLException e) {
			handleSQLException(e);
		}
		return null;
	}

	private static void handleSQLException(SQLException ex) {
		ex.printStackTrace();
	}


	public static String getClaimedDB(UUID uuid) {
		String Claimed = "";
		try (Connection connection = dataSource.getConnection();
			 Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT `claimed` FROM `" + tb_name + "` WHERE `uuid`='" + uuid + "'")) {

			while (rs.next()) {
				Claimed = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Claimed;
	}


	/*
		 * public static ResultSet Query(String qry) { ResultSet rs = null; try {
		 * Statement stmt = con.createStatement(); rs = stmt.executeQuery(qry); } catch
		 * (Exception ex) { openCon(); } return rs; }
		 */

	public static Connection openCon() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean isHikariCPConnected() {
		if (dataSource != null) {
			return !dataSource.isClosed();
		}
		return false;
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
