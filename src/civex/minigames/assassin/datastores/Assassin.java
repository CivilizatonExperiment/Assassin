package civex.minigames.assassin.datastores;

import civex.minigames.assassin.AssassinPlugin;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Ryan on 1/21/2016.
 */
public class Assassin
{
    //Anything you don't want saved to json should have the "transient" keyword
    private UUID player;
    private String playerName;

    private UUID target;
    private String targetName;
    private int assignmentNumber = 1;

    private UUID hunter;
    private String hunterName;

    private HashMap<UUID, String> kills;

    public boolean alive;
    public boolean changeWhileOffline;

    private transient AssassinPlugin plugin;

    public Assassin(Player player, AssassinPlugin plugin)
    {
        this.plugin = plugin;
        this.player = player.getUniqueId();
        this.playerName = player.getName();
        this.kills = new HashMap<UUID, String>();
        this.alive = true;
        this.changeWhileOffline = false;
    }

    public UUID getPlayerUUID()
    {
        return player;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public void setTarget(Assassin target)
    {
        this.target = target.getPlayerUUID();
        this.targetName = target.getPlayerName();
    }

    public UUID getTarget()
    {
        return target;
    }

    public String getTargetName()
    {
        return targetName;
    }

    public void setHunter(Assassin hunter)
    {
        this.hunter = hunter.getPlayerUUID();
        this.hunterName = hunter.getPlayerName();
    }

    public UUID getHunter()
    {
        return hunter;
    }

    public String getHunterName()
    {
        return hunterName;
    }

    public void killedAssasin(Assassin loser)
    {
        loser.alive = false;
        if (!kills.containsKey(loser.getPlayerUUID()))
        {
            kills.put(loser.getPlayerUUID(), loser.getPlayerName());
        }
        incrmentAssignmentNumber();

        if (plugin.getPlayersByAliveStatus(true).size() != 1)
        {
            plugin.newTargetAssignment(this, loser);
        }
        else
        {
            plugin.endGame(this);
        }

    }

    public ArrayList<String> getKills()
    {
        ArrayList<String> temp = new ArrayList<String>();

        for (String kill : kills.values())
        {
            temp.add(kill);
        }

        return temp;
    }

    public int getAssignmentNumber()
    {
        return assignmentNumber;
    }

    public void incrmentAssignmentNumber()
    {
        assignmentNumber++;
    }

}