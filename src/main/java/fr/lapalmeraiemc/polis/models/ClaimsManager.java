package fr.lapalmeraiemc.polis.models;

import com.google.gson.Gson;
import fr.lapalmeraiemc.polis.utils.AutoSaveable;
import fr.lapalmeraiemc.polis.utils.Config;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class ClaimsManager implements AutoSaveable {

  @Getter(AccessLevel.PACKAGE)
  @Setter(AccessLevel.PRIVATE)
  private static transient ClaimsManager instance;

  private final transient Gson   gson;
  private final transient Config config;
  private final transient Plugin plugin;
  private final transient File   saveFile;

  public ClaimsManager(@NotNull final Gson gson, @NotNull final Config config, @NotNull final Plugin plugin) {
    setInstance(this);

    this.gson = gson;
    this.config = config;
    this.plugin = plugin;
    this.saveFile = new File(plugin.getDataFolder(), "claims.json");

    load();
  }

  private final Map<UUID, Map<Long, ChunkData>> claims  = new ConcurrentHashMap<>();
  private final Map<UUID, Map<Long, ChunkData>> origins = new ConcurrentHashMap<>();

  public void load() {
    // TODO add a persistence adapter
    if (saveFile.exists()) {
      try (final FileReader reader = new FileReader(saveFile, StandardCharsets.UTF_8)) {
        final ClaimsManager data = gson.fromJson(reader, ClaimsManager.class);
        if (data != null) {
          claims.clear();
          claims.putAll(data.claims);
          origins.clear();
          origins.putAll(data.origins);
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

  public double getDistanceSquaredToNearestOrigin(@NotNull final Chunk chunk) {
    return getDistanceSquaredToNearestOrigin(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
  }

  public double getDistanceSquaredToNearestOrigin(@NotNull final UUID worldUuid, final int chunkX, final int chunkZ) {
    return origins.entrySet()
                  .parallelStream()
                  .filter(entry -> worldUuid.equals(entry.getKey()))
                  .flatMap(entry -> entry.getValue().values().parallelStream())
                  .map(chunk -> chunk.distanceSquared(chunkX, chunkZ))
                  .min(Comparator.naturalOrder())
                  .orElse(Double.POSITIVE_INFINITY);
  }

  public double getDistanceToNearestOrigin(@NotNull final Chunk chunk) {
    return getDistanceToNearestOrigin(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
  }

  public double getDistanceToNearestOrigin(@NotNull final UUID worldUuid, final int chunkX, final int chunkZ) {
    return Math.sqrt(getDistanceSquaredToNearestOrigin(worldUuid, chunkX, chunkZ));
  }

  public void setOrigin(final long cityId, @NotNull final Chunk chunk) {
    setOrigin(cityId, chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
  }

  public void setOrigin(final long cityId, @NotNull final UUID worldUuid, final int chunkX, final int chunkZ) {
    origins.computeIfAbsent(worldUuid, key -> new HashMap<>())
           .put(cityId, new ChunkData(cityId, Chunk.getChunkKey(chunkX, chunkZ), worldUuid, chunkX, chunkZ));
  }

  public void claim(final long cityId, @NotNull final Chunk chunk) {
    claim(cityId, chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
  }

  public void claim(final long cityId, @NotNull final UUID worldUuid, final int chunkX, final int chunkZ) {
    final ChunkData origin = origins.computeIfAbsent(worldUuid, key -> new HashMap<>()).get(cityId);
    final long chunkKey = Chunk.getChunkKey(chunkX, chunkZ);
    if (origin != null && origin.distanceSquared(chunkX, chunkZ) > config.getMaxClaimDistanceSquared()) return;
    claims.computeIfAbsent(worldUuid, key -> new HashMap<>())
          .put(chunkKey, new ChunkData(cityId, chunkKey, worldUuid, chunkX, chunkZ));
  }

  public void claim(final long cityId, @NotNull final Collection<Chunk> chunks) {
    claim(cityId, chunks.stream().map(chunk -> chunk.getWorld().getUID()).toArray(UUID[]::new),
          chunks.stream().mapToInt(Chunk::getX).toArray(), chunks.stream().mapToInt(Chunk::getZ).toArray());
  }

  public void claim(final long cityId, @NotNull final Chunk[] chunks) {
    if (chunks.length == 0) return;
    claim(cityId, Arrays.stream(chunks).map(chunk -> chunk.getWorld().getUID()).toArray(UUID[]::new),
          Arrays.stream(chunks).mapToInt(Chunk::getX).toArray(), Arrays.stream(chunks).mapToInt(Chunk::getZ).toArray());
  }

  public void claim(final long cityId, @NotNull final UUID[] worldUuids, final int @NotNull [] chunksX,
                    final int @NotNull [] chunksZ) {
    if (worldUuids.length != chunksX.length || chunksX.length != chunksZ.length)
      throw new RuntimeException("worldUuids, chunksX and chunksZ are not the same length");
    for (int i = 0; i < chunksX.length; i++) claim(cityId, worldUuids[i], chunksX[i], chunksZ[i]);
  }

  public void unclaim(final long cityId, @NotNull final Chunk chunk) {
    unclaim(cityId, chunk.getChunkKey(), chunk.getWorld().getUID());
  }

  public void unclaim(final long cityId, @NotNull final UUID worldUuid, final int chunkX, final int chunkZ) {
    unclaim(cityId, Chunk.getChunkKey(chunkX, chunkZ), worldUuid);
  }

  public void unclaim(final long cityId, final long chunkKey, @NotNull final UUID worldUuid) {
    final ChunkData chunk = claims.computeIfAbsent(worldUuid, key -> new HashMap<>()).get(chunkKey);
    if (chunk == null || chunk.getCityId() != cityId) return;
    final ChunkData origin = origins.computeIfAbsent(worldUuid, key -> new HashMap<>()).get(cityId);
    if (origin != null && origin.getKey() == chunkKey) return;
    claims.get(worldUuid).remove(chunkKey);
  }

  public void unclaim(final long cityId, @NotNull final Collection<Chunk> chunks) {
    unclaim(cityId, chunks.stream().mapToLong(Chunk::getChunkKey).toArray(),
            chunks.stream().map(chunk -> chunk.getWorld().getUID()).toArray(UUID[]::new));
  }

  public void unclaim(final long cityId, @NotNull final Chunk[] chunks) {
    unclaim(cityId, Arrays.stream(chunks).mapToLong(Chunk::getChunkKey).toArray(),
            Arrays.stream(chunks).map(chunk -> chunk.getWorld().getUID()).toArray(UUID[]::new));
  }

  public void unclaim(final long cityId, @NotNull final UUID[] worldUuids, final int @NotNull [] chunksX,
                      final int @NotNull [] chunksZ) {
    if (worldUuids.length != chunksX.length || chunksX.length != chunksZ.length)
      throw new RuntimeException("worldUuids, chunksX and chunksZ are not the same length");
    for (int i = 0; i < chunksX.length; i++) unclaim(cityId, Chunk.getChunkKey(chunksX[i], chunksZ[i]), worldUuids[i]);
  }

  public void unclaim(final long cityId, final long @NotNull [] chunkKeys, @NotNull final UUID[] worldUuids) {
    if (chunkKeys.length != worldUuids.length)
      throw new RuntimeException("chunkKeys and worldUuids are not the same length");
    for (int i = 0; i < chunkKeys.length; i++) unclaim(cityId, chunkKeys[i], worldUuids[i]);
  }

  public long getClaimCount(final long cityId) {
    return claims.values()
                 .parallelStream()
                 .flatMap(map -> map.values().parallelStream())
                 .map(ChunkData::getCityId)
                 .filter(chunkCityId -> chunkCityId == cityId)
                 .count();
  }

  public double getClaimPrice(final long claimCount) {
    final long paidClaimCount = Math.max(claimCount - Math.max(config.getFreeStartingClaimsAmount(), 0), 0);
    final long threshold = (long) Math.floor(
        (double) paidClaimCount / Math.max(config.getClaimPriceThresholdSize(), 1));
    final double rawPrice = threshold * config.getClaimBasePrice() * config.getClaimPriceMultiplicator();
    return config.isClaimPriceRounded() ? Math.round(rawPrice) : rawPrice;
  }

  public double getNextClaimPrice(final long cityId) {
    return getClaimPrice(getClaimCount(cityId) + 1);
  }

  public double getNextClaimsPrice(final long cityId, final int amount) {
    final long claimCount = getClaimCount(cityId);
    double total = 0;
    for (int i = 1; i <= amount; i++) {
      total += getClaimPrice(claimCount + i);
    }
    return total;
  }

}
