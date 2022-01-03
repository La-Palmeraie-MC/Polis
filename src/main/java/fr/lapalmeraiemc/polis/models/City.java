package fr.lapalmeraiemc.polis.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Getter
@ToString
@EqualsAndHashCode
public class City {

  private long id;

  @Setter private String name;
  @Setter private String tag;

  @Setter private UUID      owner;
  private final   Set<UUID> members = new HashSet<>();

  @Setter private Location spawn;

  City(final long id) {
    this.id = id;
  }

  public void addMember(@NotNull final UUID uuid) {
    members.add(uuid);
  }

  public void removeMember(@NotNull final UUID uuid) {
    members.remove(uuid);
  }

}
