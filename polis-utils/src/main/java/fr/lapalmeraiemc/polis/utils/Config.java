package fr.lapalmeraiemc.polis.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;


public class Config {

  private final FileManager       configFile;
  private       FileConfiguration configContent;

  public Config(@NotNull final Plugin plugin) {
    configFile = new FileManager(plugin, "config.yml");
    configFile.saveDefaults();
    configContent = configFile.getContent();
  }

  public void reload() {
    configFile.saveDefaults();
    configFile.reloadContent();
    configContent = configFile.getContent();
  }

  public int getWildernessChunks() {
    return configContent.getInt("city.wilderness-between-cities");
  }

  public int getCityCreationFee() {
    return configContent.getInt("city.creation-fee");
  }

  public int getMinCityMembers() {
    return configContent.getInt("city.min-members");
  }

  public int getConfirmationTimeout() {
    return configContent.getInt("confirmation-timeout");
  }

}
