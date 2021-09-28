package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import fr.lapalmeraiemc.polis.enums.Messages;
import fr.lapalmeraiemc.polis.utils.Localizer;
import fr.lapalmeraiemc.polis.utils.TimedCache;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;


@CommandAlias("city|ville")
public class Confirmation extends BaseCommand {

  @Inject private Localizer localizer;

  private static final TimedCache<CommandSender, Runnable> waitingConfirmation = new TimedCache<>(30);

  @Subcommand("confirm")
  public void onConfirm(CommandSender sender) {
    final Runnable callback = waitingConfirmation.invalidate(sender);

    if (callback != null) callback.run();
    else sender.sendMessage(localizer.getColorizedMessage(Messages.NO_WAITING_CONFIRM));
  }

  public static void prompt(@NotNull final CommandSender receiver, @NotNull final Component promptText,
                            @NotNull final Runnable callback) {
    receiver.sendMessage(Identity.nil(), promptText, MessageType.SYSTEM);
    waitingConfirmation.put(receiver, callback);
  }

}
