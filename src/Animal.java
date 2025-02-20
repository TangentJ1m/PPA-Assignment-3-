import java.util.*;
/**
 * Common elements of animals 
 *
 * @author Tanjim Islam and Keiran Matthews
 * @version 7.0
 */


public abstract class Animal extends Actor {
    protected static final double GENDER_PROBABILITY = 0.5;
    private static final double DISEASE_LETHALITY = 0.2; // The chance a disease kills per step
    private static final double DISEASE_SPREAD = 0.1; // The chance a disease spreads per step
    private static final double NEW_DISEASE_CHANCE = 0.00001; // The chance a creature gets a new disease
    protected static final Random rand = Randomizer.getRandom();
    protected abstract int getMaxAge();
    protected abstract double getBreedingProbability();
    protected abstract int getMaxLitterSize();
    protected abstract int getBreedingAge();

    private final Gender gender;
    private int foodLevel;
    private int age;
    private AnimalState state;
    private boolean diseased;

    /**
     * Constructs a new Animal; called by subclasses instead of directly.
     * @param randomAge whether this animal should have a random age or be age 0
     * @param location the location to create this animal
     */
    public Animal(boolean randomAge, Location location) {
        super(location);
        this.gender = assignGender();
        age = 0;
        foodLevel = 24 * 14; // Can safely not eat for 2 weeks
        state = AnimalState.SLEEPING;
        diseased = false;
        if(randomAge) {
            age = rand.nextInt(getMaxAge());
            foodLevel = rand.nextInt(24*21);
        }
    }
    
    /**
     * Provides equal chance to the animal being male or female
     * @return Gender assigned to animal 
     */
    private Gender assignGender() {
        return new Random().nextDouble() < GENDER_PROBABILITY ? Gender.MALE : Gender.FEMALE;
    }

    /**
     * Indicate that the animal is no longer active.
     */
    protected void setDead()
    {
        setActive(false);
        location = null;
        state = AnimalState.DEAD;
    }

    /**
     * @return the gender of this Animal
     */
    protected Gender getGender() {
        return gender;
    }

    /**
     * @return the state of this Animal
     */
    protected AnimalState getState() {
        return state;
    }

    /**
     * Set the state of this Animal
     * @param state the new AnimalState
     */
    protected void setState(AnimalState state) {
        this.state = state;
    }

    /**
     * Increase the age.
     * This could result in the Animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }

    protected int getAge()
    {
        return age;
    }

    /**
     * Make the Animal more hungry.
     * This could result in the Animal's death
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    protected int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * Find if there is a suitable breeding partner in an adjacent cell
     * @param field the field of animals to check
     * @return true if there is a suitable breeding partner
     */
    protected boolean isPartnerNearby(Field field)
    {
        return field.findActor(getLocation(), 4, (actor) -> {
            if (actor.getClass().equals(this.getClass())) {
                Animal animal = (Animal) actor;
                return animal.gender != this.gender && animal.state == AnimalState.BREEDING;
            }
            return false;
        }) != null;
    }

    /**
     * Get the location of food in an adjacent cell
     * @param field the field to check
     * @return the location of suitable prey if there is any, else null
     */
    protected Location findFood(Field field)
    {
        return field.findActor(getLocation(), 1,
                (a) -> canEat(a) && a.isActive());
    }
    
        /**
     * Updates the disease status of the animal.
     * - Introduces disease randomly based on NEW_DISEASE_CHANCE.
     * - Spreads disease to nearby animals based on DISEASE_SPREAD probability.
     * - Can cause death based on DISEASE_LETHALITY.
     *
     * @param currentField The current state of the field.
     */
    private void updateDisease(Field currentField) {
        if (!diseased) {
            if (rand.nextDouble() < NEW_DISEASE_CHANCE) {
                diseased = true;
            }
        }
        else {
            List<Location> adjacent = currentField.getAdjacentLocations(location, 1);
            for (Location loc : adjacent) {
                Actor a = currentField.getActorAt(loc);
                if (a instanceof Animal animal) {
                    if (rand.nextDouble() < DISEASE_SPREAD) {
                        animal.diseased = true;
                    }
                }
            }
            if (rand.nextDouble() < DISEASE_LETHALITY) {
                setDead();
            }
        }
    }

    /**
     * Allows this Animal to act
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     * @param env The environment of the simulation
     */
    @Override
    public void act(Field currentField, Field nextFieldState, Environment env)
    {
        incrementAge();
        incrementHunger();
        updateDisease(currentField);
        updateState(env);
        switch(state) {
            case SLEEPING -> sleepAction(nextFieldState);
            case BREEDING -> breedAction(currentField, nextFieldState);
            case EATING -> eatAction(currentField, nextFieldState);
            case WANDERING -> wanderAction(currentField, nextFieldState);
            case EAT_AND_BREED -> eatAndBreed(currentField, nextFieldState);
            case DEAD -> setDead();
        }
    }

    /**
     * Performs both eating and breeding actions if conditions allow.
     * - Breeds if a partner is nearby and the probability is met.
     * - Eats if food is available.
     * - Moves to a new location or dies if no valid moves exist.
     *
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    protected void eatAndBreed(Field currentField, Field nextFieldState) {
        List<Location> freeLocations = nextFieldState.getFreeOrPlant(location, 1);
        if (isPartnerNearby(currentField) && gender == Gender.FEMALE && rand.nextDouble() <= getBreedingProbability()) {
            // New Animals are born into adjacent locations.
            // Get a list of adjacent free locations.
            int births = rand.nextInt(getMaxLitterSize()) + 1;
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.removeFirst();
                Animal young = giveBirth(loc);
                nextFieldState.placeActor(young, loc);
            }
        }
        // We want to eat, so we need to look for food
        Location foodLoc = findFood(currentField);

        Location nextLoc = null;
        Actor actor = currentField.getActorAt(foodLoc);
        if (!(actor instanceof Edible food)) {
            if (!freeLocations.isEmpty()) {
                nextLoc = freeLocations.removeFirst();
            }
        } else {
            food.eat();
            foodLevel = food.getFoodValue();
            nextLoc = foodLoc;
        }
        if (nextLoc != null) {
            setLocation(nextLoc);
            nextFieldState.placeActor(this, nextLoc);
        } else {
            setDead();
        }

    }
    
    /**
     * Handles the sleep action for the animal.
     * - If another animal enters its location, it attempts to move.
     * - If no free space is available, it dies due to overcrowding.
     *
     * @param nextFieldState The new state being built.
     */
    protected void sleepAction(Field nextFieldState)
    {
        // We are sleeping, so we don't do anything
        Location nextLocation = location;
        if (nextFieldState.getActorAt(location) != null) {
            // Someone has entered our square so we try to move (otherwise we effectively kill them)
            // Maybe we are pushed by the other animal out of our square
            List<Location> possibleLocs = nextFieldState.getFreeOrPlant(location, 1);
            if (!possibleLocs.isEmpty()) {
                nextLocation = possibleLocs.getFirst();
            }
            else {
                // Overcrowding
                setDead();
                return;
            }
        }
        nextFieldState.placeActor(this, nextLocation);
    }
    
    /**
     * Handles the breeding action for the animal.
     * - Checks for a partner nearby.
     * - If conditions allow, new animals are born.
     * - Moves to a new location or dies if overcrowded.
     *
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    protected void breedAction(Field currentField, Field nextFieldState)
    {
        List<Location> freeLocations = nextFieldState.getFreeOrPlant(location, 1);
        if (isPartnerNearby(currentField) && gender == Gender.FEMALE && rand.nextDouble() <= getBreedingProbability()) {
            // New Animals are born into adjacent locations.
            // Get a list of adjacent free locations.
            int births = rand.nextInt(getMaxLitterSize()) + 1;
            for (int b = 0; b < births && !freeLocations.isEmpty(); b++) {
                Location loc = freeLocations.removeFirst();
                Animal young = giveBirth(loc);
                nextFieldState.placeActor(young, loc);
            }
        }
        if (!freeLocations.isEmpty()) {
            Location nextLoc = freeLocations.removeFirst();
            setLocation(nextLoc);
            nextFieldState.placeActor(this, nextLoc);
        } else {
            // Overcrowding
            setDead();
        }
    }

    protected void eatAction(Field currentField, Field nextFieldState)
    {
        // We want to eat, so we need to look for food
        Location foodLoc = findFood(currentField);

        Location nextLoc = null;
        Actor actor = currentField.getActorAt(foodLoc);
        if (!(actor instanceof Edible food)) {
            // FIXME: generate a random adjacent location without constructing the list of all adjacencies
            List<Location> freeLocations = nextFieldState.getFreeOrPlant(location, 1);
            if (!freeLocations.isEmpty()) {
                nextLoc = freeLocations.removeFirst();
            }
        } else {
            food.eat();
            foodLevel = food.getFoodValue();
            nextLoc = foodLoc;
        }
        if (nextLoc != null) {
            setLocation(nextLoc);
            nextFieldState.placeActor(this, nextLoc);
        } else {
            setDead();
        }
    }
    
        /**
     * Handles the wandering behavior of the animal.
     * - Moves randomly to a free adjacent location.
     * - Dies if no free locations are available.
     *
     * @param currentField The current state of the field.
     * @param nextFieldState The new state being built.
     */
    protected void wanderAction(Field currentField, Field nextFieldState) {
        List<Location> freeLocations = nextFieldState.getFreeOrPlant(location, 1);
        if (!freeLocations.isEmpty()) {
            Location nextLoc = freeLocations.removeFirst();
            setLocation(nextLoc);
            nextFieldState.placeActor(this, nextLoc);
        } else {
            setDead();
        }

    }

    protected boolean canBreed() {
        return age > getBreedingAge();
    }

    /**
     * Updates the state of the animal
     * @param env The environment of the simulation
     */
    abstract void updateState(Environment env);

    /**
     * Check if this animal can eat the given actor
     * @param actor the actor to check
     * @return true if this animal can eat `actor`
     */
    abstract boolean canEat(Actor actor);

    /**
     * Create a newborn version of this animal
     * @param loc the location for the new animal
     */
    protected abstract Animal giveBirth(Location loc);
}