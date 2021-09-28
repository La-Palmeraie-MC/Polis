package fr.lapalmeraiemc.polis.listeners;

import fr.lapalmeraiemc.polis.models.MemberManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


@RequiredArgsConstructor
public class JoinListener implements Listener {

  private final MemberManager memberManager;

  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    memberManager.load(event.getPlayer().getUniqueId());
  }

}
