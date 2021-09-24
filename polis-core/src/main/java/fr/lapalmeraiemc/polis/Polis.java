package fr.lapalmeraiemc.polis;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import fr.lapalmeraiemc.polis.utils.ReflectionUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;


public class Polis extends JavaPlugin {

  private Config              config;
  private Localizer           localizer;
  private PaperCommandManager commandManager = null;
  private Economy             economy;

  @Override
  public void onEnable() {
    config = new Config(this);
    localizer = new Localizer(this);

    initializeEconomy();
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
        throw new ConditionFailedException(String.format("The minimum accepted value is %s", c.getConfigValue("min", 0)));
      }
      if (c.hasConfig("max") && c.getConfigValue("max", 3) < value) {
        throw new ConditionFailedException(String.format("The maximum accepted value is %s", c.getConfigValue("max", 3)));
      }
    });

    commandManager.getCommandContexts().registerContext(String.class, ctx -> {
      if (!ctx.hasFlag("quoted") || !ctx.getFirstArg().startsWith("\"")) return ctx.popFirstArg();

      Integer end = null;
      for (int i = 0; i < ctx.getArgs().size(); i++) {
        if ((ctx.getArgs().get(i).endsWith("\"") && (i != 0 || ctx.getArgs().get(i).length() > 1))) {
          end = i;
          break;
        }
      }

      if (end == null) return ctx.popFirstArg();

      final StringBuilder builder = new StringBuilder();
      for (int i = 0; i <= end; i++) {
        String currentArg = ctx.popFirstArg();
        if (i == 0) {
          currentArg = currentArg.substring(1);
        }
        else {
          builder.append(" ");
        }
        if (i == end) {
          currentArg = currentArg.substring(0, currentArg.length() - 1);
        }
        builder.append(currentArg);
      }

      return builder.toString();
    });

    commandManager.registerDependency(Config.class, config);
    commandManager.registerDependency(Localizer.class, localizer);
    commandManager.registerDependency(Economy.class, economy);

    final Set<BaseCommand> commands = ReflectionUtils.getClassInstancesExtending(BaseCommand.class,
                                                                                 "fr.lapalmeraiemc.polis.commands");

    commands.forEach(commandManager::registerCommand);

    commandManager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
      getLogger().warning(String.format("An error occured while executing command: %s", command.getName()));
      return false;
    });
  }

  private void initializeEconomy() {
    if (!getServer().getPluginManager().isPluginEnabled("Vault"))
      throw new RuntimeException("Vault is needed to use this plugin.");

    final RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);

    if (economyProvider == null) throw new RuntimeException("An error occured while getting Vault.");

    economy = economyProvider.getProvider();
  }

}
