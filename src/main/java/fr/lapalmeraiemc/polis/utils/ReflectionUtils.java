package fr.lapalmeraiemc.polis.utils;

import com.google.inject.Injector;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectionUtils {

  private static String version = null;

  public static String getVersion() {
    if (version == null) version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    return version;
  }

  @Nullable
  public static Class<?> getNMSClass(@NotNull final String className) {
    try {
      return Class.forName(String.join(".", "net.minecraft.server", getVersion(), className));
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Nullable
  public static Class<?> getOBCClass(@NotNull final String className) {
    try {
      return Class.forName(String.join(".", "org.bukkit.craftbukkit", getVersion(), className));
    }
    catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public static <T> Set<Class<? extends T>> getClassesExtending(@NotNull final Class<T> type, @NotNull final String path) {
    return new Reflections(path).getSubTypesOf(type);
  }

  private static Class<?>[] getClassesFromPairs(final Object... classArgPairs) {
    final List<Class<?>> classes = new ArrayList<>();

    for (int i = 0; i < classArgPairs.length; i++) {
      if (i % 2 == 0) {
        if (classArgPairs[i] instanceof Class<?> clazz) classes.add(clazz);
        else throw new IllegalArgumentException("Pairs of class and objects must be supplied");
      }
    }

    return classes.toArray(Class[]::new);
  }

  private static Object[] getArgsFromPairs(final Object... classArgPairs) {
    final List<Object> args = new ArrayList<>();

    for (int i = 0; i < classArgPairs.length; i++) {
      if (i % 2 != 0) args.add(classArgPairs[i]);
    }

    return args.toArray(Object[]::new);
  }

  public static <T> Set<T> getClassInstancesExtending(@NotNull final Class<T> type, @NotNull final String path,
                                                      final Object... classArgPairs) {
    if (classArgPairs.length % 2 != 0) throw new IllegalArgumentException("Pairs of class and objects must be supplied");

    final Class<?>[] classes = getClassesFromPairs(classArgPairs);
    final Object[] args = getArgsFromPairs(classArgPairs);

    final Set<T> instances = new HashSet<>();
    for (final Class<? extends T> clazz : getClassesExtending(type, path)) {
      if (!Modifier.isAbstract(clazz.getModifiers()) || !Modifier.isInterface(clazz.getModifiers())) {
        try {
          instances.add(clazz.getDeclaredConstructor(classes).newInstance(args));
        }
        catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ignored) {}
      }
    }
    return instances;
  }

  public static <T> Set<T> getClassInstancesExtending(@NotNull final Injector injector, @NotNull final Class<T> type,
                                                      @NotNull final String path) {
    final Set<T> instances = new HashSet<>();
    for (final Class<? extends T> clazz : getClassesExtending(type, path)) {
      if (!Modifier.isAbstract(clazz.getModifiers()) || !Modifier.isInterface(clazz.getModifiers())) {
        instances.add(injector.getInstance(clazz));
      }
    }
    return instances;
  }

  public static Set<Method> getMethodsAnnotatedWith(@NotNull final Class<? extends Annotation> annotation,
                                                    @NotNull final String path) {
    return new Reflections(path, new MethodAnnotationsScanner()).getMethodsAnnotatedWith(annotation);
  }

  public static boolean areMethodParamsAssignableFrom(@NotNull final Method method, @NotNull final Class<?>... classes) {
    Class<?>[] params = method.getParameterTypes();

    if (params.length != classes.length) return false;

    for (int i = 0; i < params.length; i++) {
      if (!params[i].isAssignableFrom(classes[i])) return false;
    }

    return true;
  }

}
