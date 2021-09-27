package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;


@CommandAlias("city|ville")
public class OriginEditCommand extends BaseCommand {

  @Subcommand("edit origin")
  public void editOrigin(Player player, String mode) {
    throw new UnsupportedOperationException();

    // TODO check if the player is in a city
    // TODO check if the player has the rights to edit the origin

    // TODO check if the origin point is legal

    // TODO edit the origin point
  }

}
