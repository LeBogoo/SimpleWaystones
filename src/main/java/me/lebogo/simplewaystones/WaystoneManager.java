package me.lebogo.simplewaystones;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import me.lebogo.simplewaystones.config.Waystone;
import me.lebogo.simplewaystones.config.WaystoneConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class WaystoneManager {
    YamlConfiguration config = new YamlConfiguration();
    Map<Player, WaystoneConfig> waystoneConfigs = new HashMap<Player, WaystoneConfig>();

    public WaystoneManager() {
    }

    public WaystoneConfig getWaystoneConfig(Player player) {
        if (waystoneConfigs.containsKey(player)) {
            return waystoneConfigs.get(player);
        }

        WaystoneConfig waystoneConfig = new WaystoneConfig(player);
        waystoneConfigs.put(player, waystoneConfig);
        return waystoneConfig;
    }

    public void openWaystoneMenu(Player player) {
        WaystoneConfig waystoneConfig = getWaystoneConfig(player);
        List<Waystone> waystones = waystoneConfig.getWaystones();

        Inventory inventory = SimpleWaystones.INSTANCE.getServer().createInventory(null, 9 * 2,
                Component.text("Waystones", TextColor.color(0x3F3F3F)));

        for (int i = 0; i < waystones.size(); i++) {
            Waystone waystone = waystones.get(i);
            Location location = waystone.getLocation();
            Material centerBlockMaterial = location.getBlock().getType();

            boolean isWaystoneIntact = detectWaystoneStructure(location);

            if (!isWaystoneIntact) {
                centerBlockMaterial = Material.LIGHT_GRAY_CONCRETE_POWDER;
            }

            // center block as item
            ItemStack itemStack = new ItemStack(centerBlockMaterial);

            ItemMeta itemMeta = itemStack.getItemMeta();
            // set title of item to waystone name
            itemMeta.displayName(Component.text(waystone.getName(), TextColor.color(0x00CEC0)));

            // add waystone coordinates to item meta

            List<Component> lore = new ArrayList<Component>();
            lore.add(Component.text(location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()));

            if (!isWaystoneIntact) {
                lore.add(Component.text("Waystone is not intact!", TextColor.color(0xA5A5A5)));
            }

            itemMeta.lore(lore);

            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);

            ItemStack deleteItemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta deleteItemMeta = deleteItemStack.getItemMeta();
            deleteItemMeta.displayName(Component.text("Forget Waypoint", TextColor.color(0xF75353)));
            deleteItemStack.setItemMeta(deleteItemMeta);

            inventory.setItem(i + 9, deleteItemStack);
        }

        ItemStack itemStack = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text(" "));
        itemStack.setItemMeta(itemMeta);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, itemStack);
            }
        }

        player.openInventory(inventory);
    }

    public static List<Material> getWaystoneCenterBlocks() {
        List<Material> lightBlocks = new ArrayList<Material>();
        lightBlocks.add(Material.BEACON);
        lightBlocks.add(Material.OCHRE_FROGLIGHT);
        lightBlocks.add(Material.VERDANT_FROGLIGHT);
        lightBlocks.add(Material.PEARLESCENT_FROGLIGHT);
        lightBlocks.add(Material.GLOWSTONE);
        lightBlocks.add(Material.JACK_O_LANTERN);
        lightBlocks.add(Material.REDSTONE_LAMP);
        lightBlocks.add(Material.SEA_LANTERN);
        lightBlocks.add(Material.SHROOMLIGHT);
        lightBlocks.add(Material.CRYING_OBSIDIAN);
        lightBlocks.add(Material.ENDER_CHEST);
        lightBlocks.add(Material.ENCHANTING_TABLE);
        lightBlocks.add(Material.GLOW_LICHEN);
        lightBlocks.add(Material.SCULK_CATALYST);
        lightBlocks.add(Material.MAGMA_BLOCK);
        lightBlocks.add(Material.SCULK_SENSOR);
        lightBlocks.add(Material.RESPAWN_ANCHOR); // TODO - Experimental!!

        return lightBlocks;
    }

    public static boolean detectWaystoneStructure(Location location) {
        Material centerMaterial = location.getBlock().getType();

        if (!getWaystoneCenterBlocks().contains(centerMaterial)) {
            return false;
        }

        // Check if the block below is a diamond block
        Location blockBelowLocation = location.clone().subtract(0, 1, 0);
        Material blockBelowMaterial = blockBelowLocation.getBlock().getType();

        if (blockBelowMaterial != Material.DIAMOND_BLOCK) {
            return false;
        }

        // check if blocks x+1, x-1, z+1, z-1 are any stair block
        List<Location> stairLocations = new ArrayList<Location>();
        stairLocations.add(location.clone().add(1, 0, 0));
        stairLocations.add(location.clone().add(-1, 0, 0));
        stairLocations.add(location.clone().add(0, 0, 1));
        stairLocations.add(location.clone().add(0, 0, -1));

        for (Location stairLocation : stairLocations) {
            Material blockMaterial = stairLocation.getBlock().getType();

            if (!blockMaterial.toString().contains("STAIRS")) {
                return false;
            }
        }

        return true;
    }
}
