package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Values;
import fr.lapalmeraiemc.polis.enums.Messages;
import fr.lapalmeraiemc.polis.enums.Roles;
import fr.lapalmeraiemc.polis.models.City;
import fr.lapalmeraiemc.polis.models.CityManager;
import fr.lapalmeraiemc.polis.models.ClaimsManager;
import fr.lapalmeraiemc.polis.models.Member;
import fr.lapalmeraiemc.polis.models.MemberManager;
import fr.lapalmeraiemc.polis.utils.Localizer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import fr.lapalmeraiemc.polis.utils.Config;

import javax.inject.Inject;
import java.util.Objects;


@CommandAlias("city|ville")
public class CityEditCommands extends BaseCommand {

  @Inject private Config        config;
  @Inject private MemberManager memberManager;
  @Inject private Localizer     localizer;
  @Inject private ClaimsManager claimsManager;
  @Inject private CityManager   cityManager;

  @Subcommand("edit origin")
  public void editOrigin(Player player) {

    Member member = memberManager.get(player.getUniqueId());
    Chunk chunkToUse = player.getChunk();

    // check if the commandIssuer is authorized to cast command
    if(member == null || member.getRole() != Roles.OWNER){
      localizer.sendMessage(player, Messages.NO_PERMISSION);
      return;
    }

    // check if the origin point is legal
    if(claimsManager.getDistanceSquaredToNearestOrigin(chunkToUse) < config.getMinDistanceBetweenCityOrigins()){
      localizer.sendMessage(player, Messages.CITY_EDIT_ORIGIN_TOO_CLOSE);
      return;
    }

    // check if the chunk is claimed
    if(!claimsManager.hasChunkBeenClaimed(chunkToUse)){
      localizer.sendMessage(player, Messages.CITY_EDIT_ORIGIN_NOT_CLAIMED);
      return;
    }

    // check if the chunk is claimed by another city
    if(claimsManager.getCityIdByChunkClaimed(chunkToUse) != member.getCityId()){
      localizer.sendMessage(player, Messages.CITY_EDIT_ORIGIN_ALREADY_CLAIMED);
      return;
    }
    
    // edit the origin point
    claimsManager.setOrigin(member.getCityId(), chunkToUse);
    localizer.sendMessage(player, Messages.CITY_EDIT_ORIGIN_SUCCESSFULLY_EDITED);
  }

  @Subcommand("edit spawn")
  @CommandCompletion("set|remove")
  public void editSpawn(Player player, @Values("set|remove") String mode){
    Member member = memberManager.get(player.getUniqueId());

    // check if the commandIssuer is authorized to cast command
    if(member == null || member.getRole() != Roles.OWNER){
      localizer.sendMessage(player, Messages.NO_PERMISSION);
      return;
    }

    if(mode.equals("set")){
      Location newSpawn = player.getLocation();
      City city = cityManager.getById(member.getCityId());
      city.setSpawn(newSpawn);
      localizer.sendMessage(player, Messages.CITY_EDIT_SPAWN_EDIT_SUCCESS);
    }
    if(mode.equals("remove")){
      City city = cityManager.getById(member.getCityId());
      city.setSpawn(null);
      localizer.sendMessage(player, Messages.CITY_EDIT_SPAWN_DELETE_SUCCESS);
    }
    else{
      localizer.sendMessage(player, Messages.CITY_EDIT_SPAWN_UNKNOWN_MODE);
    }
  }
}
