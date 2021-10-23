package fr.lapalmeraiemc.polis.utils;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;


public class Config {

  @Getter
  @Setter(AccessLevel.PRIVATE)
  private static Config instance;

  private final FileManager       configFile;
  private       FileConfiguration configContent;

  public Config(@NotNull final Plugin plugin) {
    setInstance(this);

    configFile = new FileManager(plugin, "config.yml");
    configFile.saveDefaults();
    configContent = configFile.getContent();
  }

  public void reload() {
    configFile.reloadContent();
    configFile.saveDefaults();
    configContent = configFile.getContent();
  }

  public int getWildernessBetweenCities() {
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

  public boolean isAutoSaveEnabled() {
    return configContent.getBoolean("auto-save.enabled");
  }

  public long getAutoSavePeriod() {
    return configContent.getLong("auto-save.period");
  }

  public int getMaxClaimDistance() {
    return configContent.getInt("city.max-claim-distance");
  }

  public int getMaxClaimDistanceSquared() {
    return (int) Math.round(Math.pow(getMaxClaimDistance(), 2));
  }

  public double getDistanceBetweenCityOrigins() {
    return (double) getWildernessBetweenCities() + getMaxClaimDistance();
  }

  public double getDistanceSquaredBetweenCityOrigins() {
    return Math.pow(getDistanceBetweenCityOrigins(), 2);
  }

  public int getFreeStartingClaimsAmount() {
    return configContent.getInt("city.claim-price.free-starting-claims");
  }

  public double getClaimBasePrice() {
    return configContent.getDouble("city.claim-price.base-price");
  }

  public int getClaimPriceThresholdSize() {
    return configContent.getInt("city.claim-price.threshold-size");
  }

  public double getClaimPriceMultiplicator() {
    return configContent.getDouble("city.claim-price.multiplicator");
  }

  public boolean isClaimPriceRounded() {
    return configContent.getBoolean("city.claim-price.rounded");
  }

}
