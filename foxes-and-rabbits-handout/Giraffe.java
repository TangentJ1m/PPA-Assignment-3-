import java.util.List;
import java.util.Random;

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
    private static final int MAX_AGE = 60;
    // The likelihood of a Giraffe breeding.
    private static final double BREEDING_PROBABILITY = 1;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    /**
     * Create a new Giraffe. A Giraffe may be created with age
     * zero (a new born) or with a random age.
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
    public void act(Field currentField, Field nextFieldState)
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
                "age=" + age +
                ", active=" + isActive() +
                ", location=" + getLocation() +
                '}';
    }
    
    /**)
     * Check whether or not this Giraffe is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
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
        return age >= BREEDING_AGE;
    }
    
    @Override
    protected int getMaxAge(){
        return MAX_AGE;
    }
}
