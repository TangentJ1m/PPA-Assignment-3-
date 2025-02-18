import java.util.*;
/**
 * Common elements of animals 
 *
 * @author Tanjim Islam
 * @version 7.0
 */


public abstract class Animal extends Actor {
    protected static final double GENDER_PROBABILITY = 0.5;
    protected static final Random rand = Randomizer.getRandom();
    protected abstract int getMaxAge();
    protected abstract int getFoodValue();

    private Gender gender;
    private int foodLevel;
    private int age;
    private AnimalState state;

    /**
     * Constructs a new Animal; called by subclasses instead of directly.
     * @param randomAge whether this animal should have a random age or be age 0
     * @param location the location to create this animal
     */
    public Animal(boolean randomAge, Location location) {
        super(location);
        this.gender = assignGender();
        age = 0;
        // FIXME: Set to a reasonable random value
        // FIXME: Not every animal should have hunger with the current implementation
        foodLevel = 540;
        state = AnimalState.SLEEPING;
        if(randomAge) {
            age = rand.nextInt(getMaxAge());
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
    }

    /**
     * @return true if the Animal is male
     */
    protected boolean isMale() {
        return gender == Gender.MALE;
    }

    /**
     * @return true if the Animal is female
     */
    protected boolean isFemale() {
        return gender == Gender.FEMALE;
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
     * Get if there is a male Animal of the same subtype in an adjacent cell
     * @param field the field of animals to check
     * @return true if there is a male Animal of the same subtype in an adjacent cell
     */
    protected boolean isMaleNearby(Field field)
    {
        return field.findActor(getLocation(), 1,
                (a) -> a.getClass().equals(this.getClass()) && ((Animal) a).isMale())
                != null;
    }

    /**
     * Get the location of prey in an adjacent cell
     * @param field the field to check
     * @return the location of suitable prey if there is any, else null
     */
    protected Location findFood(Field field)
    {
        return field.findActor(getLocation(), 1,
                (a) -> canEat(a) && a.isActive());
    }

    /**
     * Get the location of a suitable breeding partner (same Animal subtype, opposite gender, in BREEDING state)
     * @param field the field to check
     * @return the location of a suitable breeding partner, or null
     */
    protected Location findPartner(Field field)
    {
        return field.findActor(getLocation(), 1,
                (a) -> a.getClass().equals(this.getClass()) &&
                        ((Animal) a).getGender() != this.getGender() &&
                        ((Animal) a).state == AnimalState.BREEDING);
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
        updateState(env);
        switch(state) {
            case SLEEPING -> sleepAction(nextFieldState);
            case BREEDING -> breedAction(currentField, nextFieldState);
            case EATING -> eatAction(currentField, nextFieldState);
            case DEAD -> setDead();
        }
    }

    protected void sleepAction(Field nextFieldState)
    {
        // We are sleeping, so we don't do anything
        nextFieldState.placeActor(this, location);
    }

    protected void breedAction(Field currentField, Field nextFieldState)
    {
        Location partnerLoc = findPartner(currentField);
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

    protected void eatAction(Field currentField, Field nextFieldState)
    {
        // We want to eat, so we need to look for food
        Location foodLoc = findFood(currentField);

        Location nextLoc = null;
        if (foodLoc == null) {
            // FIXME: generate a random adjacent location without constructing the list of all adjacencies
            List<Location> freeLocations = nextFieldState.getFreeAdjacentLocations(location);
            if (!freeLocations.isEmpty()) {
                nextLoc = freeLocations.removeFirst();
            }
        } else {
            // FIXME: What if the food isn't an Animal (maybe a Plant?)
            Animal food = (Animal) currentField.getActorAt(foodLoc);
            food.setDead();
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
     * Gives birth to more of the Animal as newborns
     * @param currentField the current state of the field
     * @param freeLocations the adjacent free locations we could place the children
     */
    protected abstract void giveBirth(Field currentField, List<Location> freeLocations);
}
    