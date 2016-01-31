package civex.minigames.assassin.listeners;

import civex.minigames.assassin.AssassinPlugin;
import civex.minigames.assassin.datastores.Assassin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Ryan on 1/22/2016.
 */
public class joinListener implements Listener
{

    private AssassinPlugin plugin;

    public joinListener(AssassinPlugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoinEvent(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if (plugin.getAssassin(player) != null)
        {
            Assassin assassin = plugin.getAssassin(player);
            if (assassin.alive)
            {
                plugin.messages.sendJustLoggedIn(player);
            }

            if (assassin.changeWhileOffline)
            {
                plugin.messages.sendChangeWhileOffline(player);
            }
        }
    }

}