package fr.lapalmeraiemc.polis.utils.cache;

import org.jetbrains.annotations.Nullable;


public interface CacheRemoval<K, V> {

  void onRemoval(@Nullable K key, @Nullable V value, boolean wasEvicted);

}
