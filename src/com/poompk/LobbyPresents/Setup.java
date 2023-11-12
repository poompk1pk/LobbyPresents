package com.poompk.LobbyPresents;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

public class Setup implements Listener {
  @EventHandler
  public void onPlayerInteractSkullSetup(PlayerInteractEvent e) {
    if (!PresentsUtils.cansetup.containsKey(e.getPlayer().getUniqueId()))
      return; 
    if (e.getClickedBlock().getType() == Material.matchMaterial("SKULL") || e.getClickedBlock().getType() == Material.matchMaterial("LEGACY_AIR")) {
      String loc = String.valueOf(e.getClickedBlock().getLocation().getWorld().getName()) + ":" + e.getClickedBlock().getLocation().getBlockX() + ":" + e.getClickedBlock().getLocation().getBlockY() + ":" + e.getClickedBlock().getLocation().getBlockZ();
      PresentsUtils.setSetupToConfig(loc, ((Integer)PresentsUtils.cansetup.get(e.getPlayer().getUniqueId())).intValue());
      PresentsUtils.chat((CommandSender)e.getPlayer(), "&aSet " + PresentsUtils.cansetup.get(e.getPlayer().getUniqueId()) + " complete!");
      PresentsUtils.cansetup.remove(e.getPlayer().getUniqueId());
    } 
  }
  
  @EventHandler
  public void onPlayerJoinGame(AsyncPlayerPreLoginEvent e) {
    if (Main.isMysql) {
      try {
        Main.getInstance().InsertDataDefault(e.getUniqueId());
        PresentsUtils.onJoinloadProfile(e.getUniqueId());
      } catch (Exception exception) {}
    } else {
      PresentsUtils.onJoinloadProfile(e.getUniqueId());
    } 
  }
  
  @EventHandler
  public void onPlayerLeftGame(PlayerQuitEvent e) {
    PresentsUtils.profile.remove(e.getPlayer().getUniqueId());
    PresentsUtils.cansetup.remove(e.getPlayer().getUniqueId());
  }
  
  @EventHandler
  public void onClickGUI(InventoryClickEvent e) {
    Inventory inv = e.getInventory();
    if (inv.getName().contains("Present Heads (")) {
      e.setCancelled(true);
      return;
    } 
  }
  
  public static void loadHead(final Inventory inv, final int page, final int total, final int all_page, final CallBack callback) {
    Bukkit.getScheduler().runTaskAsynchronously((Plugin)Main.getInstance(), new Runnable() {
          public void run() {
            if (page <= 1 && all_page > page) {
              Setup.pageRight(inv, page + 1);
            } else if (page > 1 && page < all_page) {
              Setup.pageRight(inv, page + 1);
              Setup.pageLeft(inv, page - 1);
            } else if (page >= all_page) {
              Setup.pageLeft(inv, page - 1);
            } 
            int slot = 10;
            for (int i = 28 * page - 28; i < 28 * page && 
              i <= total - 1; i++) {
              if (slot == 9 || slot == 18 || slot == 27 || slot == 36 || slot == 45) {
                slot++;
              } else if (slot == 8 || slot == 17 || slot == 26 || slot == 35 || slot == 44) {
                slot += 2;
              } 
              Setup.setSlot(inv, slot, PresentsUtils.Heads.get(i));
              slot++;
            } 
            callback.onSuccess();
          }
        });
  }
  
  public static void openPresentGUI(final Player p, int page) {
	    int total = PresentsUtils.Heads.size();
	    int all_page = (int)Math.ceil(total / 28.0D);
	    Inventory inv = Bukkit.createInventory(null, 54, "Present Heads (" + page + "/" + all_page + ")");
	    p.openInventory(inv);
	    loadHead(inv, page, total, all_page, new CallBack() {
	          public void onSuccess() {
	            p.updateInventory();
	          }
	        });
	  }
  
  @EventHandler
  public void onInventoryClick(InventoryClickEvent e) {
    Inventory inventory = e.getInventory();
    Player p = (Player)e.getWhoClicked();
    ItemStack c = e.getCurrentItem();
    if (inventory.getName().contains("Present Heads (")) {
      if (c == null || c.getType() == Material.AIR) {
        e.setCancelled(true);
        return;
      } 
      if (c.hasItemMeta() && c.getType() == Material.matchMaterial("ARROW") && c.getItemMeta().getDisplayName().contains(ChatColor.translateAlternateColorCodes('&', "&aPage ")))
        openPresentGUI(p, Integer.parseInt(c.getItemMeta().getDisplayName().split(ChatColor.translateAlternateColorCodes('&', "&aPage "))[1])); 
      if (c.hasItemMeta() && c.getType() == Material.matchMaterial("SKULL_ITEM"))
        p.getInventory().addItem(new ItemStack[] { c }); 
    } 
  }
  
  public static ItemStack createSkull(String url, String title) {
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
    headMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a" + title));
    head.setItemMeta((ItemMeta)headMeta);
    return head;
  }
  
  public static void setSlot(Inventory inv, int slot, ItemStack is) {
    inv.setItem(slot, is);
  }
  
  public static void pageLeft(Inventory inv, int page) {
    ItemStack item = new ItemStack(Material.ARROW, 1, (short)0);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aPage " + page));
    item.setItemMeta(meta);
    inv.setItem(18, item);
  }
  
  public static void pageRight(Inventory inv, int page) {
    ItemStack item = new ItemStack(Material.ARROW, 1, (short)0);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&aPage " + page));
    item.setItemMeta(meta);
    inv.setItem(26, item);
  }
  
  public static ItemStack ItemSkull(int amount, String playername, String title) {
    ItemStack skull = new ItemStack(Material.matchMaterial("SKULL_ITEM"), 1, (short)3);
    SkullMeta meta = (SkullMeta)skull.getItemMeta();
    meta.setOwner(playername);
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a" + title));
    skull.setItemMeta((ItemMeta)meta);
    return skull;
  }
  
  public static ItemStack Item(String material, int amount, int shrt, String name, List<String> lore) {
    ItemStack item = new ItemStack(Material.matchMaterial(material), amount, (short)shrt);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
    meta.setLore(lore);
    item.setItemMeta(meta);
    return item;
  }
}
