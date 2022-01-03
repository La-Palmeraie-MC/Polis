package fr.lapalmeraiemc.polis.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;


public class LocationAdapter extends TypeAdapter<Location> {

  @Override
  public void write(JsonWriter writer, Location value) throws IOException {
    writer.beginObject();
    writer.name("world");
    writer.value(value.getWorld().getName());
    writer.name("x");
    writer.value(value.getX());
    writer.name("y");
    writer.value(value.getY());
    writer.name("z");
    writer.value(value.getZ());
    writer.name("yaw");
    writer.value(value.getYaw());
    writer.name("pitch");
    writer.value(value.getPitch());
    writer.endObject();
  }

  @Override
  public Location read(JsonReader reader) throws IOException {
    final Location value = new Location(null, 0, 0, 0);
    reader.beginObject();
    String fieldname = null;

    while (reader.hasNext()) {
      if (reader.peek().equals(JsonToken.NAME)) fieldname = reader.nextName();
      if (fieldname == null) continue;

      switch (fieldname) {
        case "world" -> value.setWorld(Bukkit.getWorld(reader.nextString()));
        case "x" -> value.setX(reader.nextDouble());
        case "y" -> value.setY(reader.nextDouble());
        case "z" -> value.setZ(reader.nextDouble());
        case "yaw" -> value.setYaw((float) reader.nextDouble());
        case "pitch" -> value.setPitch((float) reader.nextDouble());
      }
    }

    reader.endObject();
    return value;
  }

}
