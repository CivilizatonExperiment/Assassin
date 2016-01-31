package civex.minigames.assassin.commands;

import civex.minigames.assassin.AssassinPlugin;
import civex.minigames.assassin.datastores.Assassin;
import civex.minigames.assassin.datastores.State;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by Ryan on 1/21/2016.
 */
public class AssassinCommand implements CommandExecutor
{

    private AssassinPlugin plugin;

    public AssassinCommand(AssassinPlugin plugin)
    {
        this.plugin = plugin;
    }
        /*
       * Command Stucture
       * basic command sends help
       * join               joins a game during init stage
       * target             shows who player is hunting
       * leave              leaves the game
       * help               sends help message
       * list players       shows if dead or not
       * list kills         that persons kills
       * admin create       inits the create game
       * admin start        starts game
       * admin end          ends game incase people can't find each other
       * admin dq		    disqualifiys a player
     */

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            plugin.messages.sendCantBeConsole(sender);
            return true;
        }

        Player player = ((Player) sender).getPlayer();

        if (args.length < 1)
        {
            plugin.messages.sendHelp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("help"))
        {
            plugin.messages.sendHelp(player);
        }

        if (args[0].equalsIgnoreCase("join"))
        {
            if (plugin.gameState == State.STARTING)
            {
                if (plugin.getAssassin(player) == null)
                {
                    plugin.addAssassins(player);
                }
                else
                {
                    plugin.messages.sendCantJoinTwice(player);
                }
                return true;
            }
            else
            {
                sendNotJoinable(player);
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("target"))
        {
            if (plugin.gameState == State.STARTING)
            {
                plugin.messages.sendTargetErrorGameNotStarted(player);
                return true;
            }

            if (plugin.gameState == State.FINISHED)
            {
                plugin.messages.sendTargetErrorGameOver(player);
                return true;
            }

            if (plugin.getAssassin(player) != null)
            {
                Assassin temp = plugin.getAssassin(player);

                if (temp.alive)
                {
                    if (temp.getTargetName() != null)
                    {
                        plugin.messages.sendTarget(player);
                        return true;
                    }
                    else
                    {
                        plugin.messages.genericError(player);
                        return true;
                    }
                }
                else
                {
                    plugin.messages.sendTargetErrorDead(player);
                    return true;
                }
            }
            else
            {
                plugin.messages.sendTargetErrorNotInGame(player);
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("leave"))
        {
            playerLeave(plugin.getAssassin(player), false);
            return true;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            if(!(args.length > 1)){
                plugin.messages.sendNotEnoughArguments(player);
                return true;
            }

            if (args[1].equalsIgnoreCase("players"))
            {
                if (plugin.gameState == State.INPROGRESS)
                {
                    ArrayList<String> alivePlayers = plugin.getPlayersByAliveStatus(true);
                    ArrayList<String> deadPlayers = plugin.getPlayersByAliveStatus(false);

                    String alive = getStringOfPlayers(alivePlayers);
                    String dead = getStringOfPlayers(deadPlayers);

                    plugin.messages.sendPlayerInfoAll(player, alive, dead);

                    return true;
                }
                else if (plugin.gameState == State.STARTING)
                {
                    ArrayList<String> players = plugin.getPlayersByAliveStatus(true);
                    String temp = getStringOfPlayers(players);
                    plugin.messages.sendPlayersInQueue(player, temp);
                    return true;
                }
                else
                {
                    ArrayList<String> winner = plugin.getPlayersByAliveStatus(true);
                    ArrayList<String> losers = plugin.getPlayersByAliveStatus(false);
                    String temp1 = getStringOfPlayers(winner);
                    String temp2 = getStringOfPlayers(losers);
                    plugin.messages.sendPlayerInfoAll(player, temp1, temp2);
                    return true;
                }

            }

            if (args[1].equalsIgnoreCase("kills"))
            {
                Assassin temp = plugin.getAssassin(player);
                ArrayList<String> tempList = temp.getKills();
                if (tempList.size() > 0)
                {
                    String output = getStringOfPlayers(tempList);
                    plugin.messages.sendKills(player, tempList.size(), output);
                    return true;
                }
                else
                {
                    plugin.messages.sendNoKills(player);
                    return true;
                }
            }

            plugin.messages.sendHelp(player);
            return true;

        }

        if (args[0].equalsIgnoreCase("admin"))
        {
            if(!(args.length > 1))
            {
                plugin.messages.sendNotEnoughArguments(player);
                return true;
            }

            if (!player.hasPermission("civex.minigames.assassin.admin"))
            {
                plugin.messages.sendNoPermission(player);
                return true;
            }

            if (args[1].equalsIgnoreCase("create"))
            {
                //Cancel if wrong state.
                if (plugin.gameState != State.FINISHED)
                {
                    switch (plugin.gameState)
                    {
                        case STARTING:
                            plugin.messages.sendAdminAlreadyCreating(player);
                            break;
                        case INPROGRESS:
                            plugin.messages.sendAdminCantCreateNewGameWhileInprogress(player);
                            break;
                    }
                    return true;
                }

                //call create new game
                plugin.createNewGame();
                return true;
            }

            if (args[1].equalsIgnoreCase("start"))
            {
                if (plugin.assassins.size() < plugin.MIN_PLAYER_COUNT)
                {
                    plugin.messages.sendAdminNotEnoughPlayers(player);
                    return true;
                }

                if (plugin.gameState != State.STARTING)
                {
                    switch (plugin.gameState)
                    {
                        case FINISHED:
                            plugin.messages.sendAdminCantStartAnonExistentGame(player);
                            break;
                        case INPROGRESS:
                            plugin.messages.sendAdminCantStartWhileInprogress(player);
                            break;
                    }
                    return true;
                }

                plugin.setupGameForPlay();
                return true;
            }

            if (args[1].equalsIgnoreCase("end"))
            {
                if (plugin.gameState != State.INPROGRESS)
                {
                    switch (plugin.gameState)
                    {
                        case FINISHED:
                            plugin.messages.sendAdminCantEndTwice(player);
                            break;
                        case STARTING:
                            plugin.messages.sendAdminCantEndBeforeGame(player);
                            break;
                    }
                    return true;
                }

                plugin.gameState = State.FINISHED;
                return true;
            }

            if (args[1].equalsIgnoreCase("dq"))
            {
                if (args.length == 3)
                {
                    String playerName = args[2];
                    Assassin temp = plugin.getAssassin(playerName);
                    playerLeave(temp, true);
                    plugin.messages.sendAdminDQedPlayer(player);
                    return true;
                }
                else
                {
                    plugin.messages.sendNotEnoughArguments(player);
                    return true;
                }
            }

            plugin.messages.sendHelp(player);
            return true;
        }
        else
        {
            plugin.messages.sendHelp(player);
            return true;
        }
    }

    public void playerLeave(Assassin player, boolean DQed)
    {
        Assassin leaver = player;
        Assassin hunter = plugin.getAssassin(leaver.getHunter());

        hunter.killedAssasin(leaver);

        if (plugin.getServer().getPlayer(player.getPlayerUUID()) != null)
        {
            Player temp = plugin.getServer().getPlayer(player.getPlayerUUID());
            if (DQed)
            {
                plugin.messages.sendDisqualifiedMessage(temp);
            }
            else
            {
                plugin.messages.sendSurrenderMessage(temp);
            }
        }
    }

    public void sendNotJoinable(Player player)
    {
        if (plugin.gameState == State.FINISHED)
        {
            plugin.messages.sendNoGameInProgress(player);
        }
        else if (plugin.gameState == State.INPROGRESS)
        {
            plugin.messages.sendGameAlreadyInProgress(player);
        }
    }

    public String getStringOfPlayers(ArrayList<String> players)
    {
        if (players.size() > 0)
        {
            String output = "";
            for (String player : players)
            {
                output += player + ", ";
            }

            output = output.substring(0, output.length() - 2);
            return output;
        }
        else
        {
            return null;
        }
    }
}