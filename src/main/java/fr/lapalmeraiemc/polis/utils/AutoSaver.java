package fr.lapalmeraiemc.polis.utils;

import fr.lapalmeraiemc.polis.Polis;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;


@RequiredArgsConstructor
public class AutoSaver {

  private final long period;

  private final Set<AutoSaveable> autoSaveableSet = new HashSet<>();
  private       BukkitTask        task;

  public void add(@NotNull final AutoSaveable autoSaveable) {
    autoSaveableSet.add(autoSaveable);
  }

  public void enable() {
    task = Bukkit.getScheduler().runTaskTimerAsynchronously(Polis.getInstance(), () -> {
      autoSaveableSet.forEach(autoSaveable -> autoSaveable.save(false));
    }, period * 20, period * 20);
  }

  public void disable() {
    task.cancel();
  }

}
