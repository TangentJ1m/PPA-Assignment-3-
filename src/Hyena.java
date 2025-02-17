import java.util.List;
import java.util.Iterator;
import java.util.Random;

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
    private static final int MAX_AGE = 20000;
    // The likelihood of a Hyena breeding.
    private static final double BREEDING_PROBABILITY = 0.03;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single Zebra. In effect, this is the
    // number of steps a Hyena can go before it has to eat again.
    // FIXME: Shouldn't this be in the Zebra class instead?
    private static final int ZEBRA_FOOD_VALUE = 250;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    

    // The Hyena's food level, which is increased by eating Zebras.
    private int foodLevel;

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
        foodLevel = rand.nextInt(ZEBRA_FOOD_VALUE);
        // TODO: Maybe this could be in Animal constructor
        setState(AnimalState.EATING);
    }
    
    /**
     * This is what the Hyena does most of the time: it hunts for
     * Zebras. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param currentField The field currently occupied.
     * @param nextFieldState The updated field.
     */
    public void act(Field currentField, Field nextFieldState, Environment env)
    {
        incrementAge();
        incrementHunger();
        updateState(env);
        switch (getState()) {
            case SLEEPING -> {
                // We are sleeping, so we don't do anything
                nextFieldState.placeActor(this, location);
            }
            case BREEDING -> {
                Location partnerLoc = currentField.findActor(location, 1, (a) -> {
                    if (a instanceof Hyena h) {
                        return h.getState() == AnimalState.BREEDING && h.getGender() != this.getGender();
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
            case EATING -> {
                // We want to eat, so we need to look for food
                Location foodLoc = currentField.findActor(location, 1,
                        (a) -> a.getClass().equals(Zebra.class) );

                Location nextLoc = null;
                if (foodLoc == null) {
                    // FIXME: generate a random adjacent location without constructing the list of all adjacencies
                    List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(location);
                    if (!freeLocations.isEmpty()) {
                        nextLoc = freeLocations.removeFirst();
                    }
                } else {
                    // Cast is safe as search predicate ensured this was a Zebra instance
                    Animal food = (Animal) currentField.getActorAt(foodLoc);
                    food.setDead();
                    foodLevel = ZEBRA_FOOD_VALUE;
                    nextLoc = foodLoc;
                }
                if (nextLoc != null) {
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
        } else if (foodLevel < 50) {
            setState(AnimalState.EATING);
        } else if (env.isNight()) {
            setState(AnimalState.SLEEPING);
        } else if (foodLevel > 200) {
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
                "age=" + age +
                ", active=" + isActive() +
                ", location=" + getLocation() +
                ", foodLevel=" + foodLevel +
                '}';
    }
    
    /**
     * Make this Hyena more hungry. This could result in the Hyena's death.
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
                    foodLevel = ZEBRA_FOOD_VALUE;
                    foodLocation = loc;
                }
            }
        }
        return foodLocation;
    }
    
    /**
     * Check whether this Hyena is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param freeLocations The locations that are free in the current field.
     */
    private void giveBirth(Field nextFieldState, List<Location> freeLocations)
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
     * A Hyena can breed if it has reached the breeding age.
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
