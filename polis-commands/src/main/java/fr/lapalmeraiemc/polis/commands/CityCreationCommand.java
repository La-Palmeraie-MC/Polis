package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import fr.lapalmeraiemc.polis.models.ChunkLocation;
import fr.lapalmeraiemc.polis.models.City;
import fr.lapalmeraiemc.polis.models.Member;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;


@CommandAlias("city|ville")
public class CityCreationCommand extends PolisBaseCommand {

  public CityCreationCommand(Plugin plugin, Logger logger, Config config, Localizer localizer, Economy economy) {
    super(plugin, logger, config, localizer, economy);
  }

  @Subcommand("create")
  public void create(Player player, String name, String tag) {
    // player.sendRawMessage("" + getEconomy().getBalance(player));

    // checks if a player has the amount of money needed...
    // if(getEconomy().getBalance(player) >= getConfig().getCityCreationFee()){
    //  player.sendRawMessage("grossepute t'as pas la moula");
    //  return;
    // }
    // ...and removes it if true
    // getEconomy().withdrawPlayer(player, getConfig().getCityCreationFee());

    // Args Order 🔻
    // City Name > City Tag > City Origin > Owner
    City cityCreated = new City();
    cityCreated.setName(name);
    cityCreated.setTag(tag);
    cityCreated.setChunkKeyOrigin(player.getChunk().getChunkKey());

    // Setting the command Issuer as the City Owner & add in the member list
    Member owner = new Member();
    owner.setUuid(player.getUniqueId());
    cityCreated.setOwner(owner);

    cityCreated.getMemberList().put(player.getUniqueId(), owner);

    // Setting the origin chunk as the 1st claimed chunk
    cityCreated.getChunksClaimed().put(player.getChunk().getChunkKey(),
                                       new ChunkLocation(player.getChunk().getX(), player.getChunk().getZ()));

    // finished

    player.sendRawMessage("Ta ville " + cityCreated.getName() + " a été créée. GG.");

    // TODO send the discord message to notify moderators
  }

}
