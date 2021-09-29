package fr.lapalmeraiemc.polis;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.PaperCommandManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import fr.lapalmeraiemc.polis.models.CityManager;
import fr.lapalmeraiemc.polis.models.MemberManager;
import fr.lapalmeraiemc.polis.utils.AutoSaver;
import fr.lapalmeraiemc.polis.utils.Config;
import fr.lapalmeraiemc.polis.utils.Localizer;
import fr.lapalmeraiemc.polis.utils.ReflectionUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;


public class Polis extends JavaPlugin {

  private Gson          gson;
  private Config        config;
  private Localizer     localizer;
  private Economy       economy;
  private CityManager   cityManager;
  private MemberManager memberManager;

  private Injector injector;

  private PaperCommandManager commandManager = null;
  private AutoSaver           autoSaver      = null;

  @Override
  public void onEnable() {
    config = new Config(this);
    localizer = new Localizer(this);

    setupGson();
    setupEconomy();

    cityManager = new CityManager(gson, this);
    memberManager = new MemberManager(gson, this);

    setupGuice();

    registerCommands();
    registerListeners();

    if (config.isAutoSaveEnabled()) {
      autoSaver = injector.getInstance(AutoSaver.class);

      autoSaver.add(cityManager);
      autoSaver.add(memberManager);

      autoSaver.enable();
    }

    getLogger().info("Successfully enabled!");
  }

  @Override
  public void onDisable() {
    if (commandManager != null) commandManager.unregisterCommands();
    if (autoSaver != null) autoSaver.disable();

    if (cityManager != null) cityManager.save(true);
    if (memberManager != null) memberManager.save(true);

    getLogger().info("Successfully disabled!");
  }

  private void setupGson() {
    gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().enableComplexMapKeySerialization().create();
  }

  private void setupEconomy() {
    if (!getServer().getPluginManager().isPluginEnabled("Vault"))
      throw new RuntimeException("Vault is needed to use this plugin.");

    final RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager()
                                                                          .getRegistration(Economy.class);

    if (economyProvider == null) throw new RuntimeException("An error occured while getting Vault.");

    economy = economyProvider.getProvider();
  }

  private void setupGuice() {
    final Polis polis = this;
    injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(Plugin.class).toInstance(polis);
        bind(JavaPlugin.class).toInstance(polis);

        bind(Gson.class).toInstance(gson);
        bind(Config.class).toInstance(config);
        bind(Localizer.class).toInstance(localizer);
        bind(Economy.class).toInstance(economy);

        bind(CityManager.class).toInstance(cityManager);
        bind(MemberManager.class).toInstance(memberManager);
      }
    });
  }

  private void registerCommands() {
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

    final Set<BaseCommand> commands = ReflectionUtils.getClassInstancesExtending(injector, BaseCommand.class,
                                                                                 "fr.lapalmeraiemc.polis.commands");

    commands.forEach(commandManager::registerCommand);

    commandManager.setDefaultExceptionHandler((command, registeredCommand, sender, args, t) -> {
      getLogger().warning(String.format("An error occured while executing command: %s", command.getName()));
      return false;
    });
  }

  private void registerListeners() {
    final Set<Listener> listeners = ReflectionUtils.getClassInstancesExtending(injector, Listener.class,
                                                                               "fr.lapalmeraiemc.polis.listeners");
    listeners.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
  }

}
