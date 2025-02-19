import java.util.List;
import java.util.Arrays;
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
    protected int getBreedingAge() { return 24; }
    // The age to which a Hyena can live.
    protected int getMaxAge() { return 24*7; }
    // The likelihood of a Hyena breeding.
    protected double getBreedingProbability() { return 0.05; }
    // The maximum number of births.
    protected int getMaxLitterSize() { return 2; }
    // The amount of "food" a hyena gives when eaten
    protected int getFoodValue() { return -1; } // Shouldn't be eaten

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
    }

    @Override
    protected void updateState(Environment env) {
        if (!isActive()) {
            setState(AnimalState.DEAD);
        } else if (getFoodLevel() < 24*2) {
            setState(AnimalState.EATING);
        } else if (!env.isNight()) {
            setState(AnimalState.SLEEPING);
        } else if (getFoodLevel() > 24*7 && canBreed()) {
            setState(AnimalState.BREEDING);
        }
        // Special case: Hyenas wake up at the end of the day
        if (env.isNight() && getState() == AnimalState.SLEEPING) {
            setState(AnimalState.EATING);
        }
    }

    @Override
    public String toString() {
        return "Hyena{" +
                "age=" + getAge() +
                ", active=" + isActive() +
                ", location=" + getLocation() +
                ", foodLevel=" + getFoodLevel() +
                '}';
    }

    protected Animal giveBirth(Location loc)
    {
        return new Hyena(false, loc);
    }
    
    /**
     * Check if this animal can eat the given actor
     * @param actor the actor to check
     * @return true if this animal can eat `actor`
     */
    @Override
    protected boolean canEat(Actor actor){
        return actor instanceof Zebra;
    }
}
