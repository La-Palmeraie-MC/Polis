package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
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
import java.util.Objects;


@CommandAlias("city|ville")
public class ClaimChunkCommands extends BaseCommand {

  @Inject private Config        config;
  @Inject private MemberManager memberManager;
  @Inject private Localizer     localizer;
  @Inject private ClaimsManager claimsManager;
  @Inject private Economy       economy;

  @Subcommand("claim")
  @CommandCompletion("|square|circle|auto")
  public void claim(Player player, @Values("|square|circle|auto") String mode, String radius) {

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

    if(mode.isBlank()){
      Chunk chunkToClaim = player.getChunk();
      if(claimsManager.hasChunkBeenClaimed(chunkToClaim)){
        localizer.sendMessage(player, Messages.CITY_CLAIM_CHUNK_ALREADY_CLAIMED);
        return;
      }

      long nearestCityID = claimsManager.getNearestCity(chunkToClaim).getId();
      long memberCityID = member.getCityId();

      if(nearestCityID != memberCityID){
        localizer.sendMessage(player, Messages.CITY_CLAIM_TOO_CLOSE);
        return;
      }

      double distanceToNearestOriginSquared = claimsManager.getDistanceSquaredToNearestOrigin(chunkToClaim);
      double maxDistanceFromOrigin = config.getMaxClaimDistanceSquared();

      if(distanceToNearestOriginSquared >= maxDistanceFromOrigin){
        localizer.sendMessage(player, Messages.CITY_CLAIM_TOO_FAR_AWAY);
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
    if(mode.equals("square")){
      throw new UnsupportedOperationException();
    }
    if(mode.equals("circle")){
      throw new UnsupportedOperationException();
    }
    if(mode.equals("auto")){
      throw new UnsupportedOperationException();
    }
  }

  private void onConfirmSingle(Player player, double claimPrice, long cityId, Chunk chunk){
    economy.withdrawPlayer(player, claimPrice);
    claimsManager.claim(cityId, chunk);
    localizer.sendMessage(player, Messages.CITY_CLAIM_CONFIRM);
  }

}
