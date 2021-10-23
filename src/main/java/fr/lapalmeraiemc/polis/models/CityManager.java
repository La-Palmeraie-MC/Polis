package fr.lapalmeraiemc.polis.models;

import com.google.gson.Gson;
import fr.lapalmeraiemc.polis.enums.Roles;
import fr.lapalmeraiemc.polis.utils.AutoSaveable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CityManager implements AutoSaveable {

  @Getter(AccessLevel.PACKAGE)
  @Setter(AccessLevel.PRIVATE)
  private static transient CityManager instance;

  private final transient Gson          gson;
  private final transient ClaimsManager claimsManager;
  private final transient Plugin        plugin;
  private final transient File          saveFile;

  public CityManager(@NotNull final Gson gson, @NotNull final Plugin plugin) {
    setInstance(this);

    this.gson = gson;
    this.claimsManager = ClaimsManager.getInstance();
    this.plugin = plugin;
    this.saveFile = new File(plugin.getDataFolder(), "cities.json");

    load();
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

  public City create(@NotNull final String name, @NotNull final String tag, @NotNull final Player owner) {
    final City city = new City(getNextId());

    city.setName(name);
    city.setTag(tag);
    city.setOwner(owner.getUniqueId());
    city.addMember(owner.getUniqueId());

    final Member member = MemberManager.getInstance().create(owner.getUniqueId(), city.getId());
    member.setRole(Roles.OWNER);

    claimsManager.claim(city.getId(), owner.getChunk());
    claimsManager.setOrigin(city.getId(), owner.getChunk());

    cities.put(city.getId(), city);
    return city;
  }

  public boolean isNameUsed(@NotNull final String name) {
    return cities.values().stream().map(City::getName).anyMatch(cityName -> cityName.equalsIgnoreCase(name));
  }

  public boolean isTagUsed(@NotNull final String tag) {
    return cities.values().stream().map(City::getTag).anyMatch(cityTag -> cityTag.equalsIgnoreCase(tag));
  }

  public City getById(final long id) {
    return cities.get(id);
  }

}
