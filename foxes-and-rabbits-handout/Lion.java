import java.util.List;
import java.util.Iterator;
import java.util.Random;

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
    private static final int MAX_AGE = 150;
    // The likelihood of a Lion breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single Zebra. In effect, this is the
    // number of steps a Lion can go before it has to eat again.
    private static final int Zebra_FOOD_VALUE = 9;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).

    // The Lion's food level, which is increased by eating Zebras.
    private int foodLevel;

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
        foodLevel = rand.nextInt(Zebra_FOOD_VALUE);
    }
    
    /**
     * This is what the Lion does most of the time: it hunts for
     * Zebras. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState)//Simulation stage class needed 
    {
        incrementAge();
        incrementHunger();
        if(isActive()) {
            List<Location> freeLocations =
                    nextFieldState.getFreeAdjacentLocations(getLocation());
            if(! freeLocations.isEmpty() && canBreed(currentField)) {
                giveBirth(nextFieldState, freeLocations);
            }
            // Move towards a source of food if found.
            Location nextLocation = findFood(currentField);
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
                setDead();
            }
        }
    }



    @Override
    public String toString() {
        return "Lion{" +
                "age=" + age +
                ", active=" + isActive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }
    
    /**
     * Make this Lion more hungry. This could result in the Lion's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for Zebras adjacent to the current location.
     * Only the first live Zebra is eaten.
     * @param field The field currently occupied.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field)
    {
        List<Location> adjacent = field.getAdjacentLocations(getLocation(),1);
        Iterator<Location> it = adjacent.iterator();
        Location foodLocation = null;
        while(foodLocation == null && it.hasNext()) {
            Location loc = it.next();
            Actor actor = field.getActorAt(loc);
            if(actor instanceof Zebra || actor instanceof Giraffe) {
                if(actor.isActive()) {
                    ((Animal)actor).setDead();
                    foodLevel = Zebra_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }
    
    /**
     * Check whether this Lion is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
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
        return age >= BREEDING_AGE && isFemale() && isMaleNearby(field);
    }
    
    @Override
    protected int getMaxAge(){
        return MAX_AGE;
    }
}
