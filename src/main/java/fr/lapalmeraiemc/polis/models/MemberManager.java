package fr.lapalmeraiemc.polis.models;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.lapalmeraiemc.polis.Polis;
import fr.lapalmeraiemc.polis.utils.AutoSaveable;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RequiredArgsConstructor
public class MemberManager implements AutoSaveable {

  private final transient Gson gson;
  private final transient File saveFile;
  private final transient Type gsonType = new TypeToken<Map<UUID, Map<Long, Member>>>() {}.getType();

  private final Map<UUID, CacheValue> cache = new ConcurrentHashMap<>();

  public void load() {
    final Set<String> membersIdToLoad = Bukkit.getOnlinePlayers()
                                              .stream()
                                              .map(Player::getUniqueId)
                                              .map(UUID::toString)
                                              .collect(Collectors.toSet());

    // TODO add a persistence adapter
    if (saveFile.exists()) {
      try (final FileReader reader = new FileReader(saveFile, StandardCharsets.UTF_8)) {
        final JsonElement jsonTree = gson.toJsonTree(gson.fromJson(reader, gsonType));
        final JsonObject jsonObject = jsonTree.isJsonObject() ? jsonTree.getAsJsonObject() : new JsonObject();

        final JsonObject jsonMembersToLoad = new JsonObject();
        jsonObject.entrySet()
                  .stream()
                  .filter(entry -> membersIdToLoad.contains(entry.getKey()))
                  .forEach(entry -> jsonMembersToLoad.add(entry.getKey(), entry.getValue()));

        final Map<UUID, Map<Long, Member>> membersToLoad = gson.fromJson(jsonMembersToLoad, gsonType);
        cache.putAll(membersToLoad.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> {
          return new CacheValue(entry.getValue(), Bukkit.getOfflinePlayer(entry.getKey()));
        })));
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void load(@NotNull final UUID uuid) {
    if (cache.containsKey(uuid)) return;

    // TODO add a persistence adapter
    if (saveFile.exists()) {
      Bukkit.getScheduler().runTaskAsynchronously(Polis.getInstance(), () -> {
        try (final FileReader reader = new FileReader(saveFile, StandardCharsets.UTF_8)) {
          final JsonElement jsonTree = gson.toJsonTree(gson.fromJson(reader, gsonType));
          final JsonObject jsonObject = jsonTree.isJsonObject() ? jsonTree.getAsJsonObject() : new JsonObject();

          final String uuidToString = uuid.toString();
          final JsonObject jsonMembersToLoad = new JsonObject();
          jsonObject.entrySet()
                    .stream()
                    .filter(entry -> uuidToString.equals(entry.getKey()))
                    .forEach(entry -> jsonMembersToLoad.add(entry.getKey(), entry.getValue()));

          final Map<UUID, Map<Long, Member>> membersToLoad = gson.fromJson(jsonMembersToLoad, gsonType);
          cache.putAll(membersToLoad.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> {
            return new CacheValue(entry.getValue(), Bukkit.getOfflinePlayer(entry.getKey()));
          })));
        }
        catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  @Override
  public void save(final boolean force) {
    final Map<UUID, Map<Long, Member>> membersToSave = cache.entrySet()
                                                            .stream()
                                                            .collect(Collectors.toMap(Entry::getKey, entry -> {
                                                              return entry.getValue().getMembershipMap();
                                                            }));

    final long currentTime = System.currentTimeMillis();
    cache.entrySet()
         .removeIf(entry -> TimeUnit.MILLISECONDS.toSeconds(currentTime - entry.getValue().getPlayer().getLastSeen()) >= 600);

    if (force) {
      save(membersToSave);
    }
    else {
      Bukkit.getScheduler().runTaskAsynchronously(Polis.getInstance(), () -> save(membersToSave));
    }
  }

  private void save(@NotNull final Map<UUID, Map<Long, Member>> membersToSave) {
    // TODO add a persistence adapter
    final Set<Entry<String, JsonElement>> previousValues;
    if (saveFile.exists()) {
      try (final FileReader reader = new FileReader(saveFile, StandardCharsets.UTF_8)) {
        final JsonElement jsonTree = gson.toJsonTree(gson.fromJson(reader, gsonType));
        previousValues = jsonTree.isJsonObject() ? jsonTree.getAsJsonObject().entrySet() : new HashSet<>();
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    else {
      previousValues = new HashSet<>();
    }

    final JsonElement jsonTree = gson.toJsonTree(membersToSave);
    final Set<Entry<String, JsonElement>> newValues =
        jsonTree.isJsonObject() ? jsonTree.getAsJsonObject().entrySet() : new HashSet<>();

    final JsonObject jsonObject = new JsonObject();

    Stream.concat(previousValues.stream(), newValues.stream()).forEachOrdered(entry -> {
      jsonObject.remove(entry.getKey());
      jsonObject.add(entry.getKey(), entry.getValue());
    });

    try (final FileWriter writer = new FileWriter(saveFile, StandardCharsets.UTF_8)) {
      writer.write(gson.toJson(jsonObject));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Member create(@NotNull final UUID uuid, final long cityId) {
    final Member newMember = new Member(uuid, cityId);

    if (cache.computeIfAbsent(uuid, key -> new CacheValue(new HashMap<>(), Bukkit.getOfflinePlayer(uuid)))
             .getMembershipMap()
             .putIfAbsent(cityId, newMember) != null)
      throw new IllegalArgumentException(uuid + " is already a member of city " + cityId);

    return newMember;
  }

  @Value
  private static class CacheValue {

    Map<Long, Member> membershipMap;
    OfflinePlayer     player;

  }

}
