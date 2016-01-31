package civex.minigames.assassin.datastores;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Ryan on 1/23/2016.
 */
public class AssassinSession
{
    public HashMap<UUID, Assassin> assassins = new HashMap<UUID, Assassin>();
    public State gameState = State.STARTING;
}