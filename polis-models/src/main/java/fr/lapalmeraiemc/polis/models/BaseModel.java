package fr.lapalmeraiemc.polis.models;

import lombok.Getter;

import java.time.Instant;


public class BaseModel {

  @Getter
  private long id;

  @Getter
  private Instant whenCreated;

  @Getter
  private Instant whenUpdated;

  @Getter
  private boolean deleted;

}
