package fr.lapalmeraiemc.polis.models;

import lombok.Getter;

import java.time.Instant;


@Getter
public class BaseModel {

  private long    id;
  private Instant whenCreated;
  private Instant whenUpdated;
  private boolean deleted;

}
