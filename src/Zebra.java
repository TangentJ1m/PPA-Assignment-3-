import java.util.List;
import java.util.Random;

/**
 * A simple model of a Zebra.
 * Zebras age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 7.1
 */
public class Zebra extends Animal
{
    // Characteristics shared by all Zebras (class variables).
    // The age at which a Zebra can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Zebra can live.
    protected int getMaxAge() { return 20000; }
    // The likelihood of a Zebra breeding.
    private static final double BREEDING_PROBABILITY = 0.01;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // How much "food" a zebra gives when eaten
    protected int getFoodValue() { return 100; }

    /**
     * Create a new Zebra. A Zebra may be created with age
     * zero (a newborn) or with a random age.
     * 
     * @param randomAge If true, the Zebra will have a random age.
     * @param location The location within the field.
     */
    public Zebra(boolean randomAge, Location location)
    {
        super(randomAge, location);
    }

    @Override
    protected void updateState(Environment env) {
        if (!isActive()) {
            setState(AnimalState.DEAD);
        } else if (env.isNight()) {
            setState(AnimalState.SLEEPING);
        } else {
            setState(AnimalState.BREEDING);
        }
    }

    @Override
    public String toString() {
        return "Zebra{" +
                "age=" + getAge() +
                ", alive=" + isActive() +
                ", location=" + getLocation() +
                '}';
    }
    
    /**
     * Check whether this Zebra is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New Zebras are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Zebra young = new Zebra(false, loc);
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
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        else {
            births = 0;
        }
        return births;
    }

    /**
     * A Zebra can breed if it has reached the breeding age.
     * @return true if the Zebra can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return getAge() >= BREEDING_AGE;
    }

    /**
     * Check if this animal can eat the given actor
     * @param actor the actor to check
     * @return true if this animal can eat `actor`
     */
    @Override
    protected boolean canEat(Actor actor)
    {
        // Doesn't eat anything (yet)
        return false;
    }
}
