package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import fr.lapalmeraiemc.polis.enums.Messages;
import fr.lapalmeraiemc.polis.models.City;
import fr.lapalmeraiemc.polis.models.CityManager;
import fr.lapalmeraiemc.polis.models.ClaimsManager;
import fr.lapalmeraiemc.polis.models.MemberManager;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import javax.inject.Inject;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@CommandAlias("city|ville")
public class CityCreationCommands extends BaseCommand {

  private static final Pattern INVALID_CITY_NAME_PATTERN = Pattern.compile(
      "[^A-Za-z0-9àèìòùÀÈÌÒÙáéíóúýÁÉÍÓÚÝâêîôûÂÊÎÔÛãñõÃÑÕäëïöüÿÄËÏÖÜŸçÇßØøÅåÆæœ' -]");
  private static final Pattern INVALID_CITY_TAG_PATTERN  = Pattern.compile("[^A-Za-z0-9]");

  @Inject private Config        config;
  @Inject private Localizer     localizer;
  @Inject private Economy       economy;
  @Inject private Logger        logger;
  @Inject private CityManager   cityManager;
  @Inject private MemberManager memberManager;
  @Inject private ClaimsManager claimsManager;

  @Subcommand("create")
  @Syntax("<nom> <tag>")
  @CommandCompletion("@nothing @nothing")
  public void create(Player player, @Flags("quoted") String name, @Single String tag) {
    if (memberManager.isAlreadyCityMember(player.getUniqueId())) {
      localizer.sendMessage(player, Messages.CITY_CREATION_ALREADY_MEMBER);
      return;
    }

    if (!economy.has(player, config.getCityCreationFee())) {
      localizer.sendMessage(player, Messages.CITY_CREATION_FEE, config.getCityCreationFee());
      return;
    }

    if (!isNameValid(player, name) || !isTagValid(player, tag)) return;

    final double distance = claimsManager.getDistanceToNearestOrigin(player.getChunk());
    final City nearestCity = claimsManager.getNearestCity(player.getChunk());
    if (distance < config.getMaxClaimDistance() + config.getWildernessBetweenCities() && nearestCity != null) {
      localizer.sendMessage(player, Messages.CITY_CREATION_TOO_CLOSE, nearestCity.getName(), nearestCity.getTag());
      return;
    }

    Confirmation.prompt(player,
                        localizer.getColorizedMessage(Messages.CITY_CREATION_FEE_PROMPT, config.getCityCreationFee()),
                        () -> {
                        if (!economy.has(player, config.getCityCreationFee())) {
                          localizer.sendMessage(player, Messages.CITY_CREATION_FEE, config.getCityCreationFee());
                          return;
                        }
                        onConfirm(player, name, tag);
                        },
                        () -> localizer.sendMessage(player, Messages.CITY_CREATION_CANCEL, name, tag));
  }

  private void onConfirm(Player player, String name, String tag) {
    economy.withdrawPlayer(player, config.getCityCreationFee());
    cityManager.create(name, tag, player);
    localizer.sendMessage(player, Messages.CITY_CREATION_CONFIRM, name, tag, config.getMinCityMembers() - 1);
  }

  private boolean isTagValid(Player player, String tag) {
    if (cityManager.isTagUsed(tag)) {
      localizer.sendMessage(player, Messages.CITY_CREATION_EXISTING_TAG, tag);
      return false;
    }

    if (tag.length() != 3) {
      localizer.sendMessage(player, Messages.CITY_CREATION_TAG_SIZE);
      return false;
    }

    final Matcher matcher = INVALID_CITY_TAG_PATTERN.matcher(tag);
    if (matcher.find()) {
      localizer.sendMessage(player, Messages.CITY_CREATION_INVALID_CHARS, matcher.replaceAll("&n$0&c"));
      return false;
    }

    return true;
  }

  private boolean isNameValid(Player player, String name) {
    if (cityManager.isNameUsed(name)) {
      localizer.sendMessage(player, Messages.CITY_CREATION_EXISTING_NAME, name);
      return false;
    }

    if (name.length() > 24) {
      localizer.sendMessage(player, Messages.CITY_CREATION_LONG_NAME);
      return false;
    }

    if (name.length() < 3) {
      localizer.sendMessage(player, Messages.CITY_CREATION_SHORT_NAME);
      return false;
    }

    final Matcher matcher = INVALID_CITY_NAME_PATTERN.matcher(name);
    if (matcher.find()) {
      localizer.sendMessage(player, Messages.CITY_CREATION_INVALID_CHARS, matcher.replaceAll("&n$0&c"));
      return false;
    }

    return true;
  }

}
