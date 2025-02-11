import java.util.*;
/**
 * Write a description of class Hunter here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Hunter extends Actor
{
    /**
     * Constructor for objects of class Hunter
     */
    public Hunter(Location location)
    {
        super(location);
    }

    public boolean isActive() {
        return true;
    }
    
    public void act(Field currentField, Field nextFieldState) {
        List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(getLocation());

        // Move towards a source of food if found.
        Location nextLocation = null;
        if(nextLocation == null && ! freeLocations.isEmpty()) {
            // No food found - try to move to a free location.
            nextLocation = freeLocations.remove(0);
        }
        // See if it was possible to move.
        if(nextLocation != null) {
            setLocation(nextLocation);
            nextFieldState.placeActor(this, nextLocation);
        }
        else {
            // Overcrowding.
            //setDead();
        }
    }
}
