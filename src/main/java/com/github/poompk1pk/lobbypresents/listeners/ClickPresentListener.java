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

    // async task prevents server freeze
    new BukkitRunnable() {
      @Override
      public void run() {
        Main.getPresents().effectclick(e.getPlayer());
        if (!PresentsUtils.getTextNormalMessage(e.getPlayer()).equalsIgnoreCase("none"))
          PresentsUtils.chat(e.getPlayer(), Main.getMessage(e.getPlayer(), PresentsUtils.getTextNormalMessage(e.getPlayer())));
        if (PresentsUtils.reward_type.equalsIgnoreCase("none"))
          return;
        if (PresentsUtils.reward_type.equalsIgnoreCase("sequence")) {
          if (PresentsUtils.sequence_rewards.isEmpty()) {
            return;
          }
          String cmd = PresentsUtils.sequence_rewards.get(Integer.valueOf(e.getProfile().getClaim().size()));
          if(cmd == null) return;
          String[] cmds1 = cmd.replace("%player%", e.getPlayer().getName()).split(",");
          byte b;
          int i;
          String[] arrayOfString1;
          for (i = (arrayOfString1 = cmds1).length, b = 0; b < i; ) {
            String s = arrayOfString1[b];
            if (s.equalsIgnoreCase("none"))
              break;
            // commands still need sync task
            new BukkitRunnable() {
              @Override
              public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
              }
            }.runTask(Main.getInstance());
            b++;
          }
        } else if (PresentsUtils.reward_type.equalsIgnoreCase("custom")) {
          String s1 = PresentsUtils.custom_rewards.get(Integer.valueOf(e.getID()));
          if (s1==null) {
            return;
          }
          String[] cmds = s1.replace("%player%", e.getPlayer().getName()).split(",");
          byte b;
          int i;
          String[] arrayOfString1;
          for (i = (arrayOfString1 = cmds).length, b = 0; b < i; ) {
            String s = arrayOfString1[b];
            if (s.equalsIgnoreCase("none"))
              break;
            // commands still need sync task
            new BukkitRunnable() {
              @Override
              public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
              }
            }.runTask(Main.getInstance());
            b++;
          }
        } else if (PresentsUtils.reward_type.equalsIgnoreCase("both")) {

          byte b;
          int i;
          String[] arrayOfString1;
          String s1 = PresentsUtils.sequence_rewards.get(Integer.valueOf(e.getProfile().getClaim().size()));
          if(s1 != null) {
            String[] cmds1 = s1.replace("%player%", e.getPlayer().getName()).split(",");

            for (i = (arrayOfString1 = cmds1).length, b = 0; b < i; ) {
              String s = arrayOfString1[b];
              if (s.equalsIgnoreCase("none"))
                break;
              // commands still need sync task
              new BukkitRunnable() {
                @Override
                public void run() {
                  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
                }
              }.runTask(Main.getInstance());
              b++;
            }
          }
          String s2 = PresentsUtils.custom_rewards.get(Integer.valueOf(e.getID()));
          if(s2 != null) {
            String[] cmds2 = s2.replace("%player%", e.getPlayer().getName()).split(",");

            for (i = (arrayOfString1 = cmds2).length, b = 0; b < i; ) {
              String s = arrayOfString1[b];
              if (s.equalsIgnoreCase("none"))
                break;
              // commands still need sync task
              new BukkitRunnable() {
                @Override
                public void run() {
                  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
                }
              }.runTask(Main.getInstance());
              b++;
            }
          }

        }
      }
    }.runTaskAsynchronously(Main.getInstance());

  }
}
