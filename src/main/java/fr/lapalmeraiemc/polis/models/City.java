package fr.lapalmeraiemc.polis.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@ToString
@EqualsAndHashCode
public class City {

  private long id;

  @Setter private String name;
  @Setter private String tag;

  City(final long id) {
    this.id = id;
  }

}
