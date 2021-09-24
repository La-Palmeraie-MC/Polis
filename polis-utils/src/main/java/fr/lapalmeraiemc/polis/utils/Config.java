package fr.lapalmeraiemc.polis.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;


public class Config {

  private final Plugin plugin;

  private final FileManager       file;
  private       FileConfiguration fileContent;

  public Config(@NotNull final Plugin plugin) {
    this.plugin = plugin;

    file = new FileManager(plugin, "config.yml");
    file.saveDefaults();
    fileContent = file.getContent();
  }

  public void reload() {
    file.reloadContent();
    fileContent = file.getContent();
  }

  public int getWildernessChunks() {
    return fileContent.getInt("city.wilderness-between-cities");
  }

  public int getCityCreationFee() {
    return fileContent.getInt("city.creation-fee");
  }

}
