package civex.minigames.assassin;

import civex.minigames.assassin.commands.AssassinCommand;
import civex.minigames.assassin.datastores.Assassin;
import civex.minigames.assassin.datastores.AssassinMessages;
import civex.minigames.assassin.datastores.AssassinSession;
import civex.minigames.assassin.datastores.State;
import civex.minigames.assassin.listeners.deathListener;
import civex.minigames.assassin.listeners.joinListener;

import com.google.gson.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ryan on 1/21/2016.
 */
public class AssassinPlugin extends JavaPlugin
{
    //part of backend file
    static final String JSON_NAME = "_assassin.json", FORMAT = "yyyy-MM-dd_HH-mm-ss";//Changing this will break shit (backwards compatibility);
    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(FORMAT);
    private static final String path = "./plugins/Assassin/";
    private File jsonFile = new File(path + JSON_NAME);
    private boolean newGame = false;

    public AssassinMessages messages;

    //part of game (read can be reset)
    public int MIN_PLAYER_COUNT = 2;
    public HashMap<UUID, Assassin> assassins = new HashMap<UUID, Assassin>();
    public State gameState = State.STARTING;

    @Override
    public void onEnable()
    {
        messages = new AssassinMessages(this);
        this.getConfig();
        tryToLoadFile();

        Bukkit.getPluginManager().registerEvents(new deathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new joinListener(this), this);
        Bukkit.getPluginCommand("assassin").setExecutor(new AssassinCommand(this));
    }

    @Override
    public void onDisable()
    {
        this.saveAssassins(jsonFile);
    }

    private void tryToLoadFile()
    {
        Date lastDate = null;
        boolean found = false;

        if (jsonFile.getParentFile().listFiles() == null)
        {
            newGame = true;
            createNewGame();
            return;
        }

        for (File file : jsonFile.getParentFile().listFiles())
        {
            if (!file.getName().endsWith(JSON_NAME))
            {
                continue;
            }
            Date date = getDate(file);
            if (date == null)
            {
                continue;
            }
            if (lastDate == null)
            {
                lastDate = date;
                jsonFile = file;
                found = true;
                continue;
            }
            if (date.after(lastDate))
            {
                lastDate = date;
                jsonFile = file;
                found = true;
            }
        }

        if (found)
        {
            loadAssassins(jsonFile);
        }
        else
        {
            newGame = true;
            createNewGame();
        }

    }

    private Date getDate(File file)
    {
        try
        {
            if (file.getName().length() < FORMAT.length())
            {
                return null;
            }
            return DATE_FORMAT.parse(file.getName().substring(0, FORMAT.length()));
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public void newFile()
    {
        String fileName = DATE_FORMAT.format(new Date()) + JSON_NAME;
        jsonFile = new File(path + fileName);
    }

    public void loadAssassins(File file)
    {
        try
        {
            Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            AssassinSession session = new Gson().fromJson(reader, AssassinSession.class);
            reader.close();

            assassins = session.assassins;
            gameState = session.gameState;
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public void saveAssassins(File file)
    {
        try
        {
            AssassinSession session = new AssassinSession();
            session.assassins = assassins;
            session.gameState = gameState;

            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            new GsonBuilder().setPrettyPrinting().create().toJson(session, writer);
            writer.flush();
            writer.close();
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public void addAssassins(Player... players)
    {
        for (Player player : players)
        {
            if (!assassins.containsKey(player.getUniqueId()))
            {
                Assassin temp = new Assassin(player, this);
                assassins.put(player.getUniqueId(), temp);
                messages.sendGameJoined(player);
            }
            else
            {
                messages.sendCantJoinTwice(player);
            }
        }
    }

    public void createNewGame()
    {
        if (!newGame)
        {
            //aka flush old file and then make a new one.
            saveAssassins(jsonFile);
            newFile();
        }
        else
        {
            newFile();
        }

        //clear vars
        assassins.clear();

        //this makes it so that people can join.
        gameState = State.STARTING;
    }

    public void setupGameForPlay()
    {
        gameState = State.INPROGRESS;

        ArrayList<Assassin> tempList = new ArrayList<Assassin>(this.assassins.values());

        //This makes the randomList so that we can make random targets for each player instead of going by w.e. order
        //java happens to keep it in.
        Collections.shuffle(tempList);

        //This sequentially sets the target of each player to the next one in the list and wraps around to the first one
        //when it gets to the end of the list. (This list is in a randomized order because of the above thing)

        for (int i = 0; i < tempList.size(); i++)
        {
            if (i != tempList.size() - 1)
            {
                tempList.get(i).setTarget(tempList.get(i + 1));
                tempList.get(i + 1).setHunter(tempList.get(i));
            }
            else
            {
                tempList.get(i).setTarget(tempList.get(0));
                tempList.get(0).setHunter(tempList.get(i));
            }
        }

        //making the list ready to go
        assassins.clear();
        for (Assassin player : tempList)
        {
            assassins.put(player.getPlayerUUID(), player);
        }

        announceGameStart();
    }

    public void endGame(Assassin winner)
    {
        for (Player player : getOnlinePlayers())
        {
            messages.sendEndOfGame(player, winner);
        }

        gameState = State.FINISHED;
        saveAssassins(jsonFile);
    }

    public void newTargetAssignment(Assassin winner, Assassin loser)
    {
        if (winner.getTarget() == loser.getPlayerUUID())
        {
            //attacker wins
            winner.setTarget(assassins.get(loser.getTarget()));
            assassins.put(winner.getPlayerUUID(), winner);
            if (getServer().getPlayer(winner.getPlayerUUID()) != null)
            {
                messages.sendTarget(getServer().getPlayer(winner.getPlayerUUID()));
            }
        }
        else if (loser.getTarget() == winner.getPlayerUUID())
        {
            //defender wins
            Assassin temp3rdParty = getAssassin(loser.getHunter());
            temp3rdParty.setTarget(winner);

            if (getServer().getPlayer(temp3rdParty.getPlayerUUID()) != null)
            {
                Player temp3rd = getServer().getPlayer(temp3rdParty.getPlayerUUID());
                messages.sendTargetHasDied(temp3rd);
                messages.sendTarget(temp3rd);
            }
            else
            {
                temp3rdParty.changeWhileOffline = true;
            }
        }
    }

    public Assassin getAssassin(Player player)
    {
        if (assassins.containsKey(player.getUniqueId()))
        {
            return assassins.get(player.getUniqueId());
        }
        else
        {
            return null;
        }
    }

    public Assassin getAssassin(UUID player)
    {
        if (assassins.containsKey(player))
        {
            return assassins.get(player);
        }
        else
        {
            return null;
        }
    }

    public Assassin getAssassin(String playerName)
    {
        //This should only be used if a player is issuing a command.
        for (Assassin assassin : assassins.values())
        {
            if (assassin.getPlayerName().equalsIgnoreCase(playerName))
            {
                return assassin;
            }
        }

        return null;
    }

    public ArrayList<String> getPlayersByAliveStatus(Boolean alive)
    {
        ArrayList<String> temp = new ArrayList<String>();

        for (Assassin player : assassins.values())
        {
            if (player.alive == alive)
            {
                if (!temp.contains(player.getPlayerName()))
                {
                    temp.add(player.getPlayerName());
                }
            }
        }

        return temp;
    }

    public void announceGameStart()
    {
        for (Player player : getOnlinePlayers())
        {
            messages.sendStartMessage(player);
            messages.sendTarget(player);
        }
    }

    public ArrayList<Player> getOnlinePlayers()
    {
        ArrayList<Player> output = new ArrayList<Player>();
        for (UUID player : assassins.keySet())
        {
            if (this.getServer().getPlayer(player) != null)
            {
                output.add(this.getServer().getPlayer(player));
            }
        }
        return output;
    }

}