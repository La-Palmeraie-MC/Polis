package fr.lapalmeraiemc.polis.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;


public class FileManager {

  private final Plugin            plugin;
  private final String            resourceName;
  private final File              file;
  private       FileConfiguration fileContent;

  public FileManager(@NotNull final Plugin plugin, @NotNull final String filename) {
    this(plugin, filename, filename);
  }

  public FileManager(@NotNull final Plugin plugin, @NotNull final String resourceName, @NotNull final String saveName) {
    this.plugin = plugin;
    this.resourceName = resourceName;
    this.file = new File(plugin.getDataFolder(), saveName);
  }

  public void reloadContent() {
    fileContent = YamlConfiguration.loadConfiguration(file);

    final InputStream defaultFile = plugin.getResource(resourceName);
    if (defaultFile != null) {
      final YamlConfiguration defaultContent = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultFile));
      fileContent.setDefaults(defaultContent);
    }
  }

  public FileConfiguration getContent() {
    if (fileContent == null) reloadContent();
    return fileContent;
  }

  public @Nullable FileConfiguration getDefaults() {
    final InputStream defaultFile = plugin.getResource(resourceName);
    return defaultFile == null ? null : YamlConfiguration.loadConfiguration(new InputStreamReader(defaultFile));
  }

  public void save() {
    if (fileContent != null) {
      try {
        getContent().save(file);
      }
      catch (IOException e) {
        plugin.getLogger().log(Level.SEVERE, String.format("Could not save '%s'", resourceName), e);
      }
    }
  }

  public void saveDefaults() {
    if (!file.exists()) {
      try {
        final FileConfiguration defaults = getDefaults();
        if (defaults != null) defaults.save(file);
      }
      catch (IOException e) {
        plugin.getLogger().log(Level.SEVERE, String.format("Could not save '%s'", resourceName), e);
      }
    }
    else {
      getContent().options().copyDefaults(true);
      save();
    }
  }

  public void clear() {
    if (fileContent != null) {
      for (final String key : getContent().getKeys(false)) {
        getContent().set(key, null);
      }
    }
  }

}
