package fr.lapalmeraiemc.polis.models;

import com.google.gson.Gson;
import fr.lapalmeraiemc.polis.utils.AutoSaveable;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@RequiredArgsConstructor
public class CityManager implements AutoSaveable {

  private final transient Gson   gson;
  private final transient Plugin plugin;
  private final transient File   saveFile;

  public CityManager(@NotNull final Gson gson, @NotNull final Plugin plugin) {
    this.gson = gson;
    this.plugin = plugin;
    this.saveFile = new File(plugin.getDataFolder(), "cities.json");
  }

  private       long            nextId = 0;
  private final Map<Long, City> cities = new ConcurrentHashMap<>();

  public void load() {
    // TODO add a persistence adapter
    if (saveFile.exists()) {
      try (final FileReader reader = new FileReader(saveFile, StandardCharsets.UTF_8)) {
        final CityManager data = gson.fromJson(reader, CityManager.class);
        if (data != null) {
          nextId = data.nextId;
          cities.clear();
          cities.putAll(data.cities);
        }
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void save(final boolean force) {
    if (force) {
      save();
    }
    else {
      Bukkit.getScheduler().runTaskAsynchronously(plugin, (Runnable) this::save);
    }
  }

  private void save() {
    // TODO add a persistence adapter
    try (final FileWriter writer = new FileWriter(saveFile, StandardCharsets.UTF_8)) {
      writer.write(gson.toJson(this));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public long getNextId() {
    final long currentId = nextId;
    nextId++;
    return currentId;
  }

  public City create(@NotNull final String name, @NotNull final String tag) {
    final City city = new City(getNextId());
    city.setName(name);
    city.setTag(tag);
    cities.put(city.getId(), city);
    return city;
  }

}
