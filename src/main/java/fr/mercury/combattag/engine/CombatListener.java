package fr.mercury.combattag.engine;

import com.google.common.collect.ImmutableSet;
import fr.mercury.combattag.MercuryTag;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Set;

public class CombatListener implements Listener {

    private FileConfiguration config = MercuryTag.getInstance().getConfig();

    private CombatManager combatManager = CombatManager.getInstance();
    private final Set<PotionEffectType> harmfulEffects = ImmutableSet.of(
            PotionEffectType.BLINDNESS,
            PotionEffectType.CONFUSION,
            PotionEffectType.HARM,
            PotionEffectType.HUNGER,
            PotionEffectType.POISON,
            PotionEffectType.SLOW,
            PotionEffectType.SLOW_DIGGING,
            PotionEffectType.WEAKNESS,
            PotionEffectType.WITHER
    );

    @EventHandler
    public void onKick(PlayerKickEvent event) {

        Player player = event.getPlayer();

        if(this.combatManager.isInCombat(player) && this.config.getBoolean("EVENTS.DISABLE_TAG_ON_KICK"))
            this.combatManager.remove(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent event) {

        if(event.getEntity() instanceof Player && event.getDamager() instanceof Projectile) {
            Player damaged = (Player) event.getEntity();
            Entity damager = event.getDamager();

            if(damager.getType() == EntityType.ENDER_PEARL)
                return;

            if(damager.getType() == EntityType.FISHING_HOOK)
                return;

            this.combatManager.setCombat(damaged);
            return;
        }

        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player && !event.isCancelled()) {

            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            this.combatManager.setCombat(damaged);
            this.combatManager.setCombat(damager);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void tagPlayer(PotionSplashEvent event) {

        ProjectileSource source = event.getEntity().getShooter();
        if (!(source instanceof Player)) return;

        if(!config.getBoolean("EVENTS.TAG_IF_POTION_AFFECT")) return;

        Player attacker = (Player) source;
        boolean isHarmful = false;

        for (PotionEffect effect : event.getPotion().getEffects()) {
            if (harmfulEffects.contains(effect.getType())) {
                isHarmful = true;
                break;
            }
        }

        // Ne rien faire si la potion n'a pas d'effet néfaste.
        if (!isHarmful) return;

        // Tag les joueurs qui sont affectés par la potion.
        for (LivingEntity entity : event.getAffectedEntities()) {
            if (!(entity instanceof Player)) continue;

            Player victim = (Player) entity;
            if (victim == attacker) continue;

                this.combatManager.setCombat(((Player) entity).getPlayer());
            }

        this.combatManager.setCombat(attacker);

    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {

        Projectile entity = event.getEntity();

        if (entity.getType() != EntityType.ENDER_PEARL) return;

        if (!(entity.getShooter() instanceof Player)) return;

    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        String command = event.getMessage();

        if(!this.combatManager.canExecute(player, command)) {

            event.setCancelled(true);
            player.sendMessage(this.config.getString("COMBAT.CANT_EXECUTE_COMMAND"));

        } else
            event.setCancelled(false);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {

        Player player = event.getPlayer();

        if(this.combatManager.isInCombat(player)) {

            player.setHealth(0);

            if(config.getBoolean("EVENTS.ENABLE_DEATHMESSAGE_ON_QUIT_EVENT"))
            Bukkit.broadcastMessage(this.config.getString("EVENTS.BROADCAST_MESSAGE_ON_QUIT").replace("%player%", player.getName()));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();

        String deathMessage = killer != null ? this.config.getString("EVENTS.DEATHMESSAGE_DUO").replace("%death%", player.getName()).replace("%killer%", killer.getName()) : "§7" + this.config.getString("EVENTS.DEATHMESSAGE_SOLO").replace("%death%", player.getName());

        event.setDeathMessage(null);

        if(this.config.getBoolean("EVENTS.ENABLE_DEATHMESSAGE"))
        Bukkit.broadcastMessage(deathMessage);

        if(this.combatManager.isInCombat(player)) {

            this.combatManager.remove(player);
        }
    }


}
