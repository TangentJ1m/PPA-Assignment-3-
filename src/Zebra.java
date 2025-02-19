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
    protected int getBreedingAge() { return 24*6; }
    // The age to which a Zebra can live.
    protected int getMaxAge() { return 24*14; }
    // The likelihood of a Zebra breeding.
    protected double getBreedingProbability() { return 0.10; }
    // The maximum number of births.
    protected int getMaxLitterSize() { return 4; }
    // How much "food" a zebra gives when eaten
    protected int getFoodValue() { return 24*14; }

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
    public void act(Field currentField, Field nextFieldState, Environment env) {
        decreaseHunger(1); // Food is always available: we can always eat
        super.act(currentField, nextFieldState, env);
    }

    @Override
    protected void updateState(Environment env) {
        if (!isActive()) {
            setState(AnimalState.DEAD);
        } else if (env.isNight()) {
            setState(AnimalState.SLEEPING);
        } else if (canBreed()) {
            setState(AnimalState.BREEDING);
        } else {
            setState(AnimalState.WANDERING);
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
    
    protected Animal giveBirth(Location loc)
    {
        return new Zebra(false, loc);
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