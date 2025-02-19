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
    protected int getBreedingAge() { return 24*7; }
    // The age to which a Giraffe can live.
    protected int getMaxAge() { return 24*56; }
    // The likelihood of a Giraffe breeding.
    protected double getBreedingProbability() { return 0.08; }
    // The maximum number of births.
    protected int getMaxLitterSize() { return 2; }
    // The amount of "food" a giraffe gives when eaten
    protected int getFoodValue() { return 24*14; }

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

    @Override
    public void act(Field currentField, Field nextFieldState, Environment env) {
        decreaseHunger(1); // Food is always available: we can always eat
        super.act(currentField, nextFieldState, env);
    }

    @Override
    public String toString() {
        return "Giraffe{" +
                "age=" + getAge() +
                ", active=" + isActive() +
                ", location=" + getLocation() +
                '}';
    }

    protected Animal giveBirth(Location loc)
    {
        return new Giraffe(false, loc);
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
}