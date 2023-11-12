package com.poompk.LobbyPresents;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

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

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.poompk.LobbyPresents.Type.Presents;
import com.poompk.LobbyPresents.Type.v1_8_R3;

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
  
  private static Connection con;
  
  private static String url;
  
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
      PresentsUtils.loadPresents();
      loadData();
      PresentsUtils.onEnableloadProfile();
      PresentsUtils.loadHeads();
      PresentsUtils.loadRewards();
      PresentsUtils.loadActionbars();
      Bukkit.getPluginManager().registerEvents(new Setup(), (Plugin)this);
      Bukkit.getPluginManager().registerEvents(new ClickPresentListener(), (Plugin)this);
      PresentsUtils.CheckVersionSystem((CommandSender)Bukkit.getConsoleSender());
      getCommand("lobbypresents").setExecutor(new Commands());
      if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        PlaceholderAPI = true;
        //(new LPSPlaceholder()).register();
        new SomeExpansion().register();
        PresentsUtils.chat((CommandSender)Bukkit.getConsoleSender(), " &bHooked &7placeholderAPI");
      } 
    } 
  }
  
  public void onDisable() {}
  
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
      url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
      if (!isconnect())
        try {
          con = DriverManager.getConnection(url, username, password);
          String sql = "CREATE TABLE IF NOT EXISTS " + tb_name + "(id INTEGER PRIMARY KEY AUTO_INCREMENT, uuid VARCHAR(50), claimed VARCHAR(250));";
          try {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.executeUpdate();
            stmt.close();
            for (Player pls : Bukkit.getOnlinePlayers())
              InsertDataDefault(pls.getUniqueId()); 
          } catch (SQLException e) {
            e.printStackTrace();
          } 
        } catch (SQLException e) {
          color("&aLobbyPresents: &cDatabase connection failed!");
          color("&aLobbyPresents: &cPlease check! host port databasename username password in &eConfig.yml! &cand restart your server");
          Bukkit.getPluginManager().disablePlugin((Plugin)this);
        }  
    } 
  }
  
  public void InsertDataDefault(UUID uuid) {
    String sql = "SELECT * FROM " + tb_name + " WHERE uuid = '" + uuid + "'";
    PreparedStatement stmt = null;
    ResultSet results = null;
    try {
      stmt = con.prepareStatement(sql);
      results = stmt.executeQuery();
      if (!results.next()) {
        String insert = "INSERT INTO " + tb_name + "(uuid, claimed) VALUES ('" + uuid + "', '');";
        PreparedStatement instmt = con.prepareStatement(insert);
        instmt.executeUpdate();
      } 
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null)
          stmt.close(); 
        if (results != null)
          results.close(); 
      } catch (SQLException e) {
        e.printStackTrace();
      } 
    } 
  }
  
  public static void Update(String qry) {
    Statement stmt = null;
    try {
      stmt = con.createStatement();
      stmt.executeUpdate(qry);
    } catch (Exception ex) {
      openCon();
    } finally {
      try {
        stmt.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } 
    } 
  }
  
  public static String getClaimedDB(UUID uuid) {
    String Claimed = "";
    ResultSet rs = null;
    try {
      rs = Query("SELECT `claimed` FROM `" + tb_name + "` WHERE `uuid`='" + uuid + "'");
      while (rs.next())
        Claimed = rs.getString(1); 
    } catch (Exception e1) {
      e1.printStackTrace();
    } finally {
      try {
        rs.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } 
    } 
    return Claimed;
  }
  
  public static ResultSet Query(String qry) {
    ResultSet rs = null;
    try {
      Statement stmt = con.createStatement();
      rs = stmt.executeQuery(qry);
    } catch (Exception ex) {
      openCon();
    } 
    return rs;
  }
  
  public static Connection openCon() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException classNotFoundException) {}
    try {
      Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
      return conn;
    } catch (SQLException sQLException) {
      return null;
    } 
  }
  
  public static boolean isconnect() {
    return (con != null);
  }
  
  public static void color(String text) {
    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
    console.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
  }
}
