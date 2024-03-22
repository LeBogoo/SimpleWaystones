package me.lebogo.simplewaystones;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;


public class Teleporter {

    private final Player player;
    private final Location target;

    public Teleporter(Player player, Location target) {
        this.player = player;
        this.target = target;
    }

    public static boolean isTeleporting(Player player) {
        Set<String> scoreboardTags = player.getScoreboardTags();
        return scoreboardTags.contains("teleporting");
    }

    public void teleport() {
        if (isTeleporting(player)) {
            player.sendMessage(Component.text("You are already teleporting.")
                    .style(Style.style(TextColor.color(0xFB5454))));
            return;
        }
        player.addScoreboardTag("teleporting");

        double teleportDuration = getTeleportDuration() + 1;
        Location startLocation = player.getLocation().clone();

        Plugin plugin = SimpleWaystones.getPlugin(SimpleWaystones.class);


        new BukkitRunnable() {
            @Override
            public void run() {
                BossBar manaBossBar = Bukkit.createBossBar("Charging up mana...", BarColor.PURPLE, BarStyle.SEGMENTED_10);
                player.playSound(player, Sound.BLOCK_PORTAL_TRIGGER, SoundCategory.BLOCKS, 1, 1);
                manaBossBar.setProgress(0.0);
                manaBossBar.addPlayer(player);
                for (int i = 0; i < 100; i++) {
                    startLocation.getWorld().spawnParticle(Particle.PORTAL, player.getLocation().add(0, 1.5, 0), 100);
                    target.getWorld().spawnParticle(Particle.PORTAL, target.clone().add(0, 1, 0), 100);

                    if (startLocation.distance(player.getLocation()) > 0) {
                        manaBossBar.removeAll();
                        player.sendMessage(Component.text("Teleportation interrupted.")
                                .style(Style.style(TextColor.color(0xFB5454))));
                        return;
                    }

                    manaBossBar.setProgress(i / 100.0);
                    try {
                        if (i % 10 == 0) {
                            Thread.sleep(200);
                        }
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                manaBossBar.removeAll();

                player.playSound(player, Sound.ENTITY_ENDER_DRAGON_FLAP, SoundCategory.BLOCKS, 1, 1);
                startLocation.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 500);


                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 2 * 20, 100, true, false, false));
                });

                for (int i = 0; i < 200; i++) {
                    startLocation.getWorld().spawnParticle(Particle.CLOUD, player.getLocation().add(0,1,0), 10);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) (teleportDuration) * 20, 1, true, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, (int) (teleportDuration - 1) * 20, 255,true, false, false));
                    player.teleport(player.getLocation().add(0, 500, 0));
                });

                BossBar teleportBossBar = Bukkit.createBossBar("Teleporting...", BarColor.PURPLE, BarStyle.SOLID);
                teleportBossBar.setProgress(0.0);
                teleportBossBar.addPlayer(player);
                for (int i = 0; i < 100; i++) {
                    teleportBossBar.setTitle("Teleporting... " + i + "%");
                    teleportBossBar.setProgress(i / 100.0);
                    target.getWorld().spawnParticle(Particle.SPELL_WITCH, target.clone().add(0,1,0), 300);
                    try {
                        Long millis = Math.round((1000L / 100L) * teleportDuration);
                        Thread.sleep(millis);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                teleportBossBar.removeAll();


                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    Location playerLocation = player.getLocation();
                    target.setPitch(playerLocation.getPitch());
                    target.setYaw(playerLocation.getYaw());
                    player.setFallDistance(0);
                    player.teleport(target);
                });
                player.playSound(player, Sound.BLOCK_PORTAL_TRAVEL, SoundCategory.BLOCKS, 1, 1);


                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                target.getWorld().spawnParticle(Particle.SPELL_WITCH, target.add(0,1.5,0), 300);

            }
        }.runTaskAsynchronously(plugin);

        // portal
        // poof
        player.removeScoreboardTag("teleporting");
    }

    public double getTeleportDuration() {
        return Math.ceil(getTeleportDistance() / 100.0);
    }

    public double getTeleportDistance() {
        return target.distance(player.getLocation());
    }
}
