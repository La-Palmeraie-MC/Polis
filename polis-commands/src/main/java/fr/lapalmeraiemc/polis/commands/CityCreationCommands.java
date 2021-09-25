package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import fr.lapalmeraiemc.polis.enums.Messages;
import fr.lapalmeraiemc.polis.models.ChunkLocation;
import fr.lapalmeraiemc.polis.models.City;
import fr.lapalmeraiemc.polis.models.Member;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import net.kyori.adventure.identity.Identity;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.util.logging.Logger;


@CommandAlias("city|ville")
public class CityCreationCommands extends BaseCommand {

  @Dependency private Config    config;
  @Dependency private Localizer localizer;
  @Dependency private Economy   economy;
  @Dependency private Logger    logger;

  @Subcommand("create")
  @Syntax("<nom> <tag>")
  @CommandCompletion("@nothing @nothing")
  public void create(Player player, @Flags("quoted") String name, @Single String tag) {

    if (!economy.has(player, config.getCityCreationFee())) {
      player.sendMessage(Identity.nil(), localizer.getColorizedMessage(Messages.CITY_CREATION_FEE,
                                                                       Integer.toString(config.getCityCreationFee())));
      return;
    }

    Confirmation.prompt(player, localizer.getColorizedMessage(Messages.CITY_CREATION_FEE_PROMPT,
                                                              Integer.toString(config.getCityCreationFee())), () -> {
      economy.withdrawPlayer(player, config.getCityCreationFee());

      // Args Order 🔻
      // City Name > City Tag > City Origin > Owner
      final City city = new City(name, tag);
      city.setOriginKey(player.getChunk().getChunkKey());

      // Setting the command Issuer as the City Owner & add in the member list
      final Member owner = new Member(player.getUniqueId());
      city.setOwner(owner);
      city.getMemberList().put(player.getUniqueId(), owner);

      // Setting the origin chunk as the 1st claimed chunk
      city.getClaimedChunks()
          .put(player.getChunk().getChunkKey(), new ChunkLocation(player.getChunk().getX(), player.getChunk().getZ()));

      player.sendMessage(Identity.nil(), localizer.getColorizedMessage(Messages.CITY_CREATED, city.getName(), city.getTag(),
                                                                       Integer.toString(config.getMinCityMembers() - 1)));
    });
  }

}
