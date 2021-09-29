package fr.lapalmeraiemc.polis.models;

import fr.lapalmeraiemc.polis.enums.Roles;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


@Getter
public class Member {

  private UUID uuid;
  private long cityId;

  @Setter private Roles role;

  Member(@NotNull final UUID uuid, final long cityId) {
    this.uuid = uuid;
    this.cityId = cityId;
  }

}
