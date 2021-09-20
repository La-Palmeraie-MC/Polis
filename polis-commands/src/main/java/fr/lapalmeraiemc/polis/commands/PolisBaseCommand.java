package fr.lapalmeraiemc.polis.commands;

import co.aikar.commands.BaseCommand;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;


@Getter(AccessLevel.PROTECTED)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public abstract class PolisBaseCommand extends BaseCommand {

  Plugin    plugin;
  Logger    logger;
  Config    config;
  Localizer localizer;

}
