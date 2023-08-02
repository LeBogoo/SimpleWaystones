package me.lebogo.simplewaystones;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import me.lebogo.simplewaystones.config.Waystone;

import java.io.File;
import java.util.logging.Logger;

public final class SimpleWaystones extends JavaPlugin {
    public static Logger LOGGER = Logger.getLogger("SimpleWaystones");
    public static SimpleWaystones INSTANCE;

    public static WaystoneManager waystoneManager = new WaystoneManager();

    public static final String PLUGIN_DIRECTORY = "plugins/SimpleWaystones/";

    static {
        ConfigurationSerialization.registerClass(Waystone.class, "Waystone");
    }

    @Override
    public void onEnable() {
        INSTANCE = this;

        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerSneakListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);

        File waystonesDirectory = new File(SimpleWaystones.PLUGIN_DIRECTORY + "waystones/");
        if (!waystonesDirectory.exists()) {
            waystonesDirectory.mkdirs();
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
