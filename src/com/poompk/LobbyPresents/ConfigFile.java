package com.poompk.LobbyPresents;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigFile {
  private FileConfiguration config;
  
  private File file;
  
  public ConfigFile(File file1) {
    this.file = file1;
    this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(file1);
  }
  
  public FileConfiguration getConfig() {
    return this.config;
  }
  
  public void reload() {
    this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.file);
  }
  
  public void save() {
    try {
      this.config.save(this.file);
    } catch (IOException ioexception) {
      ioexception.printStackTrace();
    } 
  }
}
