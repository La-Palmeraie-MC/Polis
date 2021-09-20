package fr.lapalmeraiemc.polis;

import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import fr.lapalmeraiemc.polis.commands.PolisBaseCommand;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import fr.lapalmeraiemc.polis.utils.ReflectionUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.logging.Logger;


public class Polis extends JavaPlugin {

  private Config              config;
  private Localizer           localizer;
  private PaperCommandManager commandManager = null;

  @Override
  public void onEnable() {
    config = new Config(this);
    localizer = new Localizer(this);

    initializeCommands();

    getLogger().info("Successfully enabled!");
  }

  @Override
  public void onDisable() {
    if (commandManager != null) commandManager.unregisterCommands();

    getLogger().info("Successfully disabled!");
  }

  private void initializeCommands() {
    commandManager = new PaperCommandManager(this);
    commandManager.enableUnstableAPI("help");

    commandManager.getCommandConditions().addCondition(Integer.class, "limits", (c, exec, value) -> {
      if (value == null) return;
      if (c.hasConfig("min") && c.getConfigValue("min", 0) > value) {
        throw new ConditionFailedException(
            String.format("The minimum accepted value is %s", c.getConfigValue("min", 0)));
      }
      if (c.hasConfig("max") && c.getConfigValue("max", 3) < value) {
        throw new ConditionFailedException(
            String.format("The maximum accepted value is %s", c.getConfigValue("max", 3)));
      }
    });

    final Set<PolisBaseCommand> commands = ReflectionUtils.getClassInstancesExtending(PolisBaseCommand.class, "fr.lapalmeraiemc.polis.commands",
                                                                                      Plugin.class, this, Logger.class, getLogger(),
                                                                                      Config.class, config, Localizer.class, localizer);

    commands.forEach(commandManager::registerCommand);

    commandManager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
      getLogger().warning(String.format("An error occured while executing command: %s", command.getName()));
      return false;
    });
  }

}
