package me.lebogo.simplewaystones.config;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import me.lebogo.simplewaystones.SimpleWaystones;

public class WaystoneConfig {
    YamlConfiguration config = new YamlConfiguration();
    String configPath;

    public WaystoneConfig(Player player) {
        configPath = SimpleWaystones.PLUGIN_DIRECTORY + "waystones/" + player.getUniqueId() + ".yml";

        try {
            config.load(configPath);
        } catch (Exception e) {
            SimpleWaystones.LOGGER.info("Creating new waystone config for " + player.getName());
        }
    }

    public boolean addWaystone(Waystone waystone) {
        List<Waystone> waystones = getWaystones();

        if (waystones.size() >= 9) {
            return false;
        }

        waystones.add(waystone);
        config.set("waystones", waystones);
        save();

        return true;
    }

    public void removeWaystone(Waystone waystone) {
        List<Waystone> waystones = getWaystones();

        for (Waystone waystoneInList : waystones) {
            if (waystoneInList.getLocation().getBlock().equals(waystone.getLocation().getBlock())) {
                waystones.remove(waystoneInList);
                break;
            }
        }

        config.set("waystones", waystones);
        save();
    }

    public List<Waystone> getWaystones() {
        return (List<Waystone>) config.getList("waystones", new ArrayList<>());
    }

    public Waystone getWaystone(Location centerBlockLocation) {
        List<Waystone> waystones = getWaystones();

        for (Waystone waystone : waystones) {
            if (waystone.getLocation().equals(centerBlockLocation)) {
                return waystone;
            }
        }

        return null;
    }

    private void save() {
        // create directories if they don't exist

        try {
            config.save(configPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
