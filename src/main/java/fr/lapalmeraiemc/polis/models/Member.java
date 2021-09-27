package fr.lapalmeraiemc.polis.models;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseModel {

  public Member(@NotNull final UUID uuid) {
    this.uuid = uuid;
  }

  private UUID uuid;

}
