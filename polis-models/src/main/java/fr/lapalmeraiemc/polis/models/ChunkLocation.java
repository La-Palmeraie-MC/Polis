package fr.lapalmeraiemc.polis.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class ChunkLocation {

  public ChunkLocation(int x, int z) {
    this.x = x;
    this.z = z;
  }

  private int x;
  private int z;

}
