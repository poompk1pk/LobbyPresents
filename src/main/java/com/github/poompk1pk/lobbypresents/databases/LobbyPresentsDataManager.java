package com.github.poompk1pk.lobbypresents.databases;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface LobbyPresentsDataManager {
    public boolean initialData();
    public void clearAllData(CommandSender sender);
    public String getClaimed(UUID uuid);
    public void updateClaim(UUID uuid,String data);
    public boolean insertDefaultData(UUID uuid);
    public void shutdown();
}
