import java.util.List;
import java.util.Random;
import java.util.Arrays;
/**
 * A simple model of a Giraffe.
 * Giraffes age, move, breed, and die.
 * 
 * @author Tanjim & Keiran
 * @version 1.0.0
 */
public class Giraffe extends Animal
{
    // Characteristics shared by all Giraffes (class variables).
    // The age at which a Giraffe can start to breed.
    private static final int BREEDING_AGE = 10;
    // The age to which a Giraffe can live.
    protected int getMaxAge() { return 60; }
    // The likelihood of a Giraffe breeding.
    private static final double BREEDING_PROBABILITY = 0.00;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The amount of "food" a giraffe gives when eaten
    protected int getFoodValue() { return -1; } // FIXME: Should be eaten?

    /**
     * Create a new Giraffe. A Giraffe may be created with age
     * zero (a newborn) or with a random age.
     * 
     * @param randomAge If true, the Giraffe will have a random age.
     * @param location The location within the field.
     */
    public Giraffe(boolean randomAge, Location location)
    {
        super(randomAge, location);
    }
    
    /**
     * This is what the Giraffe does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     * @param currentField The field occupied.
     * @param nextFieldState The updated field.
     */
    // FIXME: env is unused
    public void act(Field currentField, Field nextFieldState, Environment env)
    {
        incrementAge();
        if(isActive()) {
            List<Location> freeLocations = 
                nextFieldState.getFreeAdjacentLocations(getLocation());
            if(!freeLocations.isEmpty()) {
                giveBirth(nextFieldState, freeLocations);
            }
            // Try to move into a free location.
            if(! freeLocations.isEmpty()) {
                Location nextLocation = freeLocations.get(0);
                setLocation(nextLocation);
                nextFieldState.placeActor(this, nextLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    @Override
    public String toString() {
        return "Giraffe{" +
                "age=" + getAge() +
                ", active=" + isActive() +
                ", location=" + getLocation() +
                '}';
    }
    
    /**
     * Check whether this Giraffe is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    protected void giveBirth(Field nextFieldState, List<Location> freeLocations)
    {
        // New Giraffes are born into adjacent locations.
        // Get a list of adjacent free locations.
        int births = breed();
        if(births > 0) {
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.remove(0);
                Giraffe young = new Giraffe(false, loc);
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
     * A Giraffe can breed if it has reached the breeding age.
     * @return true if the Giraffe can breed, false otherwise.
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
    protected boolean canEat(Actor actor) {
        // Doesn't eat anything (yet)
        return false;
    }

    protected void updateState(Environment env) {}
}
