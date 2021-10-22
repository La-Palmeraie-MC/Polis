package fr.lapalmeraiemc.polis.utils.cache;

import com.google.common.cache.CacheBuilder;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;


@SuppressWarnings("unused")
@NoArgsConstructor
public class TimedCacheBuilder<K, V> {

  // region Weak / Soft
  private boolean weakKeys   = false;
  private boolean weakValues = false;

  private boolean softValues = false;

  public TimedCacheBuilder<K, V> weakKeys() {
    weakKeys = true;
    return this;
  }

  public TimedCacheBuilder<K, V> weakValues() {
    weakValues = true;
    softValues = false;
    return this;
  }

  public TimedCacheBuilder<K, V> softValues() {
    weakValues = false;
    softValues = true;
    return this;
  }
  // endregion

  // region Timed expiry
  private long     expiresAfterWriteDuration = 600;
  private TimeUnit expiresAfterWriteUnit     = TimeUnit.SECONDS;

  private long     expiresAfterAccessDuration = -1;
  private TimeUnit expiresAfterAccessUnit     = TimeUnit.SECONDS;

  public TimedCacheBuilder<K, V> expiresAfterWrite(final long duration, @NotNull final TimeUnit unit) {
    expiresAfterWriteDuration = duration;
    expiresAfterWriteUnit = unit;
    return this;
  }

  public TimedCacheBuilder<K, V> expiresAfterAccess(final long duration, @NotNull final TimeUnit unit) {
    expiresAfterAccessDuration = duration;
    expiresAfterAccessUnit = unit;
    return this;
  }
  // endregion

  // region Removal Listener
  private CacheRemoval<K, V> cacheRemoval;

  public TimedCacheBuilder<K, V> removalListener(@NotNull final CacheRemoval<K, V> cacheRemoval) {
    this.cacheRemoval = cacheRemoval;
    return this;
  }
  // endregion

  @SuppressWarnings({ "unchecked", "ResultOfMethodCallIgnored", "CodeBlock2Expr" })
  public TimedCache<K, V> build() {
    final CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();

    if (weakKeys) cacheBuilder.weakKeys();
    if (weakValues) cacheBuilder.weakValues();
    if (softValues) cacheBuilder.softValues();

    if (expiresAfterWriteDuration > 0) {
      cacheBuilder.expireAfterWrite(expiresAfterWriteDuration, expiresAfterWriteUnit);
    }
    if (expiresAfterAccessDuration > 0) {
      cacheBuilder.expireAfterAccess(expiresAfterAccessDuration, expiresAfterAccessUnit);
    }

    if (cacheRemoval != null) {
      cacheBuilder.removalListener(notification -> {
        cacheRemoval.onRemoval((K) notification.getKey(), (V) notification.getValue(), notification.wasEvicted());
      });
    }

    return new TimedCache<>(cacheBuilder.build());
  }

}
