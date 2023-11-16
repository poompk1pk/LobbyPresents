package com.github.poompk1pk.lobbypresents.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.poompk1pk.lobbypresents.config.ConfigFile;
import com.github.poompk1pk.lobbypresents.Main;
import com.github.poompk1pk.lobbypresents.versions.v188.listeners.Presents_18;
import com.github.poompk1pk.lobbypresents.type.ConfigType;
import com.github.poompk1pk.lobbypresents.utils.PresentsUtils;
import com.github.poompk1pk.lobbypresents.listeners.Setup;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player)sender;
      if (cmd.getName().equalsIgnoreCase("lobbypresents")) {
        if (!p.hasPermission("lobbypresents.admin")) {
          PresentsUtils.chat(sender, "&cYou don't have permission!");
          return false;
        } 
        if (args.length == 0) {
          help_msg((CommandSender)p, 1);
          return false;
        } 
        if (args[0].equalsIgnoreCase("help")) {
          if (args.length == 1) {
            help_msg((CommandSender)p, 1);
            return false;
          } 
          try {
            int page = Integer.parseInt(args[1]);
            help_msg((CommandSender)p, page);
          } catch (NumberFormatException e) {
            PresentsUtils.chat((CommandSender)p, "&cWrong agrument: /lps help {INTEGER_NUMBER}");
            return false;
          } 
        } else if (args[0].equalsIgnoreCase("set")) {
          try {
            int id = Integer.parseInt(args[1]);
            PresentsUtils.setcanSetupPresent(p, id);
          } catch (NumberFormatException e) {
            PresentsUtils.chat((CommandSender)p, "&cWrong agrument: /lps set {INTEGER_NUMBER}");
            return false;
          } 
        } else if (args[0].equalsIgnoreCase("remove")) {
          try {
            int id = Integer.parseInt(args[1]);
            PresentsUtils.removePresentsID((CommandSender)p, id);
          } catch (NumberFormatException e) {
            PresentsUtils.chat((CommandSender)p, "&cWrong agrument: /lps remove {INTEGER_NUMBER}");
            return false;
          } 
        } else if (args[0].equalsIgnoreCase("list")) {
          PresentsUtils.chat((CommandSender)p, "&aThe presents id list:");
          try {
            for (String s : ((ConfigFile)PresentsUtils.config.get(ConfigType.Presents)).getConfig().getConfigurationSection("presents").getKeys(false))
              PresentsUtils.chat((CommandSender)p, " &7- &fID[&c" + s + "&f]"); 
          } catch (Exception e) {
            PresentsUtils.chat((CommandSender)p, "&7 Not found presents!");
          } 
        } else if (args[0].equalsIgnoreCase("tp")) {
          if (args.length == 1) {
            PresentsUtils.chat((CommandSender)p, "&cWrong agrument: /lps tp {id}");
            return false;
          } 
          try {
            int id = Integer.parseInt(args[1]);
            if (Main.is18) {
              if (!PresentsUtils.Pressents_18.containsKey((new StringBuilder(String.valueOf(id))).toString())) {
                PresentsUtils.chat(sender, "&cNot found the present &fID[&c" + id + "&f]");
                return false;
              } 
              p.teleport(((Presents_18)PresentsUtils.Pressents_18.get((new StringBuilder(String.valueOf(id))).toString())).getLocationPresents());
              PresentsUtils.chat(sender, "&aTeleport to the presents &fID[&c" + id + "&f]");
            } else {
              /*
				 * if (!PresentsUtils.Pressents_19.containsKey((new
				 * StringBuilder(String.valueOf(id))).toString())) { PresentsUtils.chat(sender,
				 * "&cNot found the present &fID[&c" + id + "&f]"); return false; }
				 * p.teleport(((Presents_19)PresentsUtils.Pressents_19.get((new
				 * StringBuilder(String.valueOf(id))).toString())).getLocationPresents());
				 * PresentsUtils.chat(sender, "&aTeleport to the presents &fID[&c" + id +
				 * "&f]");
				 */
            } 
          } catch (NumberFormatException e) {
            PresentsUtils.chat((CommandSender)p, "&cWrong agrument: /lps tp {INTEGER_NUMBER}");
            return false;
          } 
        } else if (args[0].equalsIgnoreCase("reward")) {
          if (args.length == 1) {
            PresentsUtils.chat(sender, "&a&lRewards:");
            PresentsUtils.chat(sender, "&7 type: &6" + PresentsUtils.reward_type);
            if (PresentsUtils.reward_type.equalsIgnoreCase("custom")) {
              int i = 1;
              for (Iterator<Integer> iterator = PresentsUtils.custom_rewards.keySet().iterator(); iterator.hasNext(); ) {
                int s = ((Integer)iterator.next()).intValue();
                PresentsUtils.chat(sender, "&7 " + i + ". id=" + s + " cmds= [" + (String)PresentsUtils.custom_rewards.get(Integer.valueOf(s)) + "]");
                i++;
              } 
            } else if (PresentsUtils.reward_type.equalsIgnoreCase("sequence")) {
              for (Iterator<Integer> iterator = PresentsUtils.sequence_rewards.keySet().iterator(); iterator.hasNext(); ) {
                int s = ((Integer)iterator.next()).intValue();
                PresentsUtils.chat(sender, "&7 " + s + ". sequence=" + s + " cmds= [" + (String)PresentsUtils.sequence_rewards.get(Integer.valueOf(s)) + "]");
              } 
            } else if (PresentsUtils.reward_type.equalsIgnoreCase("both")) {
              int i = 1;
              PresentsUtils.chat(sender, " &6Custom:");
              Iterator<Integer> iterator;
              for (iterator = PresentsUtils.custom_rewards.keySet().iterator(); iterator.hasNext(); ) {
                int s = ((Integer)iterator.next()).intValue();
                PresentsUtils.chat(sender, "&7 " + i + ". id=" + s + " cmds= [" + (String)PresentsUtils.custom_rewards.get(Integer.valueOf(s)) + "]");
                i++;
              } 
              PresentsUtils.chat(sender, " &6Sequence:");
              for (iterator = PresentsUtils.sequence_rewards.keySet().iterator(); iterator.hasNext(); ) {
                int s = ((Integer)iterator.next()).intValue();
                PresentsUtils.chat(sender, "&7 " + s + ". sequence=" + s + " cmds= [" + (String)PresentsUtils.sequence_rewards.get(Integer.valueOf(s)) + "]");
              } 
            } 
            return false;
          } 
          if (args[1].equalsIgnoreCase("settype")) {
            if (args.length == 2) {
              PresentsUtils.chat(sender, "&cWrong agrument: /lps reward settype {sequence/custom/both}");
              return false;
            } 
            if (args[2].equalsIgnoreCase("sequence") || args[2].equalsIgnoreCase("custom") || args[2].equalsIgnoreCase("both")) {
              PresentsUtils.chat(sender, "&7Rewards settype to: &6" + args[2].toLowerCase());
              PresentsUtils.setRewardType(args[2]);
              ((ConfigFile)PresentsUtils.config.get(ConfigType.Presents)).save();
            } else {
              PresentsUtils.chat(sender, "&c/lps reward setype sequence, custom or both");
            } 
          } else if (args[1].equalsIgnoreCase("setsequence")) {
            if (args.length == 2) {
              PresentsUtils.chat((CommandSender)p, "&cWrong agrument: /lps reward setsequence {sequence} {none or command1,command2}");
              return false;
            } 
            try {
              int id = Integer.parseInt(args[2]);
              String cmds = "";
              if (args.length < 3) {
                PresentsUtils.chat((CommandSender)p, "&cWrong agrument: /lps reward setsequence " + id + " none or command1,command2");
                return false;
              } 
              int i = 0;
              byte b;
              int j;
              String[] arrayOfString;
              for (j = (arrayOfString = args).length, b = 0; b < j; ) {
                String s = arrayOfString[b];
                if (i >= 3)
                  cmds = String.valueOf(cmds) + s + " "; 
                i++;
                b++;
              } 
              PresentsUtils.setSequenceRewards(sender, id, cmds.substring(0, cmds.length() - 1));
              PresentsUtils.chat(sender, "&bSequence-Reward[" + id + "]: " + cmds.substring(0, cmds.length() - 1));
            } catch (NumberFormatException e) {
              PresentsUtils.chat((CommandSender)p, "&cWrong agrument: /lps reward setsequence {INTEGER_NUMBER} {NONE,COMMAND1,COMMAND2,...}");
            } 
          } else if (args[1].equalsIgnoreCase("setcustom")) {
            if (args.length == 2) {
              PresentsUtils.chat((CommandSender)p, "&cWrong agrument: /lps reward setcustom {sequence} {none or command1,command2}");
              return false;
            } 
            try {
              int id = Integer.parseInt(args[2]);
              if (((ConfigFile)PresentsUtils.config.get(ConfigType.Presents)).getConfig().getString("presents." + id) == null) {
                PresentsUtils.chat(sender, "&cNot found the pressent id: " + id);
                return false;
              } 
              String cmds = "";
              int i = 0;
              byte b;
              int j;
              String[] arrayOfString;
              for (j = (arrayOfString = args).length, b = 0; b < j; ) {
                String s = arrayOfString[b];
                if (i >= 3)
                  cmds = String.valueOf(cmds) + s + " "; 
                i++;
                b++;
              } 
              PresentsUtils.setCustomRewards(sender, id, cmds.substring(0, cmds.length() - 1));
            } catch (NumberFormatException e) {
              PresentsUtils.chat((CommandSender)p, "&cWrong agrument: /lps reward setsequence {INTEGER_NUMBER} {NONE,COMMAND1,COMMAND2,...}");
            } 
          } 
        } else if (args[0].equalsIgnoreCase("clearuserdata")) {
          if (args.length == 1) {
            PresentsUtils.chat(sender, "&c/lps clearuserdatta {PLAYERNAME}");
            return false;
          } 
          Player p2 = Bukkit.getPlayer(args[1]);
          if (p2 == null) {
            PresentsUtils.chat(sender, "&cPlayer is not online");
            return false;
          } 
          PresentsUtils.clearUserData(sender, p2);
        } else if (args[0].equalsIgnoreCase("actionbar")) {
          if(args.length == 1) {
            PresentsUtils.chat(sender, "&c/lps actionbar addworld");
            PresentsUtils.chat(sender, "&c/lps actionbar removeworld");
            PresentsUtils.chat(sender, "&c/lps actionbar enable/disable");
            return false;
          }
          if(args[1].equalsIgnoreCase("addworld")) {
            List<String> stringList = PresentsUtils.config.get(ConfigType.Default).getConfig().getStringList("Actionbar.world");
            stringList.add(p.getWorld().getName());
            PresentsUtils.config.get(ConfigType.Default).getConfig().set("Actionbar.world",stringList);
            ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).save();
            p.sendMessage("added "+p.getWorld().getName());
          } else if(args[1].equalsIgnoreCase("removeworld")) {
            List<String> stringList = PresentsUtils.config.get(ConfigType.Default).getConfig().getStringList("Actionbar.world");
            ArrayList<String> newWorlds = new ArrayList<>();
            Iterator<String> iterator = stringList.iterator();
            while (iterator.hasNext()) {
              String next = iterator.next();
              if(!next.equals(p.getWorld().getName())) {
                newWorlds.add(p.getWorld().getName());
              } else {
                p.sendMessage("removed "+next);
              }
            }
            PresentsUtils.config.get(ConfigType.Default).getConfig().set("Actionbar.world",newWorlds);
            ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).save();
          } else if(args[1].equalsIgnoreCase("enable")) {
            PresentsUtils.config.get(ConfigType.Default).getConfig().set("Actionbar.enable",true);
            ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).save();

          } else if(args[1].equalsIgnoreCase("disable")) {
            PresentsUtils.config.get(ConfigType.Default).getConfig().set("Actionbar.enable",false);
            ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).save();
          }

          p.sendMessage("saved! config (NOT RELOAD)");
        } else if (args[0].equalsIgnoreCase("save")) {
          ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).save();
          ((ConfigFile)PresentsUtils.config.get(ConfigType.Heads)).save();
          ((ConfigFile)PresentsUtils.config.get(ConfigType.Presents)).save();
          p.sendMessage("saved! config");
        } else if (args[0].equalsIgnoreCase("enable")) {
          ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).getConfig().set("enable", true);
          ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).save();
          PresentsUtils.reload(sender);
          p.sendMessage("enable!");
        } else if (args[0].equalsIgnoreCase("disable")) {
          ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).getConfig().set("enable", false);
          ((ConfigFile)PresentsUtils.config.get(ConfigType.Default)).save();
          PresentsUtils.reload(sender);
          p.sendMessage("disable!");

        } else if (args[0].equalsIgnoreCase("reload")) {
          PresentsUtils.reload(sender);
        } else if (args[0].equalsIgnoreCase("heads")) {
          Setup.openPresentGUI(p, 1);
        } else if (args[0].equalsIgnoreCase("checkversion")) {
          PresentsUtils.CheckVersionSystem(sender);
        } 
      } 
    } else if (sender instanceof org.bukkit.command.ConsoleCommandSender && 
      cmd.getName().equalsIgnoreCase("lobbypresents")) {
      if (args.length == 0) {
        PresentsUtils.chat(sender, "&aLobbypresents Console help:");
        PresentsUtils.chat(sender, "lps checkversion");
        PresentsUtils.chat(sender, "lps reload");
        return false;
      } 
      if (args[0].equalsIgnoreCase("checkversion")) {
        PresentsUtils.CheckVersionSystem(sender);
      } else if (args[0].equalsIgnoreCase("reload")) {
        PresentsUtils.reload(sender);
      } 
    } 
    return false;
  }
  
  public static void help_msg(CommandSender p, int page) {
    ArrayList<String> help = new ArrayList<>();
    help.add(" &a/lps &7help &c{page}");
    help.add(" &a/lps &7enable/disable");
    help.add(" &a/lps &7list");
    help.add(" &a/lps &7tp &c{id}");
    help.add(" &a/lps &7set &c{id}");
    help.add(" &a/lps &7remove &c{id}");
    help.add(" &a/lps &7reward &7settype &c{sequence/custom/both}");
    help.add(" &a/lps &7reward setsequence &c{1-total} &c{none/cmd1, cmd2, ...}");
    help.add(" &a/lps &7reward setcustom &c{id_presents} &c{none/cmd1, cmd2, ...}");
    help.add(" &a/lps &7heads");
    help.add(" &a/lps &7clearuserdata &c{playername}");
    help.add(" &a/lps &7save");
    help.add(" &a/lps &7actionbar addworld/removeworld");
    help.add(" &a/lps &7actionbar enable/disable");
    help.add(" &a/lps &7reload");
    help.add(" &a/lps &7checkversion");
    help.add("");
    help.add(" &6LobbyPresents by PoomPK");
    int total = (int)Math.ceil(help.size() / 8.0D);
    PresentsUtils.chat(p, "");
    PresentsUtils.chat(p, " &c&lLobby&f&lPresents&a&l v." + Main.getInstance().getDescription().getVersion() + " &7Help menu &6(&e" + page + "&6/&e" + total + "&6)&7:");
    if (page > total || page < 0) {
      PresentsUtils.chat(p, "");
      PresentsUtils.chat(p, "        &cPage not found!");
      PresentsUtils.chat(p, "&4&l&m   &c&l&m   &4&l&m   &c&l&m   &4&l&m   &c&l&m   &4&l&m   &c&l&m   &4&l&m   &c&l&m   &4&l&m   &c&l&m   ");
      PresentsUtils.chat(p, "");
      return;
    } 
    int end = page * 8;
    int first = end - 8;
    for (int i = first; i < end; i++) {
      if (i >= help.size()) {
        PresentsUtils.chat(p, "&4&l&m   &c&l&m   &4&l&m   &c&l&m   &4&l&m   &c&l&m   &4&l&m   &c&l&m   &4&l&m   &c&l&m   ");
        return;
      } 
      PresentsUtils.chat(p, help.get(i));
    } 
    PresentsUtils.chat(p, "&4&l&m   &c&l&m   &4&l&m   &c&l&m   &4&l&m   &c&l&m   &4&l&m   &c&l&m   &4&l&m   &c&l&m   ");
  }
}
