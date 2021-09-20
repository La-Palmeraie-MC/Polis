package fr.lapalmeraiemc.polis.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;



@ToString
public class City extends BaseModel {

  public City(@NotNull final String name, @NotNull final String tag) {
    this.name = name;
    this.tag = tag;
  }

  @Getter
  @Setter
  private String name;

  @Getter
  @Setter
  private String tag;

}
