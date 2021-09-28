package fr.lapalmeraiemc.polis.listeners;

import fr.lapalmeraiemc.polis.models.MemberManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.inject.Inject;


public class JoinListener implements Listener {

  @Inject private MemberManager memberManager;

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    memberManager.load(event.getPlayer().getUniqueId());
  }

}
