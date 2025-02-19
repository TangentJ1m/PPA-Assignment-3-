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
    protected int getBreedingAge() { return 24*3; }
    // The age to which a Lion can live.
    protected int getMaxAge() { return 24*21; }
    // The likelihood of a Lion breeding.
    protected double getBreedingProbability() { return 0.04; }
    // The maximum number of births.
    protected int getMaxLitterSize() { return 2; }
    // How much "food" a lion gives when eaten
    protected int getFoodValue() { return -1; } // Shouldn't be eaten

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
    }

    @Override
    public String toString() {
        return "Lion{" +
                "age=" + getAge() +
                ", active=" + isActive() +
                ", location=" + getLocation() +
                ", foodLevel=" + getFoodLevel() +
                '}';
    }
    
    protected Animal giveBirth(Location loc)
    {
        return new Lion(false, loc);
    }

    /**
     * Check if this animal can eat the given actor
     * @param actor the actor to check
     * @return true if this animal can eat `actor`
     */
    @Override
    protected boolean canEat(Actor actor)
    {
        return actor instanceof Giraffe || actor instanceof Zebra;
    }

    @Override
    protected void updateState(Environment env) {
        if (!isActive()) {
            setState(AnimalState.DEAD);
        } else if (getFoodLevel() < 24*2) {
            setState(AnimalState.EATING);
        } else if (env.isNight()) {
            setState(AnimalState.SLEEPING);
        } else if (getFoodLevel() > 24*7 && canBreed()) {
            setState(AnimalState.BREEDING);
        }
        // Special case: Lions wake up at the end of the night
        if (!env.isNight() && getState() == AnimalState.SLEEPING) {
            setState(AnimalState.EATING);
        }
    }
}