package me.lebogo.simplewaystones.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import me.lebogo.simplewaystones.WaystoneManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

@SerializableAs("Waystone")
public class Waystone implements ConfigurationSerializable {
    private Location location;

    public Waystone(Location location) {
        this.location = location;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("location", location);

        return result;
    }

    public static Waystone deserialize(Map<String, Object> args) {
        Location location = new Location(null, 0, 0, 0, 0, 0);

        if (args.containsKey("location"))
            location = (Location) args.get("location");

        return new Waystone(location);
    }

    public String getName() {
        String name = "Waystone";
        Block signBlock = location.clone().add(0, -2, 0).getBlock();

        if (!signBlock.getType().name().endsWith("SIGN")) {
            System.out.println("Block is not a sign.");
            return name;
        }

        if (!(signBlock.getState() instanceof Sign)) {
            System.out.println("Block state is not a sign.");
            return name;
        }

        Sign sign = (Sign) signBlock.getState();

        List<Component> lines = sign.getSide(Side.FRONT).lines();
        lines.addAll(sign.getSide(Side.BACK).lines());

        String combined = "";

        for (Component line : lines) {
            if (line == null)
                continue;

            TextComponent textComponent = (TextComponent) line;
            combined += textComponent.content();
        }

        if (!combined.isEmpty()) {
            name = combined;
        }

        if (name.length() >= 20) {
            name = combined.substring(0, 20) + "...";
        }

        return name;
    }

    public Location getLocation() {
        return location;
    }

    public boolean validate() {
        return WaystoneManager.detectWaystoneStructure(location);
    }

    public Material getMaterial() {
        return location.getBlock().getType();
    }

    @Override
    public String toString() {
        return "Waystone [location=" + location + ", name=" + getName() + "]";
    }
}
