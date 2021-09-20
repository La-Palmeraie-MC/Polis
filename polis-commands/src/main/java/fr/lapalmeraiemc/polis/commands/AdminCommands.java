package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import fr.lapalmeraiemc.polis.enums.Messages;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import net.kyori.adventure.identity.Identity;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;


@CommandAlias("polis")
@CommandPermission("polis.admin")
public class AdminCommands extends PolisBaseCommand {

  public AdminCommands(Plugin plugin, Logger logger, Config config, Localizer localizer) {
    super(plugin, logger, config, localizer);
  }

  @Subcommand("reload")
  public void reload(final CommandSender sender) {
    getConfig().reload();
    getLocalizer().reload();
    sender.sendMessage(Identity.nil(), getLocalizer().getColorizedMessage(Messages.CMD_RELOAD_SUCCESS));
  }

}
