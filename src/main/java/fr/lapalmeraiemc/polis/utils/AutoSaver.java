package fr.lapalmeraiemc.polis.utils;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;


@RequiredArgsConstructor
public class AutoSaver {

  @Inject private Plugin plugin;
  @Inject private Config config;

  private final Set<AutoSaveable> autoSaveableSet = new HashSet<>();
  private       BukkitTask        task;

  public void add(@NotNull final AutoSaveable autoSaveable) {
    autoSaveableSet.add(autoSaveable);
  }

  public void enable() {
    task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
      autoSaveableSet.forEach(autoSaveable -> autoSaveable.save(false));
    }, config.getAutoSavePeriod() * 20, config.getAutoSavePeriod() * 20);
  }

  public void disable() {
    task.cancel();
  }

}
