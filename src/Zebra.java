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
    private static final int MAX_AGE = 20000;
    // The likelihood of a Zebra breeding.
    private static final double BREEDING_PROBABILITY = 0.01;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
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
        foodValue = 100;
    }

    public void act(Field currentField, Field nextFieldState, Environment env)
    {
        incrementAge();
        updateState(env);
        switch (getState()) {
            case SLEEPING -> {
                // We are sleeping, so we don't do anything
                nextFieldState.placeActor(this, location);
            }
            case BREEDING -> {
                Location partnerLoc = currentField.findActor(location, 1, (a) -> {
                    if (a instanceof Zebra z) {
                        return z.getState() == AnimalState.BREEDING && z.getGender() != this.getGender();
                    }
                    return false;
                });

                List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(location);

                if (partnerLoc != null) {
                    // If we found a partner then we can breed
                    if (this.getGender() == Gender.FEMALE) {
                        giveBirth(nextFieldState, freeLocations);
                    }
                }

                if (!freeLocations.isEmpty()) {
                    Location nextLoc = freeLocations.removeFirst();
                    setLocation(nextLoc);
                    nextFieldState.placeActor(this, nextLoc);
                } else {
                    setDead();
                }
            }
        }
    }

    private void updateState(Environment env) {
        if (!isActive()) {
            setState(AnimalState.DEAD);
        } else if (env.isNight()) {
            setState(AnimalState.SLEEPING);
        } else {
            setState(AnimalState.BREEDING);
        }
    }

//    /**
//     * This is what the Zebra does most of the time - it runs
//     * around. Sometimes it will breed or die of old age.
//     * @param currentField The field occupied.
//     * @param nextFieldState The updated field.
//     */
//    public void act(Field currentField, Field nextFieldState, Environment env)
//    {
//        incrementAge();
//        if(isActive()) {
//            List<Location> freeLocations =
//                nextFieldState.getFreeAdjacentLocations(getLocation());
//            if(!freeLocations.isEmpty()) {
//                giveBirth(nextFieldState, freeLocations);
//            }
//            // Try to move into a free location.
//            if(! freeLocations.isEmpty()) {
//                Location nextLocation = freeLocations.get(0);
//                setLocation(nextLocation);
//                nextFieldState.placeActor(this, nextLocation);
//            }
//            else {
//                // Overcrowding.
//                setDead();
//            }
//        }
//    }

    @Override
    public String toString() {
        return "Zebra{" +
                "age=" + age +
                ", alive=" + isActive() +
                ", location=" + getLocation() +
                '}';
    }
    
    /**
     * Check whether this Zebra is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
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
        return age >= BREEDING_AGE;
    }
    
    @Override
    protected int getMaxAge(){
        return MAX_AGE;
    }
    
    protected boolean isPrey(Actor actor){
        return true;
    }
}
