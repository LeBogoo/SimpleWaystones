package me.lebogo.simplewaystones;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.lebogo.simplewaystones.config.Waystone;
import me.lebogo.simplewaystones.config.WaystoneConfig;

import java.util.List;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // check if title is type of TextComponent
        if (!(event.getView().title() instanceof TextComponent)) {
            return;
        }

        TextComponent title = (TextComponent) event.getView().title();

        if (title.color() == null) {
            return;
        }

        boolean isCustomInventory = title.color().asHexString().equals("#3f3f3f");

        if (!isCustomInventory) {
            return;
        }

        event.setCancelled(true);

        boolean isWaystonesMenu = title.content().equals("Waystones");

        if (!isWaystonesMenu) {
            return;
        }

        Inventory inventory = event.getClickedInventory();

        if (inventory == null) {
            return;
        }

        if (inventory != event.getView().getTopInventory()) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        String itemName = ((TextComponent) clickedItem.getItemMeta().displayName()).content();
        if (itemName.equals(" ")) {
            return;
        }

        if (title.content().equals("Waystones")) {
            handleWaystoneMenu(event);
        }
    }

    private void handleWaystoneMenu(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) {
            return;
        }

        String itemName = ((TextComponent) clickedItem.getItemMeta().displayName()).content();

        if (itemName.equals(" ")) {
            return;
        }

        if (itemName.equals("Forget Waypoint")) {
            forgetWaypoint(event);
            return;
        }

        Player player = (Player) event.getWhoClicked();

        Waystone waystone = getWaystoneFromItem(clickedItem, player);
        if (waystone == null) {
            return;
        }

        if (!waystone.validate()) {
            Component message = Component.text("This waystone is broken.");
            message = message.color(TextColor.color(0xF75353));

            player.sendMessage(message);
            return;
        }

        player.teleport(waystone.getLocation().clone().add(0.5, 1, 0.5));

        Component message = Component.text("You have been teleported to " + waystone.getName() + "!");
        message = message.color(TextColor.color(0x54FB54));

        player.sendMessage(message);

    }

    private Waystone getWaystoneFromItem(ItemStack item, Player player) {
        String itemName = ((TextComponent) item.getItemMeta().displayName()).content();
        ItemMeta itemMeta = item.getItemMeta();
        List<Component> lore = itemMeta.lore();

        String coordinateLine = ((TextComponent) lore.get(0)).content();
        String[] coordinateLineSplit = coordinateLine.split(", ");
        int x = Integer.parseInt(coordinateLineSplit[0]);
        int y = Integer.parseInt(coordinateLineSplit[1]);
        int z = Integer.parseInt(coordinateLineSplit[2]);

        WaystoneConfig waystoneConfig = SimpleWaystones.waystoneManager.getWaystoneConfig(player);

        List<Waystone> waystones = waystoneConfig.getWaystones();
        for (Waystone waystone : waystones) {
            if (!waystone.getName().equals(itemName)) {
                continue;
            }

            if (waystone.getLocation().getBlockX() != x) {
                System.out.println("a");
                continue;
            }

            System.out.println("b");

            if (waystone.getLocation().getBlockY() != y) {
                continue;
            }

            if (waystone.getLocation().getBlockZ() != z) {
                continue;
            }

            return waystone;
        }

        return null;
    }

    private void forgetWaypoint(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        int slot = event.getSlot() - 9;

        ItemStack waystoneItem = inventory.getItem(slot);

        Waystone waystone = getWaystoneFromItem(waystoneItem, player);

        if (waystone == null) {
            return;
        }

        WaystoneConfig waystoneConfig = SimpleWaystones.waystoneManager.getWaystoneConfig(player);
        waystoneConfig.removeWaystone(waystone);

        player.closeInventory();

        Component message = Component
                .text("You have forgotten the waystone.");
        message = message.color(TextColor.color(0xF75353));

        player.sendMessage(message);
    }
}
