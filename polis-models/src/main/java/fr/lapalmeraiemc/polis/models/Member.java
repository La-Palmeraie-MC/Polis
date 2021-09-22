package fr.lapalmeraiemc.polis.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;


@NoArgsConstructor
@ToString
public class Member extends BaseModel{

  @Getter
  @Setter
  private UUID uuid;
}
