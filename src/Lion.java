import java.util.List;
import java.util.Random;
import java.util.Arrays;

/**
 * A simple model of a Lion.
 * Lions age, move, eat Zebras, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Lion extends Animal
{
    // Characteristics shared by all Lions (class variables).
    // The age at which a Lion can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a Lion can live.
    protected int getMaxAge() { return 1800; }
    // The likelihood of a Lion breeding.
    private static final double BREEDING_PROBABILITY = 0.02;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // How much "food" a lion gives when eaten
    protected int getFoodValue() { return -1; } // Shouldn't be eaten

    /**
     * Create a Lion. A Lion can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Lion will have random age and hunger level.
     * @param location The location within the field.
     */
    public Lion(boolean randomAge, Location location)
    {
        super(randomAge, location);
    }

    @Override
    public String toString() {
        return "Lion{" +
                "age=" + getAge() +
                ", active=" + isActive() +
                ", location=" + getLocation() +
                ", foodLevel=" + getFoodLevel() +
                '}';
    }
    
    /**
     * Check whether this Lion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New Lions are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Lion young = new Lion(false, loc);
                nextFieldState.placeActor(young, loc);
            }
        }
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births;
        if(rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A Lion can breed if it has reached the breeding age.
     */
    private boolean canBreed(Field field)
    {
        return getAge() >= BREEDING_AGE && isFemale() && isMaleNearby(field);
    }

    /**
     * Check if this animal can eat the given actor
     * @param actor the actor to check
     * @return true if this animal can eat `actor`
     */
    @Override
    protected boolean canEat(Actor actor)
    {
        return actor instanceof Giraffe || actor instanceof Zebra;
    }

    protected void updateState(Environment env) {
        if (!isActive()) {
            setState(AnimalState.DEAD);
        } else if (getFoodLevel() < 50) {
            setState(AnimalState.EATING);
        } else if (env.isNight()) {
            setState(AnimalState.SLEEPING);
        } else if (getFoodLevel() > 200) {
            setState(AnimalState.BREEDING);
        }
        // Special case: Lions wake up at the end of the night
        if (!env.isNight() && getState() == AnimalState.SLEEPING) {
            setState(AnimalState.EATING);
        }
    }
}


