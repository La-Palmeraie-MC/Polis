package fr.lapalmeraiemc.polis.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class City extends BaseModel {
  private String name;
  private String tag;
  private Long                           chunkKeyOrigin;
  private final Map<Long, ChunkLocation> chunksClaimed = new HashMap<>();
  private final Map<UUID, Member>        memberList    = new HashMap<>();
  private Member owner;
}
