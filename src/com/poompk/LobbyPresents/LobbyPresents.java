package com.poompk.LobbyPresents;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.poompk.LobbyPresents.Presents.Presents;
import com.poompk.LobbyPresents.Presents.Heads;
import com.poompk.LobbyPresents.Presents.v1_8_R3;
import com.poompk.LobbyPresents.Type.ConfigType;

public class LobbyPresents extends JavaPlugin implements Listener{
	private Presents presents;
    private static LobbyPresents instance;
    public static String host;
    public static String port;
    public static String database;
    public static String username;
    public static String password;
    public static Connection con;
    public static String url;
    public static String tb_name;
    public static boolean PlaceholderAPI = false;
    public static LobbyPresents getInstance() {
        return instance;
    }
    public HashMap<String,Integer> set = new HashMap<String,Integer>();
    public HashMap<Location,Integer> local = new HashMap<Location,Integer>();
    public HashMap<Integer,Location> localinvert = new HashMap<Integer,Location>();
    public static HashMap<String,String> ClaimString = new HashMap<String,String>();
   // @SuppressWarnings("deprecation")
	@Override
    public void onEnable() {
    	Server server = Bukkit.getServer();
	    ConsoleCommandSender console = server.getConsoleSender();
	    getCommand("lobbypresents").setExecutor(this);
		getConfig().options().copyDefaults(true);
		saveConfig();
		conExist();
        if (setupVersion()) {
            Bukkit.getPluginManager().registerEvents(this, this);
            String version;
            try {
            	version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];
                } catch (ArrayIndexOutOfBoundsException Exception) {
                    return;
                }
            if (version.equals("v1_8_R1")) {
            	Bukkit.getPluginManager().registerEvents(new Listener_18(), this);
            } else if (version.equals("v1_8_R2")) {
            	Bukkit.getPluginManager().registerEvents(new Listener_18(), this);
            } else if (version.equals("v1_8_R3")) {
            	Bukkit.getPluginManager().registerEvents(new Listener_18(), this);
            }
			instance = this;
            console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&fLobby&aPresents&8] &7Enabled! &eBy PoomPK"));
            presents.conSoundDefault();
            loadData();
    		loadLocation();
    		if (server.getPluginManager().isPluginEnabled("PlaceholderAPI") == true) {
    			new SomeExpansion().register();
    		//	new PlaceholderAPI(this).hook();
    			console.sendMessage(ChatColor.GRAY+" Hooked PlaceHolderAPI");
    			PlaceholderAPI = true;
    		}
    		CheckVersionSystem(console);
        } else {
        	console.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8[&fLobby&aPresents&8] &7"+Bukkit.getBukkitVersion()+"&c is not compatible with this plugin"));
            Bukkit.getPluginManager().disablePlugin(this);
        }
        try {
        RunEffect();
        }catch (Exception e) {
		}
    }
	public static void CheckVersionSystem(CommandSender sender) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7 Outdate version"));
	}
    private boolean setupVersion() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".",  ",").split(",")[3];

        } catch (ArrayIndexOutOfBoundsException Exception) {
            return false;
        }
        if (version.equals("v1_8_R3")) {
        	presents = new v1_8_R3();
        }
        return presents != null;
    }
    public void conExist(){
    	for(Player p : Bukkit.getOnlinePlayers()){
	    	if(getConfig().getString("data."+p.getUniqueId()) == null){
	    		getConfig().set("data."+p.getUniqueId(), "#");
	    		saveConfig();
	    	}
    	}
    }
    public void conEffectExist() {
    	if(getConfig().getString("Effect") == null) {
    		getConfig().set("Effect.canclaim", "VILLAGER_HAPPY");
    		getConfig().set("Effect.claimed", "CRIT");
    		saveConfig();
    	}
    }
    public void loadData() {
    	if(getConfig().getBoolean("MYSQL.Enable") == true) {
		host = getConfig().getString("MYSQL.host");
		port = getConfig().getString("MYSQL.port");
		database = getConfig().getString("MYSQL.database");
		username = getConfig().getString("MYSQL.username");
		password = getConfig().getString("MYSQL.password");
		tb_name = getConfig().getString("MYSQL.table_name");
		url = "jdbc:mysql://" + host + ":" + port + "/" + database+"?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
	    if (!isconnect()) {
	        try {
	          con = DriverManager.getConnection(url, username, password);
			  String sql = "CREATE TABLE IF NOT EXISTS "+tb_name+"(id INTEGER PRIMARY KEY AUTO_INCREMENT, uuid VARCHAR(50), claimed VARCHAR(250));";
			    try {
			    	PreparedStatement stmt = con.prepareStatement(sql);
			    	stmt.executeUpdate();
			    	stmt.close();
			    } catch (SQLException e) {
			    	e.printStackTrace();
			    }
	          for(Player p : Bukkit.getOnlinePlayers()){
	        	  InsertDataDefault(p.getUniqueId());
	        	  loadClaimed(p.getUniqueId());
	          }
	          
	        } catch (SQLException e){
	          e.printStackTrace();
	          color("&aLobbyPresents: &cDatabase connection failed!");
	          color("&aLobbyPresents: &cPlease check! host port databasename username password in &eConfig.yml! &cand restart your server");
	          Bukkit.getPluginManager().disablePlugin(this);
	        }
	    }
	     } else {
	          for(Player p : Bukkit.getOnlinePlayers()){
	        	  loadClaimed(p.getUniqueId());
	          } 
	     }
    }
    public void loadLocation() {
		for(int i = 1;i<=getConfig().getInt("Max");i++) {
			World w = Bukkit.getWorld(getConfig().getString("loc."+i+".w"));
			int x = getConfig().getInt("loc."+i+".x");
			int y = getConfig().getInt("loc."+i+".y");
			int z = getConfig().getInt("loc."+i+".z");
			Location c = new Location(w, x, y, z);
			local.put(c, i);localinvert.put(i, c);
			}
    }
    @EventHandler
    public void Playerjoin(PlayerJoinEvent e) {
    	Player p = e.getPlayer();
    	if(getConfig().getBoolean("MYSQL.Enable") == true) {
        	InsertDataDefault(p.getUniqueId());	
    	}
    	loadClaimed(p.getUniqueId());
    }
	  public void InsertDataDefault(UUID uuid){
		  String sql = "SELECT * FROM "+tb_name+" WHERE uuid = '"+uuid+"'";
		  try {
		  PreparedStatement stmt = con.prepareStatement(sql);
		  ResultSet results = stmt.executeQuery();
			if (!results.next()) {
				  String insert = "INSERT INTO "+tb_name+"(uuid, claimed) VALUES ('"+uuid+"', '');";
				  PreparedStatement instmt = con.prepareStatement(insert);
				  instmt.executeUpdate();
				  stmt.close();
			  } else {
			      
			  }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	  }
	  public static boolean isconnect()
	  {
	    return con != null;
	  }
		public static void color(String text) {
		    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
			console.sendMessage(ChatColor.translateAlternateColorCodes('&', text));	
		}
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    		if (sender instanceof Player) {
    			Player p = (Player) sender;
    			if(cmd.getName().equalsIgnoreCase("lobbypresents") && p.hasPermission("lobbypresents.admin")){
		            if (args.length == 0) {
		              	p.sendMessage("/lps set [1-Max] #And Right click on skull");
		              	p.sendMessage("/lps setmax [Max]");
		              	p.sendMessage("/lps addreward [Number of meet] [Command]");
		              	p.sendMessage("/lps rewards #Check all rewards");
		              	p.sendMessage("/lps clearreward [Number of meet]");
		              	p.sendMessage("/lps removeallpresents #Skull , rewards, location, Max");
		              	p.sendMessage("/lps presents");
		              	p.sendMessage("/lps tp [ID]");
		              	p.sendMessage("/lps check [ID]");
		              	p.sendMessage("/lps clear [player]");
		              	p.sendMessage("/lps list");
		              	p.sendMessage("/lps reload");
		              	p.sendMessage(ChatColor.translateAlternateColorCodes('&', " &b&lLobbyPresents &f&l by PoomPK"));
		                return true;
		              	}
		            if (args[0].equalsIgnoreCase("set")){
		            	if(args.length == 1) {
		            		p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"/lps set [ID]");
		            		return false;
		            	}
		            	int id = Integer.parseInt(args[1]);
		            	boolean checkid = id <= getConfig().getInt("Max") && id > 0;
		            	if(checkid) {
			            	set.put(p.getName(), id);	
			            	p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"Right Click on Skull!");
		            	} else {
		            		p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"/lps set [1-"+getConfig().getInt("Max")+"]");
		            	}
		            }else if (args[0].equalsIgnoreCase("tp")){
		            	if(args.length == 1) {
		            		p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"/lps tp [ID]");
		            		return false;
		            	}
		            	int id = Integer.parseInt(args[1]);
        				World w = Bukkit.getWorld(getConfig().getString("loc."+id+".w"));
        				int x = getConfig().getInt("loc."+id+".x");
        				int y = getConfig().getInt("loc."+id+".y");
        				int z = getConfig().getInt("loc."+id+".z");
        				p.teleport(new Location(w, x, y, z));
        				p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"teleported! to ID."+id);
		            } else if (args[0].equalsIgnoreCase("check")){
		            	if(args.length == 1) {
		            		p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"/lps check [ID]");
		            		return false;
		            	}
		            	try {
		            	int id = Integer.parseInt(args[1]);
        				World w = Bukkit.getWorld(getConfig().getString("loc."+id+".w"));
        				int x = getConfig().getInt("loc."+id+".x");
        				int y = getConfig().getInt("loc."+id+".y");
        				int z = getConfig().getInt("loc."+id+".z");
        				p.sendMessage("[Check] "+id+". w: "+w.getName()+" x: "+x+" y: "+y+" z: "+z+"");
		            	}catch (Exception e) {
						}
		            }else if (args[0].equalsIgnoreCase("presents")){
		            	openPresentGUI(p,1);
		            }else if (args[0].equalsIgnoreCase("clear")){
		            	if(args.length == 1) {
		            		p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"/lps clear Player");
		            		return false;
		            	}
		            	@SuppressWarnings("deprecation")
						OfflinePlayer plyeroff = Bukkit.getOfflinePlayer(args[1]);
		            	Clearing(plyeroff.getUniqueId());
		            	p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"Cleared! "+plyeroff.getName());
		            }  else if (args[0].equalsIgnoreCase("reload")){
		            	reloadConfig();
		        		for(int i = 1;i<=getConfig().getInt("Max");i++) {
		        			World w = Bukkit.getWorld(getConfig().getString("loc."+i+".w"));
		        			int x = getConfig().getInt("loc."+i+".x");
		        			int y = getConfig().getInt("loc."+i+".y");
		        			int z = getConfig().getInt("loc."+i+".z");
		        			Location c = new Location(w, x, y, z);
		        			local.put(c, i);localinvert.put(i, c);
		        			set.clear();
		        			loadData();
		        			for(Player pls: Bukkit.getOnlinePlayers()) {
		        				loadClaimed(pls.getUniqueId());
		        			}
		        		}
		            	p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"Reload finished!");
		            } else if (args[0].equalsIgnoreCase("setmax")){
		            	if(args.length == 1) {
		            		p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"/lps setmax [1 to ...]");
		            		return false;
		            	}
		            	int args1 = Integer.parseInt(args[1]);
		            	getConfig().set("Max", args1);
		            	saveConfig();
		            	p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"Set max: "+args1);
		            } else if (args[0].equalsIgnoreCase("list")){
		            	try {
		            	p.sendMessage("lobbypresents List:");
		            	for(int i=1;i<=getConfig().getInt("Max");i++){
	        				World w = Bukkit.getWorld(getConfig().getString("loc."+i+".w"));
	        				int x = getConfig().getInt("loc."+i+".x");
	        				int y = getConfig().getInt("loc."+i+".y");
	        				int z = getConfig().getInt("loc."+i+".z");
		            		p.sendMessage(i+". w: "+w.getName()+" x: "+x+" y: "+y+" z: "+z);
		            	}
		            	}catch (Exception e) {
						}
		            }  else if (args[0].equalsIgnoreCase("rewards")){
		            	try {
		            	p.sendMessage("lobbypresents Rewards:");
		            	for(int i=1;i<=getConfig().getInt("Max");i++){
	        				List<String> conlist = getConfig().getStringList("Rewards."+i);
		            		p.sendMessage(i+". "+conlist.toString());
		            	}
		            	}catch (Exception e) {
						}
		            }  else if (args[0].equalsIgnoreCase("removeallpresents")){
		            	try {
		            	p.sendMessage("lobbypresents Clearing:");
		            	int max = getConfig().getInt("Max");
		            	for(int i=1;i<=max;i++){
	        				World w = Bukkit.getWorld(getConfig().getString("loc."+i+".w"));
	        				int x = getConfig().getInt("loc."+i+".x");
	        				int y = getConfig().getInt("loc."+i+".y");
	        				int z = getConfig().getInt("loc."+i+".z");
		            		Location loc = new Location(w, x, y, z);
		            		if(loc.getBlock().getType() == Material.matchMaterial("SKULL")) {
		            			local.clear();
		            			localinvert.clear();
		            			set.clear();
		            			getConfig().set("loc."+i, null);
		            			getConfig().set("Rewards."+i, null);
		            			getConfig().set("Max", "/lps setmax amount");
		            			ClaimString.clear();
		            			saveConfig();
		            			reloadConfig();
		            			p.sendMessage("Removed: "+i);
		            		}
		            	}
		            	}catch (Exception e) {
						}
		            } else if (args[0].equalsIgnoreCase("addreward")){
		            	try {
		            	if(args.length == 1) {
		            		p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"/lps addreward [Number Of meet] [command]");
		            		return false;
		            	}
		            	int id = Integer.parseInt(args[1]);
		            	if(id > getConfig().getInt("Max")) {
		            		p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"/lps addreward [1-"+getConfig().getInt("Max")+"]");
		            		return false;
		            	}
		                if(id > 0 && id < 10){
			                String cmds = "";
			                for (String arg : args) {
			                    cmds += arg + " ";
			            }
		                cmds = cmds.substring(12, cmds.length() - 1);
		                List<String> conlist = getConfig().getStringList("Rewards."+id);
		                conlist.add(cmds);
		                getConfig().set("Rewards."+id, conlist);
		                saveConfig();
		                p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"Updated Command! ["+id+"] "+conlist);
		                }
		                if(id > 9 && id < 100){
			                String cmds = "";
			                for (String arg : args) {
			                    cmds += arg + " ";
			            }
		                cmds = cmds.substring(13, cmds.length() - 1);
		                List<String> conlist = getConfig().getStringList("Rewards."+id);
		                conlist.add(cmds);
		                getConfig().set("Rewards."+id, conlist);
		                saveConfig();
		                p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"Updated Command! ["+id+"] "+conlist);
		                }
		                if(id > 99 && id < 1000){
			                String cmds = "";
			                for (String arg : args) {
			                    cmds += arg + " ";
			            }
		                cmds = cmds.substring(14, cmds.length() - 1);
		                List<String> conlist = getConfig().getStringList("Rewards."+id);
		                conlist.add(cmds);
		                getConfig().set("Rewards."+id, conlist);
		                saveConfig();
		                p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"Updated Command! ["+id+"] "+conlist);
		                }
		                if(id > 999 && id < 10000){
			                String cmds = "";
			                for (String arg : args) {
			                    cmds += arg + " ";
			            }
		                cmds = cmds.substring(15, cmds.length() - 1);
		                List<String> conlist = getConfig().getStringList("Rewards."+id);
		                conlist.add(cmds);
		                getConfig().set("Rewards."+id, conlist);
		                saveConfig();
		                p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"Updated Command! ["+id+"] "+conlist);
		                }
		            	}catch (Exception e) {
						}
		            } else if (args[0].equalsIgnoreCase("clearreward")){
		            	int id = Integer.parseInt(args[1]);
		            	if(id > getConfig().getInt("Max")) {
		            		p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"/lps addreward [1-"+getConfig().getInt("Max")+"]");
		            		return false;
		            	}
		                getConfig().set("Rewards."+id, null);
		                saveConfig();
		                p.sendMessage(ChatColor.AQUA+""+ChatColor.BOLD+"LobbyPresent: "+ChatColor.WHITE+"Cleared Reward! ["+id+"]");
		            }
    		} else if(p.isOp() == false){
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cYou don't have permission!"));
				return true;
			}
    		}
		return false;
    }
    public OfflinePlayer uuidToPlayer(UUID uuid) {
    	OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		return player;
    }
     public static Collection<String> ClaimlistSta(UUID uuid){
    	 String Claimed = ClaimString.get(Bukkit.getOfflinePlayer(uuid).getName());
		return Arrays.asList(Claimed.split("\\s*,\\s*"));
     }
	 public Collection<String> ClaimedList(UUID uuid){
		 String Claimed = ClaimString.get(uuidToPlayer(uuid).getName());
		return Arrays.asList(Claimed.split("\\s*,\\s*"));
	 }
	 public String convertClaim(UUID uuid,Integer num){
		 String Claimed = ClaimString.get(uuidToPlayer(uuid).getName());
		 StringBuilder b = new StringBuilder(Claimed);
		 b.append("\\s*"+num);
		 Claimed = Claimed+","+num;
		return Claimed;
	 }
	 public void Claiming(UUID uuid, Integer num) {
		if(getConfig().getBoolean("MYSQL.Enable") == false) {
			if(getClaimed(uuid).equals("#")) {
			getConfig().set("data."+uuid, num);
			saveConfig();
			ClaimString.put(uuidToPlayer(uuid).getName(), num+"");
			}else {
				getConfig().set("data."+uuid, convertClaim(uuid, num));
				saveConfig();
				ClaimString.put(uuidToPlayer(uuid).getName(), convertClaim(uuid, num)+"");
			}
		} else {
			setClaimed(uuid, num);
		}
	 }
	 public void Clearing(UUID uuid) {
		if(getConfig().getBoolean("MYSQL.Enable") == false) {
			getConfig().set("data."+uuid, "#");
			saveConfig();
			ClaimString.put(uuidToPlayer(uuid).getName(), "#");
		} else {
			  Update("UPDATE `"+tb_name+"` SET `claimed`='" + "" + "' WHERE `uuid`='" + uuid + "'");
			    ClaimString.put(uuidToPlayer(uuid).getName(), "");
		}
	 }
	  public void setClaimed(UUID uuid, Integer num){
		  if(ClaimedList(uuid).contains(""+num+"") != true){
			if(getClaimed(uuid).equals("") == false) {
				String Claimed = convertClaim(uuid, num);
			    Update("UPDATE `"+tb_name+"` SET `claimed`='" + Claimed + "' WHERE `uuid`='" + uuid + "'");
			    ClaimString.put(uuidToPlayer(uuid).getName(), convertClaim(uuid, num)+"");
				} else {
					    Update("UPDATE `"+tb_name+"` SET `claimed`='" + num + "' WHERE `uuid`='" + uuid + "'");
					    ClaimString.put(uuidToPlayer(uuid).getName(), num+"");
				}
		  }
	  }
	 public String getClaimed(UUID uuid){
		 String Claimed = null;
		 	Claimed = ClaimString.get(uuidToPlayer(uuid).getName());
		    return Claimed;
		  }
	 public void loadClaimed(UUID uuid){
		 String Claimed = null;
		 if(getConfig().getBoolean("MYSQL.Enable") == false) {
		    Claimed = getConfig().getString("data."+uuid);
		    ClaimString.put(uuidToPlayer(uuid).getName(), Claimed);
		 } else {
			 Claimed = getClaimedDB(uuid);
			 ClaimString.put(uuidToPlayer(uuid).getName(), Claimed);
		 }
		  }
	  public void Update(String qry){
	    try{
	      Statement stmt = con.createStatement();
	      stmt.executeUpdate(qry);
	      
	      stmt.close();
	    }
	    catch (Exception ex){
	      openCon();
	    }
	  }
	 public String getClaimedDB(UUID uuid){
		    String Claimed = "";
		    try
		    {
		      ResultSet rs = Query("SELECT `claimed` FROM `"+tb_name+"` WHERE `uuid`='" + uuid + "'");
		      while (rs.next()) {
		    	  Claimed = rs.getString(1);
		      }
		    }
		    catch (Exception e1)
		    {
		      e1.printStackTrace();
		    }
		    return Claimed;
		  }
	  public ResultSet Query(String qry)
	  {
	    ResultSet rs = null;
	    try
	    {
	      Statement stmt = con.createStatement();
	      rs = stmt.executeQuery(qry);
	    }
	    catch (Exception ex)
	    {
	      openCon();
	    }
	    return rs;
	  }
	  public static Connection openCon(){
	    try{
	      Class.forName("com.mysql.jdbc.Driver");
	    }
	    catch (ClassNotFoundException e1){
	    }
	    try
	    {
	      Connection conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
	      return conn;
	    }
	    catch (SQLException e){
	    }
	    return null;
	  }

	 public void RunEffect(){
		 new BukkitRunnable() {
			 @Override
		        public void run() {
				 try {
				 	PlayEffect();
				 }catch (Exception e) {
				}
				   } 
				}.runTaskTimer(this, 0L, 40L);
		}
		public static String getIntThousan(Integer i) {
			  return NumberFormat.getNumberInstance(Locale.US).format(i);
		}
		public static int getMax() {
			int i = 0;
			try {
			i = LobbyPresents.getInstance().getConfig().getInt("Max");
			return i;
			} catch (Exception e) {
				return 0;
			}
		}
		public static int getFound(OfflinePlayer p) {
			 if(LobbyPresents.getInstance().getClaimed(p.getUniqueId()).equals("#") || LobbyPresents.getInstance().getClaimed(p.getUniqueId()).equals("")) {
				 return 0;
			} else {
				 int total = LobbyPresents.getInstance().ClaimedList(p.getUniqueId()).size();
				 return total;
			}
		}
		public static int getNotFound(OfflinePlayer p) {
			 if(LobbyPresents.getInstance().getClaimed(p.getUniqueId()).equals("#") || LobbyPresents.getInstance().getClaimed(p.getUniqueId()).equals("")) {
				 return getMax();
			} else {
				 int total = LobbyPresents.getInstance().ClaimedList(p.getUniqueId()).size();
				 return getMax()-total;
			}
		}
		public static String getMessage(Player p,String s) {
			if(LobbyPresents.PlaceholderAPI) {
				return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(p, s.replace("%player%", p.getName()));
			}
			return s.replace("%player%", p.getDisplayName());
		}
		public void PlayEffect(){
			 if(!((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).getConfig().getBoolean("enable")) {
				 return;
			 }
			int max = getConfig().getInt("Max");
			if(getConfig().getBoolean("Actionbar") == true) {
			 for(Player p : Bukkit.getOnlinePlayers()){
				 if(getClaimed(p.getUniqueId()).equals("#") || getClaimed(p.getUniqueId()).equals("")) {
					 if(p.getWorld().getName().equals(getConfig().getString("loc.1.w"))) {
						 String total = "0";
					 presents.sendtitlebar(p, ChatColor.translateAlternateColorCodes('&', getMessage(p, getConfig().getString("Messages.actionbar").replaceAll("%max%", max+"").replaceAll("%status%",  total+"/"+max).replaceAll("%found%", total).replaceAll("%player%", p.getName()))));
					 }
				} else {
					 if(p.getWorld().getName().equals(getConfig().getString("loc.1.w"))) {
				 String total = ""+ClaimedList(p.getUniqueId()).size();
				 Boolean complete = (ClaimedList(p.getUniqueId()).size() == max);
				 if(complete) {
					 presents.sendtitlebar(p, ChatColor.translateAlternateColorCodes('&', getMessage(p, getConfig().getString("Messages.actionbar-completed").replaceAll("%max%", max+"").replaceAll("%status%", total+"/"+max).replaceAll("%found%", total).replaceAll("%player%", p.getName()))));
				 } else {
					 presents.sendtitlebar(p, ChatColor.translateAlternateColorCodes('&', getMessage(p, getConfig().getString("Messages.actionbar").replaceAll("%max%", max+"").replaceAll("%status%", total+"/"+max).replaceAll("%found%", total).replaceAll("%player%", p.getName()))));
				 }
					 }
					 }
				 }
			}
			for(int i=1;i<=getConfig().getInt("Max");i++){
				 for(Player p : Bukkit.getOnlinePlayers()){
						World w = Bukkit.getWorld(getConfig().getString("loc."+i+".w"));
						if(ClaimedList(p.getUniqueId()).contains(""+i+"") == false) {
								int x = getConfig().getInt("loc."+i+".x");
								int y = getConfig().getInt("loc."+i+".y");
								int z = getConfig().getInt("loc."+i+".z");
								Location locals = new Location(w, x, y, z);
								Location localalt = new Location(w, x+1, y, z+1);
								Block b = localalt.getBlock();
								Block cb = locals.getBlock();
								 if(p.getWorld().getName().equals(getConfig().getString("loc.1.w"))) {
									 if(cb.getType() == Material.matchMaterial("SKULL_ITEM") || cb.getType() == Material.matchMaterial("SKULL")){
									 presents.GreenVillager(p, b, getConfig().getString("Effect.canclaim"));
									 }
								 }
						 } else {
								int x = getConfig().getInt("loc."+i+".x");
								int y = getConfig().getInt("loc."+i+".y");
								int z = getConfig().getInt("loc."+i+".z");
								Location localalt = new Location(w, x+1, y, z+1);
								Block b = localalt.getBlock();
								Location locals = new Location(w, x, y, z);
								Block cb = locals.getBlock();
								 if(p.getWorld().getName().equals(getConfig().getString("loc.1.w"))) {
									 if(cb.getType() == Material.matchMaterial("SKULL_ITEM") || cb.getType() == Material.matchMaterial("SKULL")){
									 if(getConfig().getBoolean("disappear_on_claim") == false) {
									 presents.HitEffect(p, b, getConfig().getString("Effect.claimed"));
									 } else {
										 presents.sendBlockChange(p, cb.getLocation());
									 }
									 }
								 }
						 }
				 }
			}
		}
	    @EventHandler
	    public void ClickonSkull(PlayerInteractEvent e) {
	    	Player p = e.getPlayer();
	    	if(set.get(p.getName()) == null || set.get(p.getName()) == -1) {
	    		return;
	    	}
	    	if(e.getClickedBlock() == null) {
	    		return;
	    	}
	    	if(e.getClickedBlock().getType() == Material.matchMaterial("SKULL_ITEM") || e.getClickedBlock().getType() == Material.matchMaterial("SKULL")) {
	    	int id = set.get(p.getName());
	    	Block b = e.getClickedBlock();
	    	String w = b.getWorld().getName();
	    	int x = b.getLocation().getBlockX();
	    	int y = b.getLocation().getBlockY();
	    	int z = b.getLocation().getBlockZ();
	    	getConfig().set("loc."+id+".w", w);
	    	getConfig().set("loc."+id+".x", x);
	    	getConfig().set("loc."+id+".y", y);
	    	getConfig().set("loc."+id+".z", z);
	    	set.put(p.getName(), null);
	    	p.sendMessage(ChatColor.GREEN+"Added Presents ["+id+"]!");
	    	saveConfig();
	    	if(id == getConfig().getInt("Max")) {
	    		p.sendMessage(ChatColor.AQUA+"You need to update the locale type: /lps reload");
	    	}
	    	}
	    }

    public static ItemStack getHead(String name){
        for (Heads head : Heads.values()){
            if (head.getName().equalsIgnoreCase(name))
            {
                return head.getItemStack();
            }
        }
        return null;
    }
    public static ItemStack createSkull(String url, String name){
        ItemStack head = new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1, (short)3);
        if (url.isEmpty()) return head;
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", url));
        try{
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        }
        catch (IllegalArgumentException|NoSuchFieldException|SecurityException | IllegalAccessException error){
            error.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
    public static void GUI(Inventory inv, int Slot, String i) {
		ItemStack item = getHead(i);
		ItemMeta meta = item.getItemMeta();
				 meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aPresent "+i)); 
				 item.setItemMeta(meta);
				 inv.setItem(Slot, item); 
    }
	
	public static void pageLeft(Inventory inv,int page) {
		ItemStack item = new ItemStack(Material.ARROW, 1, (short) 0);
		ItemMeta meta = item.getItemMeta();
				 meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aPage "+page));
				 item.setItemMeta(meta);
				 inv.setItem(18, item); 
	}
	
	public static void pageRight(Inventory inv,int page) {
		ItemStack item = new ItemStack(Material.ARROW, 1, (short) 0);
		ItemMeta meta = item.getItemMeta();
				 meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aPage "+page));
				 item.setItemMeta(meta);
				 inv.setItem(26, item); 
	}
	  @EventHandler
	  public void onClickGUI(InventoryClickEvent e){
	    Inventory inv = e.getInventory();
	    Inventory inv1 = Bukkit.createInventory(null, 45, "Present Heads");
	    if (inv.getName().equals(inv1.getName()))
	    {
	      e.setCancelled(true);
	      return;
	    }
	  }
	  public static void openPresentGUI(Player p,int page) {
		  	Inventory i = Bukkit.createInventory(null,54, "Present Heads");
		  	if(page == 1) {
		  		pageRight(i, 2);
		  	} else if(page == 2) {
		  		pageLeft(i, 1);
		  		pageRight(i, 3);
		  	} else if(page == 3) {
		  		pageLeft(i, 2);
		  	}
		  	for(int c=1;c<=28;c++) {
		  	if(page==1) {
		  		if(c <= 7){
		  			int x = c+9;
		  			GUI(i, x, c+"");
		  		} else if(c > 7 && c <= 14){
		  			int x = c+11;
		  			GUI(i, x, c+"");
		  		} else if(c > 14 && c <= 21){
		  			int x = c+13;
		  			GUI(i, x, c+"");
		  		} else if(c > 21 && c <= 28){
		  			int x = c+15;
		  			GUI(i, x, c+"");
		  		}
		  	} else if(page == 2) {
		  		int num = c+28;
		  		if(c <= 7){
		  			int x = c+9;
		  			GUI(i, x, num+"");
		  		} else if(c > 7 && c <= 14){
		  			int x = c+11;
		  			GUI(i, x, num+"");
		  		} else if(c > 14 && c <= 21){
		  			int x = c+13;
		  			GUI(i, x, num+"");
		  		} else if(c > 21 && c <= 28){
		  			int x = c+15;
		  			GUI(i, x, num+"");
		  		}
		  	} else if(page == 3) {
		  		int num = c+56;
		  		if(c <= 7){
		  			int x = c+9;
		  			GUI(i, x, num+"");
		  		} else if(c > 7 && c <= 14){
		  			int x = c+11;
		  			GUI(i, x, num+"");
		  		} else if(c > 14 && c <= 21){
		  			int x = c+13;
		  			GUI(i, x, num+"");
		  		} else if(c > 21 && c <= 28){
		  			int x = c+15;
		  			GUI(i, x, num+"");
		  		}
		  	}
		  	}
		  	p.openInventory(i);
	  }
	  @EventHandler
	  public void onInventoryClick(InventoryClickEvent e) {
		  Inventory inventory = e.getInventory();
		  Player p = (Player) e.getWhoClicked();
		  ItemStack c = e.getCurrentItem();
	  if (inventory.getName().equals("Present Heads")) {
	        if(c == null || c.getType()== Material.AIR){
                e.setCancelled(true);
                return;
	        }
            if(c.hasItemMeta() && c.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&aPage 1"))){
            	openPresentGUI(p, 1);
            } else if(c.hasItemMeta() && c.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&aPage 2"))){
            	openPresentGUI(p, 2);
            } else if(c.hasItemMeta() && c.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&aPage 3"))){
            	openPresentGUI(p, 3);
            }
            for(int i=1;i<=84;i++) {
            	if(c.hasItemMeta() && c.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&aPresent "+i))){
            		p.getInventory().addItem(getHead(i+""));
            		presents.PickupSound(p);
            	}
            }
	  	}
	  }
	  @EventHandler
	  public void JoinEvent(PlayerJoinEvent e) {
		  Player p = e.getPlayer();
		  if(getConfig().getBoolean("MYSQL.Enable") == false) {
	    	if(getConfig().getString("data."+p.getUniqueId()) == null){
	    		getConfig().set("data."+p.getUniqueId(), "#");
	    		saveConfig();
	    	}
		  }
	  }
	  public void effectclick(Player p) {
		  presents.effectclick(p);
	  }
	  public void FoundSound(Player p) {
		  presents.FoundSound(p, 0.8f);
	  }


}
