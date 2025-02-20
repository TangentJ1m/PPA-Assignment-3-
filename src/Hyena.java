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
    protected int getMaxAge() { return 24*30; }
    // The likelihood of a Hyena breeding.
    protected double getBreedingProbability() { return 0.06; }
    // The maximum number of births.
    protected int getMaxLitterSize() { return 2; }

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
        } else if (shouldHunt(env)) {
            setState(AnimalState.EATING);
        } else if (shouldSleep(env)) {
            setState(AnimalState.SLEEPING);
        } else if (shouldBreed()) {
            setState(AnimalState.BREEDING);
        }
        // Special case: Hyenas wake up at the end of the day
        if (shouldWakeUp(env)) {
            setState(AnimalState.EATING);
        }
    }

    /**
     * Hunts more in foggy or cloudy unless in heavy rain
     * @return true if should hunt
     */
    private boolean shouldHunt(Environment env){
        Weather weather = env.getWeather();
        return (weather == Weather.FOGGY || weather == Weather.CLOUDY) &&
                weather != Weather.CLOUDY && weather != Weather.RAINY;
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
    
    /**
     * Wakes up when night ends unless it's bad weather
     * @return true if should wake up 
     */
    private boolean shouldWakeUp(Environment env){
        return !env.isNight() && getState() == AnimalState.SLEEPING && 
           env.getWeather() != Weather.RAINY &&
           env.getWeather() != Weather.STORMY;
    }
    
    /**
     * Breeds if full and conditions are good
     * @return true if should eat
     */
    private boolean shouldBreed(){
        return getFoodLevel() > 24 && canBreed();
    }
    
    /**
     * Sleep if it's night or raining
     * @return true is should sleep 
     */
    private boolean shouldSleep(Environment env){
        return env.isDay() || env.getWeather() == Weather.RAINY;
    }
}
