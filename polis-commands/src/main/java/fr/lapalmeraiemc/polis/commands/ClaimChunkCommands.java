package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import fr.lapalmeraiemc.polis.models.City;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;


@CommandAlias("city|ville")
public class ClaimChunkCommands extends PolisBaseCommand {

  public ClaimChunkCommands(Plugin plugin, Logger logger, Config config, Localizer localizer, Economy economy) { super(plugin, logger, config, localizer, economy); }

  @Subcommand("claim")
  public void create(Player player, String mode){

  }

}
