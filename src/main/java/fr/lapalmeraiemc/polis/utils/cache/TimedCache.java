package fr.lapalmeraiemc.polis.utils.cache;

import com.google.common.cache.Cache;
import fr.lapalmeraiemc.polis.Polis;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class TimedCache<K, V> {

  @Getter(AccessLevel.PRIVATE)
  @Setter(AccessLevel.PRIVATE)
  private static       BukkitTask                           cleanupScheduler;
  private static final Set<WeakReference<TimedCache<?, ?>>> caches = new HashSet<>();

  private final Cache<K, V> cache;

  @SuppressWarnings("CodeBlock2Expr")
  TimedCache(@NotNull final Cache<K, V> cache) {
    this.cache = cache;

    if (getCleanupScheduler() == null) {
      setCleanupScheduler(Bukkit.getScheduler().runTaskTimerAsynchronously(Polis.getInstance(), () -> {
        caches.stream().map(Reference::get).filter(Objects::nonNull).forEach(TimedCache::cleanUp);
      }, 20, 20));
    }

    caches.add(new WeakReference<>(this));
  }

  public void put(K key, V value) {
    cache.put(key, value);
  }

  public V get(K key) {
    return cache.getIfPresent(key);
  }

  public void cleanUp() {
    cache.cleanUp();
  }

  public void invalidate(K key) {
    cache.invalidate(key);
  }

  public void invalidateAll() {
    cache.invalidateAll();
  }

}
