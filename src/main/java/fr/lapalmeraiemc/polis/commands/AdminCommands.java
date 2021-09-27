package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Subcommand;
import fr.lapalmeraiemc.polis.enums.Messages;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import net.kyori.adventure.identity.Identity;
import org.bukkit.command.CommandSender;


@CommandAlias("polis")
@CommandPermission("polis.admin")
public class AdminCommands extends BaseCommand {

  @Dependency private Config    config;
  @Dependency private Localizer localizer;

  @Subcommand("reload")
  public void reload(final CommandSender sender) {
    config.reload();
    localizer.reload();
    sender.sendMessage(Identity.nil(), localizer.getColorizedMessage(Messages.RELOAD_SUCCESS));
  }

}
