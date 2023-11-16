package com.github.poompk1pk.lobbypresents.listeners;

import com.github.poompk1pk.lobbypresents.Main;
import com.github.poompk1pk.lobbypresents.utils.PresentsUtils;
import com.github.poompk1pk.lobbypresents.presents.api.events.PlayerClickClaimedPresentEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class ClickPresentListener implements Listener {


  @EventHandler
  public void onPlayerClickPresents(PlayerClickClaimedPresentEvent e) {

    new BukkitRunnable() {
      @Override
      public void run() {
        Main.getPresents().effectclick(e.getPlayer());
        if (!PresentsUtils.getTextNormalMessage(e.getPlayer()).equalsIgnoreCase("none"))
          PresentsUtils.chat((CommandSender)e.getPlayer(), Main.getMessage(e.getPlayer(), PresentsUtils.getTextNormalMessage(e.getPlayer())));
        if (PresentsUtils.reward_type.equalsIgnoreCase("none"))
          return;
        if (PresentsUtils.reward_type.equalsIgnoreCase("sequence")) {
          String[] cmds1 = ((String)PresentsUtils.sequence_rewards.get(Integer.valueOf(e.getProfile().getClaim().size()))).replace("%player%", e.getPlayer().getName()).split(",");
          byte b;
          int i;
          String[] arrayOfString1;
          for (i = (arrayOfString1 = cmds1).length, b = 0; b < i; ) {
            String s = arrayOfString1[b];
            if (s.equalsIgnoreCase("none"))
              break;
            new BukkitRunnable() {
              @Override
              public void run() {
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), s);
              }
            }.runTask(Main.getInstance());
            b++;
          }
        } else if (PresentsUtils.reward_type.equalsIgnoreCase("custom")) {
          String[] cmds = ((String)PresentsUtils.custom_rewards.get(Integer.valueOf(e.getID()))).replace("%player%", e.getPlayer().getName()).split(",");
          byte b;
          int i;
          String[] arrayOfString1;
          for (i = (arrayOfString1 = cmds).length, b = 0; b < i; ) {
            String s = arrayOfString1[b];
            if (s.equalsIgnoreCase("none"))
              break;
            new BukkitRunnable() {
              @Override
              public void run() {
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), s);
              }
            }.runTask(Main.getInstance());
            b++;
          }
        } else if (PresentsUtils.reward_type.equalsIgnoreCase("both")) {
          String[] cmds1 = ((String)PresentsUtils.sequence_rewards.get(Integer.valueOf(e.getProfile().getClaim().size()))).replace("%player%", e.getPlayer().getName()).split(",");
          String[] cmds2 = ((String)PresentsUtils.custom_rewards.get(Integer.valueOf(e.getID()))).replace("%player%", e.getPlayer().getName()).split(",");
          byte b;
          int i;
          String[] arrayOfString1;
          for (i = (arrayOfString1 = cmds1).length, b = 0; b < i; ) {
            String s = arrayOfString1[b];
            if (s.equalsIgnoreCase("none"))
              break;
            new BukkitRunnable() {
              @Override
              public void run() {
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), s);
              }
            }.runTask(Main.getInstance());
            b++;
          }
          for (i = (arrayOfString1 = cmds2).length, b = 0; b < i; ) {
            String s = arrayOfString1[b];
            if (s.equalsIgnoreCase("none"))
              break;
            new BukkitRunnable() {
              @Override
              public void run() {
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), s);
              }
            }.runTask(Main.getInstance());
            b++;
          }
        }
      }
    }.runTaskAsynchronously(Main.getInstance());

  }
}
