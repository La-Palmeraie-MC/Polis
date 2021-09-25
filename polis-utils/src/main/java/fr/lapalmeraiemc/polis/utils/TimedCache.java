package fr.lapalmeraiemc.polis.utils;

import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RequiredArgsConstructor
public class TimedCache<K, V> {

  private static final long DEFAULT_TIMEOUT = 600;

  private final Map<K, CacheEntry<V>> cache = new HashMap<>();
  private final long                  timeout;

  public TimedCache() {
    this(DEFAULT_TIMEOUT);
  }

  public void put(K key, V value) {
    cleanup();
    cache.put(key, new CacheEntry<>(value));
  }

  public V get(K key) {
    cleanup();
    final CacheEntry<V> entry = cache.get(key);
    return entry != null ? entry.getValue() : null;
  }

  public void cleanup() {
    final long currentTime = System.currentTimeMillis();
    cache.entrySet()
         .removeIf(entry -> TimeUnit.MILLISECONDS.toSeconds(currentTime - entry.getValue().getInsertTime()) >= timeout);
  }

  public V invalidate(K key) {
    cleanup();
    final CacheEntry<V> entry = cache.remove(key);
    return entry != null ? entry.getValue() : null;
  }

  public void invalidateAll() {
    cache.clear();
  }

  @Value
  private static class CacheEntry<T> {

    T    value;
    long insertTime = System.currentTimeMillis();

  }

}
