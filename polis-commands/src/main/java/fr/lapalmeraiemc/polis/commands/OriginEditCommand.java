package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;


@CommandAlias("city|ville")
public class OriginEditCommand extends PolisBaseCommand {

  public OriginEditCommand(Plugin plugin, Logger logger, Config config, Localizer localizer, Economy economy) { super(plugin, logger, config, localizer, economy); }

  @Subcommand("edit origin")
  public void editOrigin(Player player, String mode){

    // TODO check if the player is in a city
    // TODO check if the player has the rights to edit the origin

    // TODO check if the origin point is legal

    // TODO edit the origin point
  }

}
