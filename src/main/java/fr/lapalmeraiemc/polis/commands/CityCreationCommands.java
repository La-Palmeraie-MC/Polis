package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import fr.lapalmeraiemc.polis.enums.Messages;
import fr.lapalmeraiemc.polis.models.CityManager;
import fr.lapalmeraiemc.polis.models.MemberManager;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import net.kyori.adventure.identity.Identity;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.logging.Logger;


@CommandAlias("city|ville")
public class CityCreationCommands extends BaseCommand {

  @Inject private Config        config;
  @Inject private Localizer     localizer;
  @Inject private Economy       economy;
  @Inject private Logger        logger;
  @Inject private CityManager   cityManager;
  @Inject private MemberManager memberManager;

  @Subcommand("create")
  @Syntax("<nom> <tag>")
  @CommandCompletion("@nothing @nothing")
  public void create(Player player, @Flags("quoted") String name, @Single String tag) {
    if (memberManager.isCityMember(player.getUniqueId())) {
      player.sendMessage(Identity.nil(), localizer.getColorizedMessage(Messages.CITY_CREATION_ALREADY_MEMBER));
      return;
    }

    if (!economy.has(player, config.getCityCreationFee())) {
      player.sendMessage(Identity.nil(), localizer.getColorizedMessage(Messages.CITY_CREATION_FEE,
                                                                       Integer.toString(config.getCityCreationFee())));
      return;
    }

    if (cityManager.isNameUsed(name)) {
      player.sendMessage(Identity.nil(), localizer.getColorizedMessage(Messages.CITY_CREATION_EXISTING_NAME, name));
      return;
    }

    if (cityManager.isTagUsed(tag)) {
      player.sendMessage(Identity.nil(), localizer.getColorizedMessage(Messages.CITY_CREATION_EXISTING_TAG, tag));
      return;
    }

    Confirmation.prompt(player, localizer.getColorizedMessage(Messages.CITY_CREATION_FEE_PROMPT,
                                                              Integer.toString(config.getCityCreationFee())), () -> {
      economy.withdrawPlayer(player, config.getCityCreationFee());

      cityManager.create(name, tag, player);

      player.sendMessage(Identity.nil(), localizer.getColorizedMessage(Messages.CITY_CREATED, name, tag,
                                                                       Integer.toString(
                                                                           config.getMinCityMembers() - 1)));
    });
  }

}
