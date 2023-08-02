package me.lebogo.simplewaystones;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerSneakListener implements Listener {
    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        if (!event.isSneaking())
            return;

        Block blockBelowPlayer = playerLocation.getBlock().getRelative(0, -1, 0);
        Material blockBelowPlayerMaterial = blockBelowPlayer.getType();

        if (!WaystoneManager.getWaystoneCenterBlocks().contains(blockBelowPlayerMaterial))
            return;

        if (!WaystoneManager.detectWaystoneStructure(blockBelowPlayer.getLocation()))
            return;

        event.setCancelled(true);

        SimpleWaystones.waystoneManager.openWaystoneMenu(player);
    }
}
