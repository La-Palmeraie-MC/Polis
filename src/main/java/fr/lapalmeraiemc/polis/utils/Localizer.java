package fr.lapalmeraiemc.polis.utils;

import fr.lapalmeraiemc.polis.enums.Messages;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class Localizer {

  @Getter
  @Setter(AccessLevel.PRIVATE)
  private static Localizer instance;

  private final Map<Messages, String> messages = new EnumMap<>(Messages.class);
  private final FileManager           messagesFile;

  public Localizer(@NotNull final Plugin plugin) {
    this(plugin, "messages.yml");
  }

  public Localizer(@NotNull final Plugin plugin, @NotNull final String filename) {
    this(plugin, filename, filename);
  }

  public Localizer(@NotNull final Plugin plugin, @NotNull final String resourceName, @NotNull final String saveName) {
    setInstance(this);

    messagesFile = new FileManager(plugin, resourceName, saveName);
    messagesFile.saveDefaults();
    reload();
  }

  public void reload() {
    messages.clear();
    messagesFile.reloadContent();
    messagesFile.saveDefaults();

    final FileConfiguration messagesContent = messagesFile.getContent();
    final Set<String> messageKeys = Arrays.stream(Messages.values())
                                          .map(Enum::name)
                                          .collect(Collectors.toUnmodifiableSet());

    for (final String key : messagesContent.getKeys(true)) {
      final String messageKey = key.replace(".", "_").replace("-", "_").toUpperCase();
      if (messageKeys.contains(messageKey)) messages.put(Messages.valueOf(messageKey), messagesContent.getString(key));
    }
  }

  public String getMessage(@NotNull final Messages key, @NotNull final Object... args) {
    String msg = messages.get(key);
    for (int i = 0; i < args.length; i++) {
      msg = msg.replace(String.format("{%s}", i), Objects.toString(args[i]));
    }
    return msg;
  }

  public Component getColorizedMessage(@NotNull final Messages key, @NotNull final Object... args) {
    return Colorizer.parse(getMessage(key, args));
  }

  public void sendMessage(@NotNull final Audience audience, @NotNull final Messages key,
                          @NotNull final Object... args) {
    audience.sendMessage(Identity.nil(), getColorizedMessage(key, args));
  }

}
