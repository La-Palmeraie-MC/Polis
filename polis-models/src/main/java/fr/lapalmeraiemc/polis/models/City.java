package fr.lapalmeraiemc.polis.models;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class City extends BaseModel {

  public City(@NotNull final String name, @NotNull final String tag) {
    this.name = name;
    this.tag = tag;
  }

  private String name;
  private String tag;

  private       Member            owner;
  private final Map<UUID, Member> memberList = new HashMap<>();

  private       long                     originKey;
  private final Map<Long, ChunkLocation> claimedChunks = new HashMap<>();

}
