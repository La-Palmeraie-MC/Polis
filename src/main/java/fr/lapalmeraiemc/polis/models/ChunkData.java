package fr.lapalmeraiemc.polis.models;

import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


@Value
public class ChunkData {

  long cityId;
  long key;

  UUID worldUuid;
  int  x;
  int  z;

  public double distanceSquared(@NotNull final ChunkData chunk) {
    if (!worldUuid.equals(chunk.getWorldUuid())) return Double.POSITIVE_INFINITY;
    return distanceSquared(chunk.getX(), chunk.getZ());
  }

  public double distanceSquared(final int chunkX, final int chunkZ) {
    return Math.pow((double) chunkX - x, 2) + Math.pow((double) chunkZ - z, 2);
  }

  public double distance(@NotNull final ChunkData chunk) {
    if (!worldUuid.equals(chunk.getWorldUuid())) return Double.POSITIVE_INFINITY;
    return distance(chunk.getX(), chunk.getZ());
  }

  public double distance(final int chunkX, final int chunkZ) {
    return Math.sqrt(distanceSquared(chunkX, chunkZ));
  }

}
