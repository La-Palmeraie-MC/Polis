package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;


@CommandAlias("city|ville")
public class ClaimChunkCommands extends BaseCommand {

  @Subcommand("claim")
  public void create(Player player, String mode) {
    throw new UnsupportedOperationException();
  }

}
