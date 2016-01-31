package civex.minigames.assassin.datastores;

import civex.minigames.assassin.AssassinPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Ryan on 1/23/2016.
 */
public class AssassinMessages
{
    private AssassinPlugin plugin;

    public AssassinMessages(AssassinPlugin plugin)
    {
        this.plugin = plugin;
    }

    /* These are message types and changing these will effect all of the other messages */

    private void sendMessage(Player player, String message, ChatColor color)
    {
        String prefix = "[Assassin]: ";
        player.sendMessage(color + prefix + message);
    }

    private void sendErrorMessage(Player player, String message)
    {
        sendMessage(player, message, ChatColor.RED);
    }

    private void sendGoldMessage(Player player, String message)
    {
        sendMessage(player, message, ChatColor.GOLD);
    }

    private void sendGreenMessage(Player player, String message)
    {
        sendMessage(player, message, ChatColor.GREEN);
    }

    private void sendAquaMessage(Player player, String message)
    {
        sendMessage(player, message, ChatColor.AQUA);
    }
    /* END OF MESSAGE TYPES */


    /* These are for messages while joining */
    public void sendNoGameInProgress(Player player)
    {
        sendErrorMessage(player, "Unfortunately there no game to join please try again later.");
    }

    public void sendGameAlreadyInProgress(Player player)
    {
        sendErrorMessage(player, "You're not allowed to join a game that is already started.");
    }

    public void sendCantJoinTwice(Player player)
    {
        sendErrorMessage(player, "You've already entered into the game and can not join a second time.");
    }

    public void sendGameJoined(Player player)
    {
        sendGreenMessage(player, "You've been entered into list of players that are going to play in the Assassin game.");
    }
    /* END OF JOINING MESSAGES */


    /* These are player status commands */
    public void sendPlayersInQueue(Player player, String players)
    {
        sendAquaMessage(player, "These are the players that are currently signed up for the next game.");
        sendAquaMessage(player, players);
    }

    public void sendPlayerInfoAll(Player player, String alive, String dead)
    {
        sendAquaMessage(player, "These players are " + ChatColor.GREEN + "alive" + ChatColor.AQUA + ", " + alive);
        sendAquaMessage(player, "These players are " + ChatColor.RED + "dead" + ChatColor.AQUA + ", " + dead);
    }

    public void sendPlayerHasDied(Player player)
    {
        sendAquaMessage(player, "A chill runs down your spine.");

    }

    public void sendTargetHasDied(Player player)
    {
        sendAquaMessage(player, "Sources tell you that your target has been taken care of by someone else.");
    }
    /* END OF STATUS COMMANDS */

    /* In game messages (status updates and the like) */
    public void sendChangeWhileOffline(Player player)
    {
        sendAquaMessage(player, "While you were unreachable sources tell you that your target has been taken care of by someone else.");
        sendTarget(player);
    }

    public void sendJustLoggedIn(Player player)
    {
        sendGoldMessage(player, "Do not forget that you're part of the Assassin game to find out your target type /assassin target");
    }

    public void sendStartMessage(Player player)
    {
        sendGoldMessage(player, "==========================================");
        sendGoldMessage(player, "The Assassin game has begun you have been assigned your first target.");
        sendGoldMessage(player, "==========================================");
    }

    public void sendTarget(Player player)
    {
        Assassin temp = plugin.getAssassin(player);
        sendAquaMessage(player, "Your " + ordinal(temp.getAssignmentNumber()) + " assignment is to assassinate " + temp.getTargetName() + ".");
    }

    public void sendTargetErrorNotInGame(Player player)
    {
        sendErrorMessage(player, "You appear to not be in this game. Please join the next game.");
    }

    public void sendTargetErrorDead(Player player)
    {
        sendErrorMessage(player, "You've been assassinated so you are out of the game and as such you do not have a target.");
    }

    public void sendTargetErrorGameOver(Player player)
    {
        sendErrorMessage(player, "The game is over so you do not have a target.");
    }

    public void sendTargetErrorGameNotStarted(Player player)
    {
        sendErrorMessage(player, "The game has not started yet please wait for the game to start and you will get a notification that the game has started.");
    }

    public void sendNoKills(Player player)
    {
        sendErrorMessage(player, "You currently have no kills, you should go out and get some.");
    }

    public void sendKills(Player player, int numKills, String killString)
    {
        sendAquaMessage(player, "You currently have " + ChatColor.GREEN + numKills + ChatColor.AQUA + "assassinations.");
        sendAquaMessage(player, "These are the players you've assassinated; " + killString + ".");
    }

    public void sendEndOfGame(Player player, Assassin winner)
    {
        sendGoldMessage(player, ChatColor.WHITE + winner.getPlayerName() + ChatColor.GOLD + " has won the game. We hope you will join us next time.");
    }
    /* END OF IN GAME MESSAGE */


    /* These are for leave / DQed (force leave) */
    public void sendDisqualifiedMessage(Player player)
    {
        sendErrorMessage(player, "You've been disqualified from the game of Assassin, if you have any questions please speak to the event coordinator.");
    }

    public void sendSurrenderMessage(Player player)
    {
        sendGoldMessage(player, "You've surrendered your life to your hunter so in terms of the game you've been killed.");
    }
    /* END OF LEAVE / DQ */


    /* There are messages that are only sent because of admin commands */
    public void sendAdminDQedPlayer(Player player)
    {
        sendGreenMessage(player, "Player has been disqualified.");
    }

    public void sendAdminCantEndBeforeGame(Player player)
    {
        sendErrorMessage(player, "You can not end a game before it has started.");
    }

    public void sendAdminCantEndTwice(Player player)
    {
        sendErrorMessage(player, "You can not end a game twice.");
    }

    public void sendAdminCantStartWhileInprogress(Player player)
    {
        sendErrorMessage(player, "You can not start a new game while there is a game in progress.");
    }

    public void sendAdminCantStartAnonExistentGame(Player player)
    {
        sendErrorMessage(player, "You can not start a game that isn't created (please create a new game).");
    }

    public void sendAdminNotEnoughPlayers(Player player)
    {
        sendErrorMessage(player, "You do not have enough players to start the game, the minimum amount is "
                + ChatColor.WHITE + plugin.MIN_PLAYER_COUNT + ChatColor.RED + " so please get that amount before starting.");
    }

    public void sendAdminCantCreateNewGameWhileInprogress(Player player)
    {
        sendErrorMessage(player, "You can not create a new game while there is a game in progress.");
    }

    public void sendAdminAlreadyCreating(Player player)
    {
        sendErrorMessage(player, "You're already in a new game (aka you're already starting a new game).");
    }

    public void sendNoPermission(Player player)
    {
        sendErrorMessage(player, "You do not have permission to use this command.");
    }
    /* END OF ADMIN MESSAGES */


    public void sendHelp(Player player)
    {
        player.sendMessage(ChatColor.GRAY + "The following is how to use the /assassin command. All of the follow should be preceded by the base command (ex. /assassin help)");
        player.sendMessage(ChatColor.GRAY + "    join : If there is a joinable game it will add you to the players.");
        player.sendMessage(ChatColor.GRAY + "    target : It will show you your current target.");
        player.sendMessage(ChatColor.GRAY + "    leave : This will remove you from the current game and grant your hunter the kill against you");
        player.sendMessage(ChatColor.GRAY + "    help : Brings up this help menu.");
        player.sendMessage(ChatColor.GRAY + "    list players : Shows you the people that are currently signed up and or playing the game.");
        player.sendMessage(ChatColor.GRAY + "    list kills : Shows you the people that you have killed.");

        if (player.hasPermission("civex.assassin.admin"))
        {
            player.sendMessage(ChatColor.GRAY + "    admin create : Starts a new game (only available after last game has ended)");
            player.sendMessage(ChatColor.GRAY + "    admin start : This starts the killing part of the game");
            player.sendMessage(ChatColor.GRAY + "    admin end : This ends the killing part of the game incase someone hides from the other");
            player.sendMessage(ChatColor.GRAY + "    admin dq : This will disqualify the player and is the same as if they used the leave command them selves");
        }
    }

    public void sendNotEnoughArguments(Player player)
    {
        sendErrorMessage(player, "You did not put in enough arguments please use /assassin help if you need more help.");
    }

    public void genericError(Player player)
    {
        sendErrorMessage(player, "Something went horribly wrong please talk to the organizer of the event.");
    }

    public void sendCantBeConsole(CommandSender sender)
    {
        sender.sendMessage(ChatColor.RED + "You can not do this command from the console please log in and issue the command.");
    }

    //got this from some stackoverflow thread a long long time ago.
    public static String ordinal(int i)
    {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100)
        {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];
        }
    }
}