import java.util.List;
import java.util.Iterator;
import java.util.Random;
import java.util.Arrays;
/**
 * A simple model of a Hyena.
 * Hyenas age, move, eat Zebras, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Hyena extends Animal
{
    // Characteristics shared by all Hyenas (class variables).
    // The age at which a Hyena can start to breed.
    private static final int BREEDING_AGE = 15;
    // The age to which a Hyena can live.
    protected int getMaxAge() { return 20000; }
    // The likelihood of a Hyena breeding.
    private static final double BREEDING_PROBABILITY = 0.03;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // List of preys that a hyena can eat
    private static final List<Class<?>> PREY = Arrays.asList(Zebra.class, Giraffe.class);
    // The amount of "food" a hyena gives when eaten
    protected int getFoodValue() { return -1; } // Shouldn't be eaten

    /**
     * Create a Hyena. A Hyena can be created as a newborn (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the Hyena will have random age and hunger level.
     * @param location The location within the field.
     */
    public Hyena(boolean randomAge, Location location)
    {
        super(randomAge, location);
    }

    @Override
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
        // Special case: Hyenas wake up at the end of the night
        if (!env.isNight() && getState() == AnimalState.SLEEPING) {
            setState(AnimalState.EATING);
        }
    }

    @Override
    public String toString() {
        return "Hyena{" +
                "age=" + getAge() +
                ", active=" + isActive() +
                ", location=" + getLocation() +
                ", foodLevel=" + getFoodLevel() +
                '}';
    }

    /**
     * Check whether this Hyena is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New Hyenas are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && ! freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Hyena young = new Hyena(false, loc);
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
     * Check if this animal can eat the given actor
     * @param actor the actor to check
     * @return true if this animal can eat `actor`
     */
    @Override
    protected boolean canEat(Actor actor){
        return actor != null && PREY.contains(actor.getClass());
    }
}
