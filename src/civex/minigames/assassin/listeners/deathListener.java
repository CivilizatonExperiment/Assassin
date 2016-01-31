package civex.minigames.assassin.listeners;

import civex.minigames.assassin.AssassinPlugin;
import civex.minigames.assassin.datastores.Assassin;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Created by Ryan on 1/23/2016.
 */
public class deathListener implements Listener
{
    private AssassinPlugin plugin;

    public deathListener(AssassinPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerDeathEvent(PlayerDeathEvent event)
    {
        Event eventType = event.getEntity().getLastDamageCause();

        if (eventType instanceof EntityDamageByEntityEvent)
        {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) eventType;

            if (e.getDamager() instanceof Player && e.getEntity() instanceof Player)
            {
                Player killer = (Player) e.getDamager();
                Player deceased = (Player) e.getEntity();

                if (plugin.getAssassin(killer) != null && plugin.getAssassin(deceased) != null)
                {
                    Assassin winner = plugin.getAssassin(killer);
                    Assassin loser = plugin.getAssassin(deceased);

                    if (shouldCount(winner, loser))
                    {
                        winner.killedAssasin(loser);
                    }

                    for (Player player : plugin.getOnlinePlayers())
                    {
                        if (player != killer && player != deceased)
                        {
                            sendDeathMessage(player);
                        }
                    }

                    event.setDeathMessage(null);
                }
            }
        }
    }

    public void sendDeathMessage(Player player)
    {
        plugin.messages.sendPlayerHasDied(player);
        player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1, 1);
    }

    public boolean shouldCount(Assassin a1, Assassin a2)
    {
        if (a1.getTarget() == a2.getPlayerUUID() || a1.getHunter() == a2.getPlayerUUID())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}