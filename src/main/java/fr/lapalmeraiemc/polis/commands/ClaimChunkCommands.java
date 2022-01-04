package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Values;
import fr.lapalmeraiemc.polis.enums.Messages;
import fr.lapalmeraiemc.polis.enums.Roles;
import fr.lapalmeraiemc.polis.models.ClaimsManager;
import fr.lapalmeraiemc.polis.models.Member;
import fr.lapalmeraiemc.polis.models.MemberManager;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import javax.inject.Inject;

@Subcommand("claim")
@CommandAlias("city|ville")
public class ClaimChunkCommands extends BaseCommand {

  @Inject private Config        config;
  @Inject private MemberManager memberManager;
  @Inject private Localizer     localizer;
  @Inject private ClaimsManager claimsManager;
  @Inject private Economy       economy;

  @Default
  public void claimSingle(Player player){
    boolean isACityMember = memberManager.isAlreadyCityMember(player.getUniqueId());
    if(!isACityMember){
      localizer.sendMessage(player, Messages.CITY_CLAIM_NOT_A_CITY_MEMBER);
      return;
    }

    Member member = memberManager.get(player.getUniqueId());
    if(member.getRole() == Roles.HELPER){
      localizer.sendMessage(player, Messages.CITY_CLAIM_NO_PERMISSION);
      return;
    }

    Chunk chunkToClaim = player.getChunk();
    if(claimsManager.hasChunkBeenClaimed(chunkToClaim)){
      localizer.sendMessage(player, Messages.CITY_CLAIM_CHUNK_ALREADY_CLAIMED);
      return;
    }

    double distanceToNearestOriginSquared = claimsManager.getDistanceSquaredToNearestOrigin(chunkToClaim);
    double maxDistanceFromOrigin = config.getMaxClaimDistanceSquared();

    if(distanceToNearestOriginSquared >= maxDistanceFromOrigin){
      localizer.sendMessage(player, Messages.CITY_CLAIM_TOO_FAR_AWAY);
    }

    if(claimsManager.getNearestCity(chunkToClaim) == null){
      localizer.sendMessage(player, Messages.CITY_CLAIM_NO_NEAREST_CITY);
      return;
    }

    long nearestCityID = claimsManager.getNearestCity(chunkToClaim).getId();
    long memberCityID = member.getCityId();

    if(nearestCityID != memberCityID){
      localizer.sendMessage(player, Messages.CITY_CLAIM_TOO_CLOSE);
      return;
    }

    //after all those checks, the point is legal. Starting the payment process.

    long numberOfCityClaims = claimsManager.getClaimCount(memberCityID);
    double claimPrice = claimsManager.getClaimPrice(numberOfCityClaims);

    Confirmation.prompt(player,
                        localizer.getColorizedMessage(Messages.CITY_CLAIM_FEE_PROMPT, claimPrice),
                        () -> {
                          if(!economy.has(player, claimPrice)){
                            localizer.sendMessage(player, Messages.CITY_CLAIM_FEE, claimPrice);
                            return;
                          }
                          onConfirmSingle(player, claimPrice, memberCityID, chunkToClaim);
                        },
                        () -> localizer.sendMessage(player, Messages.CITY_CLAIM_CANCEL));

  }

  @Subcommand("square")
  public void claimSquare(Player player, int radius){
    boolean isACityMember = memberManager.isAlreadyCityMember(player.getUniqueId());
    if(!isACityMember){
      localizer.sendMessage(player, Messages.CITY_CLAIM_NOT_A_CITY_MEMBER);
      return;
    }

    Member member = memberManager.get(player.getUniqueId());
    if(member.getRole() == Roles.HELPER){
      localizer.sendMessage(player, Messages.CITY_CLAIM_NO_PERMISSION);
      return;
    }

    Chunk centralChunk = player.getChunk();

    if(radius >= config.getMinDistanceBetweenCityOrigins()){
      //TODO localizer
      return;
    }

    if(claimsManager.getNearestCity(player.getChunk()) == null){
      localizer.sendMessage(player, Messages.CITY_CLAIM_NO_NEAREST_CITY);
      return;
    }

    int numberOfChunksToClaim = 0;
    for(int dx = -radius; dx <= radius; dx++){
      for(int dz = -radius; dz <= radius; dz++) {
        int x = centralChunk.getX() + dx;
        int z = centralChunk.getZ() + dz;

        if(claimsManager.hasChunkBeenClaimed(player.getWorld().getUID(), Chunk.getChunkKey(x,z))){
          continue;
        }

        //if(claimsManager.getDistanceSquaredToNearestOrigin(player.getWorld().getUID(), x, z) >=

      }
    }
  }

  @Subcommand("circle")
  public void claimCircle(Player player, int radius){
    boolean isACityMember = memberManager.isAlreadyCityMember(player.getUniqueId());
    if(!isACityMember){
      localizer.sendMessage(player, Messages.CITY_CLAIM_NOT_A_CITY_MEMBER);
      return;
    }

    Member member = memberManager.get(player.getUniqueId());
    if(member.getRole() == Roles.HELPER){
      localizer.sendMessage(player, Messages.CITY_CLAIM_NO_PERMISSION);
      return;
    }

    Chunk centralChunk = player.getChunk();

    if(radius >= config.getMinDistanceBetweenCityOrigins()){
      //TODO localizer
      return;
    }

    if(claimsManager.getNearestCity(player.getChunk()) == null){
      localizer.sendMessage(player, Messages.CITY_CLAIM_NO_NEAREST_CITY);
      return;
    }

    int numberOfChunksToClaim = 0;
    final int radiusSquared = radius*radius;

    for(int dx = -radius; dx <= radius; dx++){
      for(int dz = -radius; dz <= radius; dz++) {

        if(dx*dx + dz*dz > radiusSquared){
          continue;
        }

        int x = centralChunk.getX() + dx;
        int z = centralChunk.getZ() + dz;

        if(claimsManager.hasChunkBeenClaimed(player.getWorld().getUID(), Chunk.getChunkKey(x,z))){
          continue;
        }

        //if(claimsManager.getDistanceSquaredToNearestOrigin(player.getWorld().getUID(), x, z) >=

      }
    }
  }

  @Subcommand("auto")
  public void claimAuto(Player player){
    throw new UnsupportedOperationException();
  }

  private void onConfirmSingle(Player player, double claimPrice, long cityId, Chunk chunk){
    economy.withdrawPlayer(player, claimPrice);
    claimsManager.claim(cityId, chunk);
    localizer.sendMessage(player, Messages.CITY_CLAIM_CONFIRM);
  }

}
