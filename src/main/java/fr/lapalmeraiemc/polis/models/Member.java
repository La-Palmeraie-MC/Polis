package fr.lapalmeraiemc.polis.models;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
public class Member {

  private UUID uuid;
  private long cityId;

  Member(@NotNull final UUID uuid, final long cityId) {
    this.uuid = uuid;
    this.cityId = cityId;
  }

}
