package com.github.poompk1pk.lobbypresents.databases;

import com.github.poompk1pk.lobbypresents.config.ConfigFile;
import com.github.poompk1pk.lobbypresents.type.ConfigType;
import com.github.poompk1pk.lobbypresents.utils.PresentsUtils;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class YmlDB implements LobbyPresentsDataManager{

    @Override
    public boolean initialData() {
        return true;
    }

    @Override
    public void clearAllData(CommandSender sender) {
        ((ConfigFile)PresentsUtils.config.get(ConfigType.PlayerData)).getConfig().set("user",null);
        ((ConfigFile)PresentsUtils.config.get(ConfigType.PlayerData)).save();
    }


    @Override
    public String getClaimed(UUID uuid) {
        return PresentsUtils.config.get(ConfigType.PlayerData).getConfig().getString("user." + uuid);
    }

    @Override
    public void updateClaim(UUID uuid, String claimString) {
        PresentsUtils.config.get(ConfigType.PlayerData).getConfig().set("user." + uuid, claimString);
        PresentsUtils.config.get(ConfigType.PlayerData).save();
    }

    @Override
    public boolean insertDefaultData(UUID uuid) {
        return true;
    }

    @Override
    public void shutdown() {

    }
}
