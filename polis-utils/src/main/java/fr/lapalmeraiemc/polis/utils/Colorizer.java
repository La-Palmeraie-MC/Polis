package fr.lapalmeraiemc.polis.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Colorizer {

  private static final Pattern STYLE_PATTERN = Pattern.compile("&([0-9a-fA-Fk-oK-OrR]|#[0-9a-fA-F]{6}|#[0-9a-fA-F]{3})");
  private static final Pattern COLOR_PATTERN = Pattern.compile("^(?:[0-9a-fA-FrR]|#[0-9a-fA-F]{6}|#[0-9a-fA-F]{3})$");
  private static final Pattern HEX_PATTERN   = Pattern.compile("^(?:#[0-9a-fA-F]{3}|#[0-9a-fA-F]{6})$");


  public static Component parse(String str) {
    Component component = Component.empty();
    if (str == null) return component;

    Matcher matcher = STYLE_PATTERN.matcher(str);
    Style lastStyle = Style.empty();
    while (matcher.find()) {
      component = component.append(Component.text(str.substring(0, matcher.start()), lastStyle));

      if (COLOR_PATTERN.matcher(matcher.group(1)).matches()) {
        lastStyle = Style.empty().color(getColor(matcher.group(1)));
      }
      else {
        lastStyle = toggleDecoration(lastStyle, matcher.group(1));
      }

      str = str.substring(matcher.end());
      matcher = STYLE_PATTERN.matcher(str);
    }

    component = component.append(Component.text(str, lastStyle));
    return component;
  }


  private static @Nullable TextColor getColor(final String str) {
    return switch (str.toLowerCase()) {
      case "0" -> TextColor.color(0, 0, 0);
      case "1" -> TextColor.color(0, 0, 170);
      case "2" -> TextColor.color(0, 170, 0);
      case "3" -> TextColor.color(0, 170, 170);
      case "4" -> TextColor.color(170, 0, 0);
      case "5" -> TextColor.color(170, 0, 170);
      case "6" -> TextColor.color(255, 170, 0);
      case "7" -> TextColor.color(170, 170, 170);
      case "8" -> TextColor.color(85, 85, 85);
      case "9" -> TextColor.color(85, 85, 255);
      case "a" -> TextColor.color(85, 255, 85);
      case "b" -> TextColor.color(85, 255, 255);
      case "c" -> TextColor.color(255, 85, 85);
      case "d" -> TextColor.color(255, 85, 255);
      case "e" -> TextColor.color(255, 255, 85);
      case "f" -> TextColor.color(255, 255, 255);
      default -> HEX_PATTERN.matcher(str).matches() ? TextColor.fromCSSHexString(str) : null;
    };
  }


  private static Style toggleDecoration(@NotNull final Style style, @NotNull final String str) {
    return switch (str.toLowerCase()) {
      case "k" -> toggleDecoration(style, TextDecoration.OBFUSCATED);
      case "l" -> toggleDecoration(style, TextDecoration.BOLD);
      case "m" -> toggleDecoration(style, TextDecoration.STRIKETHROUGH);
      case "n" -> toggleDecoration(style, TextDecoration.UNDERLINED);
      case "o" -> toggleDecoration(style, TextDecoration.ITALIC);
      default -> style;
    };
  }


  private static Style toggleDecoration(@NotNull final Style style, @NotNull final TextDecoration decoration) {
    return style.decoration(decoration, style.decoration(decoration) != TextDecoration.State.TRUE);
  }

}
