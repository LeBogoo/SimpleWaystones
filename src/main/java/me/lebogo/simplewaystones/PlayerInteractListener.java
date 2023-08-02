package me.lebogo.simplewaystones;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import me.lebogo.simplewaystones.config.Waystone;
import me.lebogo.simplewaystones.config.WaystoneConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class PlayerInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteractBlock(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        Player player = event.getPlayer();

        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        if (clickedBlock == null) {
            return;
        }

        if (!WaystoneManager.getWaystoneCenterBlocks().contains(clickedBlock.getType())) {
            return;
        }

        Location location = clickedBlock.getLocation();
        boolean isWaystoneStructure = WaystoneManager.detectWaystoneStructure(location);

        if (!isWaystoneStructure) {
            return;
        }

        event.setCancelled(true);

        WaystoneConfig waystoneConfig = SimpleWaystones.waystoneManager.getWaystoneConfig(player);

        Waystone waystone = waystoneConfig.getWaystone(location);
        if (waystone != null) {
            Component message = Component.text("You have already discovered this waystone!");
            message = message.color(TextColor.color(0xF75353));

            player.sendMessage(message);
            return;
        }

        // get name from sign

        waystone = new Waystone(location);
        boolean successful = waystoneConfig.addWaystone(waystone);

        Component message = Component
                .text("You already have reached the maximum amount of waystones!");
        message = message.color(TextColor.color(0xF75353));

        if (successful) {
            message = Component.text("You have discovered a new waystone!");
            message = message.color(TextColor.color(0x54FB54));
        }

        player.sendMessage(message);
    }
}
