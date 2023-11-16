package com.github.poompk1pk.lobbypresents;

import com.github.poompk1pk.lobbypresents.Main;
import com.github.poompk1pk.lobbypresents.config.ConfigFile;
import com.github.poompk1pk.lobbypresents.type.ConfigType;
import com.github.poompk1pk.lobbypresents.utils.PresentsUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Profile {
  private final UUID uuid;
  private final ConfigFile config;

  // Cache with a time-based expiration of 5 seconds
  private static final Cache<UUID, Set<Integer>> claimCache = CacheBuilder.newBuilder()
          .expireAfterWrite(5, TimeUnit.SECONDS)
          .build();

  public Profile(UUID uuid) {
    this.uuid = uuid;
    this.config = PresentsUtils.config.get(ConfigType.PlayerData);
  }

  public UUID getUUID() {
    return uuid;
  }

  public ConfigFile getConfig() {
    return config;
  }

  public void addClaim(int id) {
    Set<Integer> ids = getClaim();
    if (ids.add(id)) {
      updateClaim(ids);
    }
  }

  public void clearClaim() {
    updateClaim(new HashSet<>());
  }

  public Set<Integer> getClaim() {
    // Check the cache first
    Set<Integer> cachedClaim = claimCache.getIfPresent(uuid);
    if (cachedClaim != null) {
      return cachedClaim;
    }

    Set<Integer> ids = new HashSet<>();
    String claimData = Main.isMysql ? Main.getClaimedDB(uuid) : getConfig().getConfig().getString("user." + getUUID());
    if(claimData.equals("[]")) {
      claimData = "";
    }
    if (claimData != null && !claimData.isEmpty()) {
      String[] idStrings = claimData.split(",");
      for (String id : idStrings) {
        ids.add(Integer.parseInt(id));
      }
    }

    // Put the result in the cache
    claimCache.put(uuid, ids);

    return ids;
  }

  private void updateClaim(Set<Integer> ids) {
    String claimString = String.join(",", ids.toString()).replace("[", "").replace("]", "").replace(" ", "");
    if (Main.isMysql) {
      Main.Update("UPDATE `" + Main.getTb_name() + "` SET `claimed`='" + claimString + "' WHERE `uuid`='" + uuid + "'");
    } else {
      getConfig().getConfig().set("user." + getUUID(), claimString);
      getConfig().save();
    }

    // Remove the entry from the cache when data is updated
    claimCache.invalidate(uuid);
  }
}
