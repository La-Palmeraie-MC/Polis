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
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;


@CommandAlias("city|ville")
public class Confirmation extends BaseCommand {

  @Inject private Localizer localizer;

  private static final TimedCache<CommandSender, Entry<Runnable, Runnable>> waitingResponse = new TimedCache<>(30);

  @Subcommand("confirm")
  public void onConfirm(CommandSender sender) {
    final Entry<Runnable, Runnable> callbacks = waitingResponse.invalidate(sender);

    if (callbacks != null && callbacks.getKey() != null) {
      callbacks.getKey().run();
    }
    else {
      sender.sendMessage(localizer.getColorizedMessage(Messages.NO_WAITING_CONFIRM));
    }
  }

  @Subcommand("cancel")
  public void onCancel(CommandSender sender) {
    final Entry<Runnable, Runnable> callbacks = waitingResponse.invalidate(sender);

    if (callbacks != null) {
      if (callbacks.getValue() != null) {
        callbacks.getValue().run();
      }
    }
    else {
      sender.sendMessage(localizer.getColorizedMessage(Messages.NO_WAITING_CANCEL));
    }
  }

  public static void prompt(@NotNull final CommandSender receiver, @NotNull final Component promptText,
                            @NotNull final Runnable onConfirm) {
    prompt(receiver, promptText, onConfirm, null);
  }

  public static void prompt(@NotNull final CommandSender receiver, @NotNull final Component promptText,
                            @NotNull final Runnable onConfirm, final Runnable onCancel) {
    receiver.sendMessage(Identity.nil(), promptText, MessageType.SYSTEM);
    waitingResponse.put(receiver, new SimpleImmutableEntry<>(onConfirm, onCancel));
  }

}
